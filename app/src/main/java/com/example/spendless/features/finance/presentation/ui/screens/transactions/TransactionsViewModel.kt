package com.example.spendless.features.finance.presentation.ui.screens.transactions

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
import com.example.spendless.features.finance.presentation.ui.common.groupTransactionsByDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber
import javax.inject.Inject

sealed interface TransactionsEvents {
    data object NavigateBack : TransactionsEvents
}

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val sessionStorage: SessionStorage,
    private val transactionsRepository: TransactionsRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(TransactionsUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<TransactionsEvents>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            setPreferencesFormat()
            getAllTransactions()
        }
    }

    fun onActions(transactionsActions: SharedActions) {
        when (transactionsActions) {
            SharedActions.TransactionsActions.ExportData -> exportData()
            SharedActions.TransactionsActions.NavigateBack -> navigateBack()
            SharedActions.DismissBottomSheet -> dismissBottomSheet()
            SharedActions.ShowBottomBar -> showBottomBar()
            SharedActions.OnCreateClick -> onCreateClick()

            is SharedActions.SelectedTransaction -> setSelectedTransaction(transactionsActions.selectedTransactionItem)
            is SharedActions.UpdateExpense -> updateExpense(transactionsActions.isExpense)
            is SharedActions.UpdateTextFieldValue -> updateTextFieldValue(transactionsActions.textFieldValue)
            is SharedActions.UpdateAmountTextFieldValue -> updateAmountTextFieldValue(
                transactionsActions.amountTextFieldValue
            )

            is SharedActions.UpdateNote -> updateNote(note = transactionsActions.noteValue)
            is SharedActions.UpdateSelectedCategory -> updateSelectedCategory(
                transactionsActions.category
            )

            is SharedActions.UpdateDropDownCategoryExpand -> updateDropDownCategoryExpand(
                transactionsActions.isExpand
            )

            is SharedActions.UpdateSelectedPaymentRecurrence -> updateSelectedPaymentRecurrence(
                transactionsActions.paymentRecurrence
            )

            is SharedActions.UpdateDropDownPaymentRecurrenceExpand -> updateDropDownPaymentRecurrenceExpand(
                transactionsActions.isExpand
            )

            is SharedActions.ShowFloatingActionButton -> showFloatingActionButton(
                transactionsActions.isVisible
            )

            else -> {}
        }
    }

    private suspend fun setPreferencesFormat() {
        val username = sessionStorage.getAuthInfo()!!.username
        val result = userRepository.getPreferencesByUsername(username)
        if (result is Result.Success) {
            _state.update { newState ->
                newState.copy(
                    bottomSheetUiState = newState.bottomSheetUiState.copy(
                        preferencesFormat = result.data,
                    )
                )
            }
            Timber.tag("MyTag").d("preferences: ${result.data}")
        }
    }

    private suspend fun getAllTransactions() {
        val transactionsList = transactionsRepository.getAllTransactions()
        transactionsList.collect { transactionsList ->
            val transactionMap = transactionsList.map {
                it.copy(
                    price = amountFormatter(
                        total = it.price,
                        isExpense = it.isExpense,
                        preferencesFormat = _state.value.bottomSheetUiState.preferencesFormat
                    )
                )
            }
            //map the date with their lists for example 9/10/2024 should have a list with key 9/10/2024 and value
            //all the transactionItems at this date
            val groupTransactions = groupTransactionsByDate(transactions = transactionMap, showAllDates = true)
            _state.update { newState ->
                newState.copy(
                    bottomSheetUiState = newState.bottomSheetUiState.copy(listOfTransactions = transactionsList),
                    transactionsByDate = groupTransactions
                )
            }
        }
    }

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
            //show loader for on create button
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
            Timber.tag("MyTag").d("onCreateClick $transactionItem")
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
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(selectedTransactionItem = selectedTransaction)
            )
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(TransactionsEvents.NavigateBack)
        }
    }

    private fun exportData() {

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