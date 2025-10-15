package com.example.spendless.features.finance.presentation.ui.screens.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendless.core.domain.auth.SessionStorage
import com.example.spendless.core.domain.util.Result
import com.example.spendless.features.auth.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.spendless.R
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.features.auth.data.model.CounterPerTimeUnit
import kotlinx.coroutines.delay
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

sealed interface SecurityEvents {
    data object NavigateBack : SecurityEvents
    data class ShowToast(val showText: UiText): SecurityEvents
}

sealed interface SecurityActions {
    data object NavigateBack : SecurityActions
    data class UpdateBiometric(val biometric: Int) : SecurityActions
    data class UpdateExpiryDuration(val counterPerTimeUnit: CounterPerTimeUnit) : SecurityActions
    data class UpdateLockedDuration(val counterPerTimeUnit: CounterPerTimeUnit) : SecurityActions
    data object OnSave: SecurityActions
}

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionStorage: SessionStorage,
) : ViewModel() {
    private val _state = MutableStateFlow(SecurityUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<SecurityEvents>()
    val events = _events.receiveAsFlow()

    init {
        Timber.tag("MyTag").d("started")
        getUserSecurity()
    }

    fun onActions(securityActions: SecurityActions) {
        when (securityActions) {
            SecurityActions.NavigateBack -> navigateBack()
            SecurityActions.OnSave -> onSave()
            is SecurityActions.UpdateBiometric -> updateBiometric(securityActions.biometric)
            is SecurityActions.UpdateExpiryDuration -> updateExpiryDuration(securityActions.counterPerTimeUnit)
            is SecurityActions.UpdateLockedDuration -> updateLockedDuration(securityActions.counterPerTimeUnit)
        }
    }

    private fun getUserSecurity() {
        viewModelScope.launch {
            val username = sessionStorage.getAuthInfo()?.username ?: ""
            val result = userRepository.getSecurityByUsername(username)
            if (result is Result.Success) {
                _state.update { newState ->
                    newState.copy(
                        username = username,
                        security = result.data,
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun updateBiometric(biometric: Int) {
        _state.update { newState ->
            newState.copy(
                security = newState.security.copy(
                    withBiometric = biometric == R.string.enable
                )
            )
        }
    }

    private fun updateExpiryDuration(counterPerTimeUnit: CounterPerTimeUnit) {
        Timber.tag("MyTag").d("updateExpiryDuration: $counterPerTimeUnit")
        _state.update { newState ->
            newState.copy(
                security = newState.security.copy(
                    sessionExpiry = counterPerTimeUnit
                )
            )
        }
    }

    private fun updateLockedDuration(counterPerTimeUnit: CounterPerTimeUnit) {
        Timber.tag("MyTag").d("updateLockedDuration: $counterPerTimeUnit")
        _state.update { newState ->
            newState.copy(
                security = newState.security.copy(
                    lockedOutDuration = counterPerTimeUnit
                )
            )
        }
    }

    private fun onSave(){
        _state.update { newState->
            newState.copy(
                isButtonLoading = true,
                isButtonsEnabled = false,
            )
        }
        viewModelScope.launch {
            //show loader only
            delay(1.seconds)
            val currentState = _state.value
            val username = currentState.username
            val security = currentState.security
            val result = userRepository.updateSecurity(
                username = username,
                security = security
            )
            if(result is Result.Success){
                _events.send(SecurityEvents.ShowToast(UiText.StringResource(R.string.successfully_saved)))
                navigateBack()
            }
            _state.update { newState->
                newState.copy(
                    isButtonLoading = false,
                    isButtonsEnabled = true,
                )
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(SecurityEvents.NavigateBack)
        }
    }
}