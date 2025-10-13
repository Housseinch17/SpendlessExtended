package com.example.spendless.features.auth.presentation.ui.screens.logIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendless.R
import com.example.spendless.core.domain.auth.AuthInfo
import com.example.spendless.core.domain.auth.SessionStorage
import com.example.spendless.core.domain.util.DataError
import com.example.spendless.core.domain.util.Result
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.core.domain.PatternValidator
import com.example.spendless.features.auth.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

sealed interface LogInEvents {
    data object NavigateToRegister : LogInEvents
    data class NavigateToDashboard(val username: String) : LogInEvents
}

sealed interface LogInActions {
    data object NavigateToRegister : LogInActions
    data object LogIn : LogInActions
    data class UpdateUsername(val username: String) : LogInActions
    data class UpdatePin(val pin: String) : LogInActions
}

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionStorage: SessionStorage,
) : ViewModel() {
    private val _state = MutableStateFlow(LogInUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<LogInEvents>()
    val events = _events.receiveAsFlow()

    private var job: Job? = null

    fun onActions(logInActions: LogInActions) {
        when (logInActions) {
            LogInActions.NavigateToRegister -> navigateToRegister()
            LogInActions.LogIn -> logIn()
            is LogInActions.UpdatePin -> updatePin(pin = logInActions.pin)
            is LogInActions.UpdateUsername -> updateUsername(username = logInActions.username)
        }
    }


    private fun updateUsername(username: String) {
        val isUsernameValid = PatternValidator.isUsernameValid(username)
        //username error will only show in ui when username is not valid
        val usernameError = PatternValidator.getUsernameError(username = username)
        _state.update { newState ->
            newState.copy(
                username = username,
                isUsernameError = (!isUsernameValid && !username.isEmpty()),
                usernameError = usernameError,
            )
        }
    }

    private fun updatePin(pin: String) {
        val isPinValid = PatternValidator.isPinValid(pin = pin)
        //pin error will only show in ui when pin is not valid
        val pinError = PatternValidator.getPinError(pin = pin)
        _state.update { newState ->
            newState.copy(
                pin = pin,
                isPinError = (!isPinValid && !pin.isEmpty()),
                pinError = pinError
            )
        }
    }

    private fun logIn() {
        _state.update { newState ->
            newState.copy(
                isLogInButtonLoading = true
            )
        }
        //login will only be enabled if username and pin are valid
        val state = _state.value
        val username = state.username
        val pin = state.pin

        viewModelScope.launch {
            val doesUserExist = userRepository.doesUserExist(username)
            when (doesUserExist) {
                is Result.Error -> {
                    Timber.tag("MyTag").e("logIn: error: ${doesUserExist.error}")
                    _state.update { newState ->
                        newState.copy(
                            isLogInButtonLoading = false
                        )
                    }
                    showBanner(UiText.DynamicString(value = doesUserExist.error.toString()))
                }

                is Result.Success -> {
                    if (doesUserExist.data) {
                        //show circular progress indicator for 1 seconds
                        delay(1.seconds)
                        _state.update { newState ->
                            newState.copy(
                                isLogInButtonLoading = false
                            )
                        }

//                        //run concurrently(in parallel)
//                        val (security, preferencesFormat) = coroutineScope {
//                            val securityDeferred =
//                                async { userRepository.getSecurityByUsername(username) }
//                            val preferencesDeferred =
//                                async { userRepository.getPreferencesByUsername(username) }
//
//                            // Wait for both in parallel
//                            Pair(securityDeferred.await(), preferencesDeferred.await())
//                        }
//                        if (security is Result.Success && preferencesFormat is Result.Success) {
                            sessionStorage.setAuthInfo(
                                authInfo = AuthInfo(
                                    username = username,
//                                    security = security.data,
//                                    preferencesFormat = preferencesFormat.data
                                )
                            )
                            verifyPin(username = username, pin = pin) {
                                _events.send(LogInEvents.NavigateToDashboard(username = username))
                            }
                    } else {
                        _state.update { newState ->
                            newState.copy(
                                isLogInButtonLoading = false
                            )
                        }
                        showBanner(bannerText = UiText.StringResource(R.string.username_doesnt_exist))
                    }
                }
            }
        }
    }

    private fun verifyPin(username: String, pin: String, navigateToDashboard: suspend () -> Unit) {
        viewModelScope.launch {
            val pinByUsername = userRepository.getPinByUsername(username = username)
            when (pinByUsername) {
                is Result.Error -> {
                    if (pinByUsername.error is DataError.Local.Unknown) {
                        Timber.tag("MyTag")
                            .e("verifyPin: error: ${pinByUsername.error.unknownError}")
                        showBanner(bannerText = UiText.DynamicString(pinByUsername.error.unknownError))
                    } else {
                        Timber.tag("MyTag").e("verifyPin: error: ${pinByUsername.error}")
                        showBanner(bannerText = UiText.DynamicString(pinByUsername.error.toString()))
                    }
                }

                is Result.Success -> {
                    if (pinByUsername.data == pin) {
                        navigateToDashboard()
                    } else {
                        showBanner(bannerText = UiText.StringResource(R.string.incorrect_pin))
                    }
                }
            }
        }
    }

    private fun navigateToRegister() {
        viewModelScope.launch {
            _events.send(LogInEvents.NavigateToRegister)
        }
    }

    private fun showBanner(bannerText: UiText) {
        job?.cancel()
        job = viewModelScope.launch {
            _state.update { newState ->
                newState.copy(
                    bannerText = bannerText
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