package com.example.spendless.features.finance.presentation.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendless.core.domain.auth.SessionStorage
import com.example.spendless.core.domain.util.DataError
import com.example.spendless.core.domain.util.Result
import com.example.spendless.features.auth.domain.UserRepository
import com.example.spendless.features.finance.data.model.TransactionItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface DashboardEvents {
    data object NavigateToExportData : DashboardEvents
    data object NavigateToSettings : DashboardEvents
}

sealed interface DashboardActions {
    data object NavigateToExportData : DashboardActions
    data object NavigateToSettings : DashboardActions
    data class SelectedTransaction(val selectedTransactionItem: TransactionItem): DashboardActions
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionStorage: SessionStorage,
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardUiState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = DashboardUiState()
    ).onStart {
        viewModelScope.launch {
            //getPreferencesFormat() & getTotal() depends on setUsername()
            //so they have to run sequentially with setUsername()
            //while getPreferencesFormat() & getTotal() doesn't depends on each other
            //so they can run concurrently(in parallel)
            async {
                setUsername()
            }.await()
            launch {
                getPreferencesFormat()
            }
            getTotal()
        }
    }


    private val _events = Channel<DashboardEvents>()
    val events = _events.receiveAsFlow()

    private suspend fun setUsername() {
        val username = sessionStorage.getAuthInfo()?.username ?: ""
        _state.update { newState ->
            newState.copy(username)
        }
    }

    fun onActions(dashboardActions: DashboardActions) {
        when (dashboardActions) {
            DashboardActions.NavigateToExportData -> navigateToExportData()
            DashboardActions.NavigateToSettings -> navigateToSettings()
            is DashboardActions.SelectedTransaction -> setSelectedTransaction(dashboardActions.selectedTransactionItem)
        }
    }

    private suspend fun getTotal() {
        val username = _state.value.username

        val result = userRepository.getTotalByUsername(username)
        when (result) {
            is Result.Error -> {
                if (result.error is DataError.Local.Unknown) {
                    Timber.tag("MyTag").e("getTotal: ${result.error.unknownError}")
                } else {
                    Timber.tag("MyTag").e("getTotal: ${result.error}")
                }
            }

            is Result.Success -> {
                _state.update { newState ->
                    newState.copy(
                        total = result.data
                    )
                }
            }
        }
    }

    private suspend fun getPreferencesFormat() {
        val username = _state.value.username
        val result = userRepository.getPreferencesByUsername(username)
        when (result) {
            is Result.Error -> {
                if (result.error is DataError.Local.Unknown) {
                    Timber.tag("MyTag").e("getPreferencesFormat: ${result.error.unknownError}")
                } else {
                    Timber.tag("MyTag").e("getPreferencesFormat: ${result.error}")
                }
            }

            is Result.Success -> {
                _state.update { newState ->
                    newState.copy(
                        preferencesFormat = result.data
                    )
                }
            }
        }
    }

    private fun setSelectedTransaction(selectedTransaction: TransactionItem){
        _state.update { newState->
            newState.copy(
                selectedTransaction = selectedTransaction
            )
        }
    }

    private fun navigateToSettings() {
        viewModelScope.launch {
            _events.send(DashboardEvents.NavigateToSettings)
        }
    }

    private fun navigateToExportData() {
        viewModelScope.launch {
            _events.send(DashboardEvents.NavigateToExportData)
        }
    }

}