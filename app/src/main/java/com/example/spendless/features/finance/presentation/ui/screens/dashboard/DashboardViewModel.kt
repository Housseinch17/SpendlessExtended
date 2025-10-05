package com.example.spendless.features.finance.presentation.ui.screens.dashboard

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendless.R
import com.example.spendless.core.data.model.Category
import com.example.spendless.core.domain.PatternValidator
import com.example.spendless.core.domain.auth.SessionStorage
import com.example.spendless.core.domain.util.Result
import com.example.spendless.core.presentation.ui.amountFormatter
import com.example.spendless.features.auth.domain.UserRepository
import com.example.spendless.features.finance.data.model.PaymentRecurrence
import com.example.spendless.features.finance.data.model.TransactionItem
import com.example.spendless.features.finance.domain.TransactionsRepository
import com.example.spendless.features.finance.presentation.ui.common.SharedActions
import com.example.spendless.features.finance.presentation.ui.common.SharedActions.DashboardActions
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
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber
import javax.inject.Inject

sealed interface DashboardEvents {
    data object NavigateToExportData : DashboardEvents
    data object NavigateToSettings : DashboardEvents
    data object NavigateToTransactions : DashboardEvents
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
            try {
                //they should work sequentially because getPreferencesFormat() depends on username from setUsername()
                //and combineFlows() depends on preferencesFormat from getPreferencesFormat()
                setUsername()
                setPreferencesFormat()
                combineFlows()
            } catch (_: Exception) {
                hideLoader()
            }
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
        val username = sessionStorage.getAuthInfo()?.username ?: ""
        _state.update { newState ->
            newState.copy(username = username)
        }
    }

    private suspend fun combineFlows() {
        val total = transactionsRepository.getNetTotalForUser()
        val transactionsList = transactionsRepository.getTransactionsForTodayAndYesterday()
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

    fun onActions(dashboardActions: SharedActions) {
        when (dashboardActions) {
            DashboardActions.NavigateToExportData -> navigateToExportData()
            DashboardActions.NavigateToSettings -> navigateToSettings()
            DashboardActions.ShowAll -> navigateToTransactions()
            is DashboardActions.SelectedTransaction -> setSelectedTransaction(dashboardActions.selectedTransactionItem)
            is DashboardActions.ShowFloatingActionButton -> showFloatingActionButton(
                dashboardActions.isVisible
            )

            SharedActions.DismissBottomSheet -> dismissBottomSheet()
            SharedActions.ShowBottomBar -> showBottomBar()
            SharedActions.OnCreateClick -> onCreateClick()

            is SharedActions.SelectedTransaction -> setSelectedTransaction(dashboardActions.selectedTransactionItem)
            is SharedActions.UpdateExpense -> updateExpense(dashboardActions.isExpense)
            is SharedActions.UpdateTextFieldValue -> updateTextFieldValue(dashboardActions.textFieldValue)
            is SharedActions.UpdateAmountTextFieldValue -> updateAmountTextFieldValue(
                dashboardActions.amountTextFieldValue
            )

            is SharedActions.UpdateNote -> updateNote(note = dashboardActions.noteValue)
            is SharedActions.UpdateSelectedCategory -> updateSelectedCategory(
                dashboardActions.category
            )

            is SharedActions.UpdateDropDownCategoryExpand -> updateDropDownCategoryExpand(
                dashboardActions.isExpand
            )

            is SharedActions.UpdateSelectedPaymentRecurrence -> updateSelectedPaymentRecurrence(
                dashboardActions.paymentRecurrence
            )

            is SharedActions.UpdateDropDownPaymentRecurrenceExpand -> updateDropDownPaymentRecurrenceExpand(
                dashboardActions.isExpand
            )

            is SharedActions.ShowFloatingActionButton -> showFloatingActionButton(
                dashboardActions.isVisible
            )

            else -> {}
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

    //shared actions for bottom sheet
    private fun showFloatingActionButton(isVisible: Boolean) {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(
                    isFloatingActionButtonVisible = isVisible
                )
            )
        }
    }

    private fun onCreateClick() {
        viewModelScope.launch {
            onCreateLoading(true)
            val timeNow = Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
            val state = _state.value
            val transactionItem = state.bottomSheetUiState.selectedTransactionItem.copy(
                category = state.bottomSheetUiState.selectedCategory,
                title = state.bottomSheetUiState.textFieldValue,
                isExpense = state.bottomSheetUiState.isExpense,
                description = if (state.bottomSheetUiState.isExpense) state.bottomSheetUiState.selectedCategory.categoryName.categoryRes else R.string.income,
                price = state.bottomSheetUiState.amountTextFieldValue.text,
                date = timeNow.toString(),
                content = state.bottomSheetUiState.noteValue,
                image = if (state.bottomSheetUiState.isExpense) state.bottomSheetUiState.selectedCategory.image else R.drawable.accessories
            )
            val result = transactionsRepository.insertTransaction(transactionItem)
            when (result) {
                is Result.Error -> Timber.tag("MyTag").d("onCreateClick error: ${result.error}")
                is Result.Success -> {
                    _state.update { newState ->
                        newState.copy(
                            bottomSheetUiState = newState.bottomSheetUiState.copy(showBottomSheet = false)
                        )
                    }
                    resetTextFields()
                    onCreateLoading(false)
                    dismissBottomSheet()
                }
            }
        }
    }

    private fun onCreateLoading(onCreateLoading: Boolean) {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(
                    isOnCreateLoading = onCreateLoading
                )
            )
        }
    }

    private fun updateDropDownPaymentRecurrenceExpand(isExpand: Boolean) {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(
                    isDropDownPaymentRecurrenceExpand = isExpand
                )
            )
        }
    }

    private fun updateSelectedPaymentRecurrence(paymentRecurrence: PaymentRecurrence) {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(selectedPaymentRecurrence = paymentRecurrence)
            )
        }
    }

    private fun updateDropDownCategoryExpand(isExpand: Boolean) {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(isDropDownCategoryExpand = isExpand)
            )
        }
    }

    private fun updateSelectedCategory(selectedCategory: Category) {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(selectedCategory = selectedCategory)
            )
        }
    }

    private fun updateNote(note: String) {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(noteValue = note)
            )
        }
    }

    private fun updateAmountTextFieldValue(amountTextFieldValue: TextFieldValue) {
        if (!(amountTextFieldValue.text.startsWith("0"))) {
            //to not use the format and only digits and only 8 length
            val digitsOnly = amountTextFieldValue.text.filter { it.isDigit() }.take(8)
            //set textField cursor to the end
            val textFieldValue =
                TextFieldValue(text = digitsOnly, selection = TextRange(digitsOnly.length))
            _state.update { newState ->
                newState.copy(
                    bottomSheetUiState = newState.bottomSheetUiState.copy(
                        amountTextFieldValue = textFieldValue
                    )
                )
            }
        }
    }

    private fun updateTextFieldValue(textFieldValue: String) {
        val isTextFieldValid = PatternValidator.isUsernameValid(textFieldValue)
        //username error will only show in ui when username is not valid
        val textFieldError = PatternValidator.getUsernameError(username = textFieldValue)
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(
                    textFieldValue = textFieldValue,
                    isTextFieldError = (!isTextFieldValid && !textFieldValue.isEmpty()),
                    textFieldError = textFieldError,
                )
            )
        }
    }

    private fun setPreferencesFormat() {
        viewModelScope.launch {
            val username = sessionStorage.getAuthInfo()!!.username
            val result = userRepository.getPreferencesByUsername(username)
            if (result is Result.Success) {
                val preferences = result.data
                transactionsRepository.getTotalSpentPreviousWeek(preferences)
                    .collect { totalSpentPreviousWeek ->
                        _state.update { newState ->
                            newState.copy(
                                bottomSheetUiState = newState.bottomSheetUiState.copy(
                                    preferencesFormat = preferences,
                                ),
                                preferencesFormat = preferences,
                                previousWeekSpent = totalSpentPreviousWeek
                            )
                        }
                    }
            }
        }
    }

    private fun showBottomBar() {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(showBottomSheet = true)
            )
        }
    }

    private fun updateExpense(isExpense: Boolean) {
        _state.update { newState ->
            newState.copy(bottomSheetUiState = newState.bottomSheetUiState.copy(isExpense = isExpense))
        }
    }

    private fun dismissBottomSheet() {
        _state.update { newState ->
            newState.copy(bottomSheetUiState = newState.bottomSheetUiState.copy(showBottomSheet = false))
        }
    }

    private fun setSelectedTransaction(selectedTransaction: TransactionItem) {
        val isSelected =
            selectedTransaction == _state.value.bottomSheetUiState.selectedTransactionItem
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(selectedTransactionItem = if (isSelected) TransactionItem() else selectedTransaction)
            )
        }
    }

    private fun resetTextFields() {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(
                    textFieldValue = "",
                    amountTextFieldValue = TextFieldValue(""),
                    noteValue = null
                )
            )
        }
    }
}