package com.example.spendless.features.finance.presentation.ui.screens.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendless.core.data.model.Category
import com.example.spendless.core.domain.PatternValidator
import com.example.spendless.features.finance.data.model.PaymentRecurrence
import com.example.spendless.features.finance.data.model.TransactionItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TransactionsEvents {
    data object NavigateBack : TransactionsEvents
}

sealed interface TransactionsActions {
    data object NavigateBack : TransactionsActions
    data object ExportData : TransactionsActions
    data class SelectedTransaction(val selectedTransactionItem: TransactionItem) :
        TransactionsActions

    data object DismissBottomSheet : TransactionsActions
    data object ShowBottomBar : TransactionsActions
    data class UpdateExpense(val isExpense: Boolean) : TransactionsActions
    data class UpdateTextFieldValue(val textFieldValue: String) : TransactionsActions
    data class UpdateAmountTextFieldValue(val amountTextFieldValue: String) : TransactionsActions
    data class UpdateNote(val noteValue: String) : TransactionsActions
    data class UpdateSelectedCategory(val category: Category) : TransactionsActions
    data class UpdateDropDownCategoryExpand(val isExpand: Boolean): TransactionsActions
    data class UpdateSelectedPaymentRecurrence(val paymentRecurrence: PaymentRecurrence) : TransactionsActions
    data class UpdateDropDownPaymentRecurrenceExpand(val isExpand: Boolean): TransactionsActions
    data object OnCreateClick: TransactionsActions
}

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val saveStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(TransactionsUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<TransactionsEvents>()
    val events = _events.receiveAsFlow()

    init {
        setShowBottomBar()
    }

    fun onActions(transactionsActions: TransactionsActions) {
        when (transactionsActions) {
            TransactionsActions.ExportData -> exportData()
            TransactionsActions.NavigateBack -> navigateBack()
            TransactionsActions.DismissBottomSheet -> dismissBottomSheet()
            TransactionsActions.ShowBottomBar -> showBottomBar()
            TransactionsActions.OnCreateClick -> onCreateClick()

            is TransactionsActions.SelectedTransaction -> setSelectedTransaction(transactionsActions.selectedTransactionItem)
            is TransactionsActions.UpdateExpense -> updateExpense(transactionsActions.isExpense)
            is TransactionsActions.UpdateTextFieldValue -> updateTextFieldValue(transactionsActions.textFieldValue)
            is TransactionsActions.UpdateAmountTextFieldValue -> updateAmountTextFieldValue(
                transactionsActions.amountTextFieldValue
            )

            is TransactionsActions.UpdateNote -> updateNote(note = transactionsActions.noteValue)
            is TransactionsActions.UpdateSelectedCategory -> updateSelectedCategory(
                transactionsActions.category
            )

            is TransactionsActions.UpdateDropDownCategoryExpand -> updateDropDownCategoryExpand(transactionsActions.isExpand)
            is TransactionsActions.UpdateSelectedPaymentRecurrence -> updateSelectedPaymentRecurrence(transactionsActions.paymentRecurrence)
            is TransactionsActions.UpdateDropDownPaymentRecurrenceExpand -> updateDropDownPaymentRecurrenceExpand(transactionsActions.isExpand)

        }
    }

    private fun onCreateClick(){

    }

    private fun updateDropDownPaymentRecurrenceExpand(isExpand: Boolean){
        _state.update { newState->
            newState.copy(
                isDropDownPaymentRecurrenceExpand = isExpand
            )
        }
    }

    private fun updateSelectedPaymentRecurrence(paymentRecurrence: PaymentRecurrence){
        _state.update { newState ->
            newState.copy(
                selectedPaymentRecurrence = paymentRecurrence
            )
        }
    }

    private fun updateDropDownCategoryExpand(isExpand: Boolean){
        _state.update { newState->
            newState.copy(
                isDropDownCategoryExpand = isExpand
            )
        }
    }

    private fun updateSelectedCategory(selectedCategory: Category) {
        _state.update { newState ->
            newState.copy(
                selectedCategory = selectedCategory
            )
        }
    }

    private fun updateNote(note: String) {
        _state.update { newState ->
            newState.copy(
                noteValue = note
            )
        }
    }

    private fun updateAmountTextFieldValue(amountTextFieldValue: String) {
        _state.update { newState ->
            newState.copy(amountTextFieldValue = amountTextFieldValue)
        }
    }

    private fun updateTextFieldValue(textFieldValue: String) {
        val isTextFieldValid = PatternValidator.isUsernameValid(textFieldValue)
        //username error will only show in ui when username is not valid
        val textFieldError = PatternValidator.getUsernameError(username = textFieldValue)
        _state.update { newState ->
            newState.copy(
                textFieldValue = textFieldValue,
                isTextFieldError = (!isTextFieldValid && !textFieldValue.isEmpty()),
                textFieldError = textFieldError,
            )
        }
    }

    private fun setShowBottomBar() {
        val showBottomSheet = saveStateHandle.get<Boolean>("showBottomSheet") == true
        _state.update { newState ->
            newState.copy(
                showBottomSheet = showBottomSheet
            )
        }
    }

    private fun showBottomBar() {
        _state.update { newState ->
            newState.copy(
                showBottomSheet = true
            )
        }
    }

    private fun updateExpense(isExpense: Boolean) {
        _state.update { newState ->
            newState.copy(isExpense = isExpense)
        }
    }

    private fun dismissBottomSheet() {
        _state.update { newState ->
            newState.copy(showBottomSheet = false)
        }

    }

    private fun setSelectedTransaction(selectedTransaction: TransactionItem) {
        _state.update { newState ->
            newState.copy(
                selectedTransactionItem = selectedTransaction
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
}