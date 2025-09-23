package com.example.spendless.features.auth.presentation.ui.screens.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendless.R
import com.example.spendless.features.auth.domain.UserRepository
import com.example.spendless.core.domain.util.Result
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.features.auth.domain.PatternValidator
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

sealed interface RegisterEvents {
    data object NavigateToLogIn : RegisterEvents
    data class NavigateToCreatePin(val username: String) : RegisterEvents
}

sealed interface RegisterActions {
    data class UpdateUsername(val username: String) : RegisterActions
    data object ClickNext : RegisterActions
    data object ClickAlreadyHaveAnAccount : RegisterActions
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<RegisterEvents>()
    val events = _events.receiveAsFlow()

    private var job: Job? = null

    fun onActions(registerActions: RegisterActions) {
        when (registerActions) {
            RegisterActions.ClickNext -> clickNext()
            RegisterActions.ClickAlreadyHaveAnAccount -> clickAlreadyHaveAccount()

            is RegisterActions.UpdateUsername -> updateUsername(newUsername = registerActions.username)
        }
    }

    private fun updateUsername(newUsername: String) {
        //username valid
        val isUsernameValid = PatternValidator.isUsernameValid(newUsername)
        //username error will only show in ui when username is not valid
        val usernameError = PatternValidator.getUsernameError(username = newUsername)
        _state.update { newState ->
            newState.copy(
                isNextEnabled = true,
                username = newUsername,
                isUsernameError = (!isUsernameValid && !newUsername.isEmpty()),
                usernameError = usernameError,
            )
        }
    }

    private fun clickNext() {
        viewModelScope.launch {
            _state.update { newState->
                newState.copy(
                    isNextLoading = true
                )
            }
            val username = _state.value.username
            val doesUserExist = userRepository.doesUserExist(username)

            when (doesUserExist) {
                is Result.Error -> {
                    _state.update { newState->
                        newState.copy(
                            isNextLoading = false
                        )
                    }
                    Timber.tag("MyTag").e("error: ${doesUserExist.error}")
                }
                is Result.Success -> {
                    Timber.tag("MyTag").d("data: ${doesUserExist.data}")
                    //if user exists
                    if (doesUserExist.data) {
                        //disable next button
                        _state.update { newState ->
                            newState.copy(
                                isNextEnabled = false,
                                isNextLoading = false
                            )
                        }
                        //if user exists show error banner
                        showBanner()
                    } else {
                        _state.update { newState->
                            newState.copy(
                                isNextLoading = false
                            )
                        }
                        //else navigate to create pin
                        _events.send(RegisterEvents.NavigateToCreatePin(username = username))
                    }
                }
            }
        }
    }

    private fun clickAlreadyHaveAccount() {
        viewModelScope.launch {
            _events.send(RegisterEvents.NavigateToLogIn)
        }
    }

    private fun showBanner() {
        job?.cancel()
        job = viewModelScope.launch {
            _state.update { newState ->
                newState.copy(
                    bannerText = UiText.StringResource(R.string.username_already_exists)
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