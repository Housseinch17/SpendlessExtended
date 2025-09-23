package com.example.spendless.features.auth.presentation.ui.screens.pinPrompt

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendless.R
import com.example.spendless.core.domain.auth.SessionStorage
import com.example.spendless.core.domain.util.DataError
import com.example.spendless.core.domain.util.Result
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.core.presentation.ui.formatCounter
import com.example.spendless.features.auth.data.model.CounterPerTimeUnit
import com.example.spendless.features.auth.domain.BiometricRepository
import com.example.spendless.features.auth.domain.UserRepository
import com.example.spendless.features.auth.presentation.designsystem.Constants.DELETE_CHAR
import com.example.spendless.features.auth.presentation.designsystem.Constants.FINGERPRINT
import com.example.spendless.features.auth.presentation.ui.common.PinActions
import com.example.spendless.features.auth.presentation.ui.common.PinActions.PromptPinActions
import com.example.spendless.features.auth.presentation.ui.common.PinEvents
import com.example.spendless.features.auth.presentation.ui.common.PinEvents.PinPromptEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class PinPromptViewModel @Inject constructor(
    private val biometricRepository: BiometricRepository,
    private val sessionStorage: SessionStorage,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(PinPromptUIState())
    val state = _state.asStateFlow()

    private val _events = Channel<PinEvents>()
    val events = _events.receiveAsFlow()

    private var job: Job? = null

    private var counter: Job? = null

    init {
        setUsername()
    }

    fun onActions(pinActions: PinActions) {
        when (pinActions) {
            PromptPinActions.LogOut -> logOut()

            is PinActions.UpdatePin -> {
                updatePin(
                    newPin = pinActions.newPin,
                    activity = pinActions.activity
                )
            }

            else -> {}
        }
    }

    private fun setUsername(){
        viewModelScope.launch {
            val user = sessionStorage.getAuthInfo()
            val username = user?.username ?: ""
            val counterPerTimeUnit = user?.security?.lockedOutDuration ?: CounterPerTimeUnit()

            val pinResult = userRepository.getPinByUsername(username)
            when(pinResult){
                is Result.Error -> {
                    if(pinResult.error is DataError.Local.Unknown){
                        showBanner(uiText = UiText.DynamicString(pinResult.error.unknownError))
                    }else{
                        showBanner(uiText = UiText.DynamicString(pinResult.error.toString()))
                    }
                }
                is Result.Success -> {
                    Timber.tag("MyTag").d("authInfo: $username")
                    _state.update { newState->
                        newState.copy(
                            username = username,
                            headerText = "$username !",
                            pin = pinResult.data,
                            counterPerTimeUnit = counterPerTimeUnit
                        )
                    }
                }
            }
        }
    }


    private fun logOut() {
        _state.update { newState->
            newState.copy(
                enabledButtons = false,
            )
        }
        viewModelScope.launch {
            val result = sessionStorage.clearAuthInfo()
            when(result){

                is Result.Error -> {
                    if(result.error is DataError.Local.Unknown) {
                        showBanner(uiText = UiText.DynamicString(result.error.unknownError))
                    }
                    else{
                        showBanner(uiText = UiText.DynamicString(result.error.toString()))
                    }
                    _state.update { newState->
                        newState.copy(
                            enabledButtons = true
                        )
                    }
                }
                is Result.Success -> {
                    _state.update { newState->
                        newState.copy(
                            enabledButtons = true
                        )
                    }
                    _events.send(PinPromptEvents.NavigateToLogIn)
                }
            }

        }
    }

    private fun updatePin(
        newPin: String,
        activity: AppCompatActivity?
    ) {
        if (newPin != FINGERPRINT) {
            val currentPin = _state.value.pinPromptPin
            val pinPromptPin = when {
                newPin == DELETE_CHAR -> currentPin.dropLast(1)
                currentPin.length < 5 -> currentPin + newPin
                else -> currentPin
            }

            _state.update { newState ->
                newState.copy(
                    pinPromptPin = pinPromptPin
                )
            }

            if (pinPromptPin.length == 5) {
                //pin sent from createPin screen
                val pin = _state.value.pin
                viewModelScope.launch {
                    if (pinPromptPin == pin) {
                        //the action
                    } else {
                        incrementPinCounter()
                        showBanner(
                            uiText = UiText.StringResource(R.string.pins_dont_match_try_again)
                        )
                        if (_state.value.pinErrorCounter == 3) {
                            startCounter()
                        }
                    }
                }
            }
        } else {
            viewModelScope.launch {
                Timber.tag("MyTag").d("FingerPrint")
                val biometricResult = biometricRepository.showBiometricPrompt(
                    activity = activity!!,
                    title = UiText.StringResource(R.string.biometric_verification),
                    subtitle = UiText.StringResource(R.string.use_biometric_for_verification)
                )
                when (biometricResult) {
                    is PinEvents.BiometricResult.AuthenticationError -> {
                        Timber.tag("MyTag").e("AuthenticationError")
                        //biometric error, increment biometric counter
                        incrementBiometricCounter()
                        showBanner(
                            uiText = UiText.StringResource(
                                R.string.biometric_auth_error,
                                biometricResult.error
                            )
                        )
                        if (_state.value.biometricErrorCounter == 3) {
                            startCounter()
                        }
                    }

                    PinEvents.BiometricResult.AuthenticationFailed -> {
                        Timber.tag("MyTag").e("AuthenticationFailed")
                        //biometric error, increment biometric counter
                        incrementBiometricCounter()
                        showBanner(
                            uiText = UiText.StringResource(R.string.biometric_auth_failed)
                        )
                        if (_state.value.biometricErrorCounter == 3) {
                            startCounter()
                        }
                    }

                    PinEvents.BiometricResult.AuthenticationNotSet -> {
                        Timber.tag("MyTag").e("AuthenticationNotSet")
                        _events.send(PinEvents.BiometricResult.AuthenticationNotSet)
                    }

                    PinEvents.BiometricResult.AuthenticationSuccess -> {
                        Timber.tag("MyTag").d("AuthenticationSuccess")
                        _events.send(PinEvents.BiometricResult.AuthenticationSuccess)
                    }

                    PinEvents.BiometricResult.FeatureUnavailable -> {
                        Timber.tag("MyTag").e("FeatureUnavailable")
                        showBanner(
                            uiText = UiText.StringResource(R.string.biometric_error_no_hardware)
                        )
                    }

                    PinEvents.BiometricResult.HardwareUnavailable -> {
                        Timber.tag("MyTag").e("HardwareUnavailable")
                        showBanner(
                            uiText = UiText.StringResource(R.string.biometric_error_hw_unavailable)
                        )
                    }
                }
            }
        }
    }

    private fun incrementPinCounter() {
        _state.update { newState ->
            newState.copy(
                pinErrorCounter = newState.pinErrorCounter + 1
            )
        }
        Timber.tag("MyTag").d("counter: ${_state.value.pinErrorCounter}")
    }

    private fun incrementBiometricCounter() {
        _state.update { newState ->
            newState.copy(
                biometricErrorCounter = newState.biometricErrorCounter + 1
            )
        }
        Timber.tag("MyTag").d("counter: ${_state.value.biometricErrorCounter}")
    }


    private fun showBanner(
        uiText: UiText,
    ) {
        job?.cancel()
        job = viewModelScope.launch {
            _state.update { newState ->
                newState.copy(
                    bannerText = uiText,
                    pinPromptPin = ""
                )
            }
            //show banner for 2 seconds
            delay(2.seconds)
            _state.update { newState ->
                newState.copy(
                    bannerText = null
                )
            }
        }
    }

    private fun startCounter() {
        val oldHeaderText = _state.value.headerText
        val oldHeaderUiText = _state.value.headerUiText

        val newHeaderUiText = UiText.StringResource(R.string.too_many_failed_attempts)
        val newHeaderText = null
        _state.update { newState ->
            newState.copy(
                enabledButtons = false,
                headerUiText = newHeaderUiText,
                headerText = newHeaderText
            )
        }

        val counterByTimeUnit = _state.value.counterPerTimeUnit
        val totalSeconds =
            counterByTimeUnit.timeUnit.toSeconds(counterByTimeUnit.counter.toLong()).toInt()

        counter = viewModelScope.launch {
            for (seconds in totalSeconds downTo 0) {
                Timber.tag("MyTag").d("counter: $seconds")
                _state.update { newState ->
                    newState.copy(
                        counter = seconds.formatCounter()
                    )
                }
                //delay 1 seconds after showing the first counter
                delay(1.seconds)
            }

            _state.update { newState ->
                newState.copy(
                    enabledButtons = true, counter = null, pinErrorCounter = 0,
                    biometricErrorCounter = 0,
                    headerText = oldHeaderText,
                    headerUiText = oldHeaderUiText
                )
            }
        }
    }

}