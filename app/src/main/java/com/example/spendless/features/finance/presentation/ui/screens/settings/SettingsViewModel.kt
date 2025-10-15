package com.example.spendless.features.finance.presentation.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendless.core.domain.auth.SessionStorage
import com.example.spendless.core.domain.util.Result
import com.example.spendless.features.finance.domain.SessionExpiryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SettingsEvents {
    data object NavigateBack : SettingsEvents
    data object NavigateToPreferences : SettingsEvents
    data object NavigateToSecurity : SettingsEvents
    data object LogOut : SettingsEvents
    data object PromptPin : SettingsEvents

}

sealed interface SettingsActions {
    data object NavigateBack : SettingsActions
    data object NavigateToPreferences : SettingsActions
    data object NavigateToSecurity : SettingsActions
    data object LogOut : SettingsActions
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionStorage: SessionStorage,
    private val sessionExpiryUseCase: SessionExpiryUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<SettingsEvents>()
    val events = _events.receiveAsFlow()

    fun onActions(settingsActions: SettingsActions) {
        when (settingsActions) {
            SettingsActions.NavigateBack -> navigateBack()
            SettingsActions.LogOut -> logOut()
            SettingsActions.NavigateToPreferences -> navigateToPreferences()
            SettingsActions.NavigateToSecurity -> navigateToSecurity()
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(SettingsEvents.NavigateBack)
        }
    }

    private fun navigateToPreferences() {
        viewModelScope.launch {
            val isExpiry = isExpiry()
            if (isExpiry) {
                promptPin()
                return@launch
            }
            _events.send(SettingsEvents.NavigateToPreferences)
        }
    }

    private fun navigateToSecurity() {
        viewModelScope.launch {
            val isExpiry = isExpiry()
            if (isExpiry) {
                promptPin()
                return@launch
            }
            _events.send(SettingsEvents.NavigateToSecurity)
        }
    }


    private fun logOut() {
        viewModelScope.launch {
            _state.update { newState ->
                newState.copy(
                    isLoading = true,
                    isButtonsEnabled = false
                )
            }
            val result = sessionStorage.clearAuthInfo()
            when (result) {
                is Result.Error -> {
                    _state.update { newState ->
                        newState.copy(
                            isButtonsEnabled = true,
                            isLoading = false
                        )
                    }
                }

                is Result.Success -> {
                    //keep button disabled because settings screen will be cleared
                    //so when navigating to it again the state will reset
                    _state.update { newState ->
                        newState.copy(
                            isLoading = false,
                        )
                    }
                    _events.send(SettingsEvents.LogOut)
                }
            }
        }
    }

    private fun promptPin() {
        viewModelScope.launch {
            _events.send(SettingsEvents.PromptPin)
        }
    }

    private suspend fun isExpiry(): Boolean {
            val isExpired = sessionExpiryUseCase.invoke()
            return isExpired
    }
}