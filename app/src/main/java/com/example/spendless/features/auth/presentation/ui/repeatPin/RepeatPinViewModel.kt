package com.example.spendless.features.auth.presentation.ui.repeatPin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendless.R
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.features.auth.presentation.designsystem.Constants.DELETE_CHAR
import com.example.spendless.features.auth.presentation.ui.common.PinActions
import com.example.spendless.features.auth.presentation.ui.common.PinEvents
import com.example.spendless.features.auth.presentation.ui.common.PinEvents.RepeatPinEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class RepeatPinViewModel @Inject constructor(
    private val saveHandleStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(RepeatPinUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<PinEvents>()
    val events = _events.receiveAsFlow()

    private var job: Job? = null

    init {
        saveUsernameAndPin()
    }

    private fun saveUsernameAndPin() {
        val username = saveHandleStateHandle.get<String>("username") ?: ""
        val pin = saveHandleStateHandle.get<String>("pin") ?: ""
        _state.update { newState ->
            newState.copy(
                username = username,
                pin = pin
            )
        }
    }

    fun onActions(pinActions: PinActions) {
        when (pinActions) {
            is PinActions.UpdatePin -> updatePin(newPin = pinActions.newPin)
            PinActions.NavigateBack -> navigateBack()
            else -> {}
        }
    }

    private fun updatePin(newPin: String) {
        val currentPin = _state.value.repeatPin
        val repeatPin = when {
            newPin == DELETE_CHAR -> currentPin.dropLast(1)
            currentPin.length < 5 -> currentPin + newPin
            else -> currentPin
        }

        _state.update { newState ->
            newState.copy(
                repeatPin = repeatPin
            )
        }
        if (repeatPin.length == 5) {
            val username = _state.value.username
            //pin sent from createPin screen
            val pin = _state.value.pin
            viewModelScope.launch {
                if (repeatPin == pin) {
                    _events.send(
                        RepeatPinEvents.NavigateToOnBoarding(
                            username = username,
                            pin = pin
                        )
                    )
                } else {
                    showBanner()
                }
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(PinEvents.NavigateBack)
        }
    }

    private fun showBanner() {
        job?.cancel()
        job = viewModelScope.launch {
            _state.update { newState ->
                newState.copy(
                    bannerText = UiText.StringResource(R.string.pins_dont_match_try_again),
                    repeatPin = ""
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

}