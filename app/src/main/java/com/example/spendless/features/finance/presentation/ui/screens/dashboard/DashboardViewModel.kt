package com.example.spendless.features.finance.presentation.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendless.core.domain.auth.SessionStorage
import com.example.spendless.core.domain.util.DataError
import com.example.spendless.core.domain.util.Result
import com.example.spendless.core.presentation.ui.amountFormatter
import com.example.spendless.features.auth.domain.UserRepository
import com.example.spendless.features.finance.data.model.TransactionItem
import com.example.spendless.features.finance.domain.TransactionsRepository
import com.example.spendless.features.finance.presentation.ui.common.groupTransactionsByDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
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
    data object NavigateToTransactions : DashboardEvents
    data object NavigateToCreateTransactions : DashboardEvents
}

sealed interface DashboardActions {
    data object NavigateToExportData : DashboardActions
    data object NavigateToSettings : DashboardActions
    data class SelectedTransaction(val selectedTransactionItem: TransactionItem) : DashboardActions
    data object ShowAll : DashboardActions
    data class CreateNewTransaction(val showBottomBar: Boolean) : DashboardActions
    data class ShowFloatingActionButton(val isVisible: Boolean) : DashboardActions
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionStorage: SessionStorage,
    private val transactionsRepository: TransactionsRepository
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
            setUsername()
            launch {
                getPreferencesFormat()
            }.join()
            combineFlows()
        }
    }

    private val _events = Channel<DashboardEvents>()
    val events = _events.receiveAsFlow()


    private fun hideLoader() {
        _state.update { newState ->
            newState.copy(
                isLoading = false
            )
        }
    }

    private suspend fun setUsername() {
        Timber.tag("MyTag").d("setUsername")
        val username = sessionStorage.getAuthInfo()?.username ?: ""
        _state.update { newState ->
            newState.copy(username = username)
        }
        Timber.tag("MyTag").d("setUsername done")
    }

    private suspend fun getPreferencesFormat() {
        Timber.tag("MyTag").d("getPreferencesFormat")
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
        Timber.tag("MyTag").d("getPreferencesFormat finished")
    }

    private suspend fun combineFlows() {
        val total = transactionsRepository.getNetTotalForUser()
        val transactionsList = transactionsRepository.getAllTransactions()
        val largestTransaction = transactionsRepository.getLargestTransaction()
        combine(
            total,
            transactionsList,
            largestTransaction
        ) { total, transactionList, largestTransaction ->
            //format prices
            val uiState = DashboardUiState(
                total = total,
                largestTransaction = largestTransaction,
                listOfTransactions = transactionList,
            )
            val transactionMap = transactionList.map {
                it.copy(
                    price = amountFormatter(
                        total = it.price,
                        isExpense = it.isExpense,
                        preferencesFormat = _state.value.preferencesFormat
                    )
                )
            }
            //map the date with their lists for example 9/10/2024 should have a list with key 9/10/2024 and value
            //all the transactionItems at this date
            val groupTransactions = groupTransactionsByDate(transactions = transactionMap)

            Pair(uiState, groupTransactions)
        }.collect { (uiState, groupTransactions) ->
            _state.update { newState ->
                newState.copy(
                    total = uiState.total,
                    largestTransaction = uiState.largestTransaction,
                    listOfTransactions = uiState.listOfTransactions,
                    transactionsByDate = groupTransactions
                )
            }
            //here after each flow emit at least once we will hide the loader
            hideLoader()
        }
    }

    fun onActions(dashboardActions: DashboardActions) {
        when (dashboardActions) {
            DashboardActions.NavigateToExportData -> navigateToExportData()
            DashboardActions.NavigateToSettings -> navigateToSettings()
            DashboardActions.ShowAll -> navigateToTransactions()
            is DashboardActions.SelectedTransaction -> setSelectedTransaction(dashboardActions.selectedTransactionItem)
            is DashboardActions.CreateNewTransaction -> navigateToCreateTransactions()
            is DashboardActions.ShowFloatingActionButton -> showFloatingActionButton(
                dashboardActions.isVisible
            )
        }
    }

    private fun showFloatingActionButton(isVisible: Boolean) {
        _state.update { newState ->
            newState.copy(isFloatingActionButtonVisible = isVisible)
        }
    }

    private fun setSelectedTransaction(selectedTransaction: TransactionItem) {
        val alreadySelectedTransaction = _state.value.selectedTransaction == selectedTransaction
        _state.update { newState ->
            newState.copy(
                selectedTransaction = if (alreadySelectedTransaction) TransactionItem() else selectedTransaction
            )
        }
    }

    private fun navigateToCreateTransactions() {
        viewModelScope.launch {
            _events.send(DashboardEvents.NavigateToCreateTransactions)
        }
    }

    private fun navigateToTransactions() {
        viewModelScope.launch {
            _events.send((DashboardEvents.NavigateToTransactions))
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