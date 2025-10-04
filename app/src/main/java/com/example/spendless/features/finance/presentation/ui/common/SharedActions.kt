package com.example.spendless.features.finance.presentation.ui.common

import androidx.compose.ui.text.input.TextFieldValue
import com.example.spendless.core.data.model.Category
import com.example.spendless.features.finance.data.model.PaymentRecurrence
import com.example.spendless.features.finance.data.model.TransactionItem

sealed interface SharedActions {
    data class SelectedTransaction(val selectedTransactionItem: TransactionItem) :
        SharedActions

    data object DismissBottomSheet : SharedActions
    data object ShowBottomBar : SharedActions
    data class UpdateExpense(val isExpense: Boolean) : SharedActions
    data class UpdateTextFieldValue(val textFieldValue: String) : SharedActions
    data class UpdateAmountTextFieldValue(val amountTextFieldValue: TextFieldValue) :
        SharedActions

    data class UpdateNote(val noteValue: String) : SharedActions
    data class UpdateSelectedCategory(val category: Category) : SharedActions
    data class UpdateDropDownCategoryExpand(val isExpand: Boolean) : SharedActions
    data class UpdateSelectedPaymentRecurrence(val paymentRecurrence: PaymentRecurrence) :
        SharedActions

    data class UpdateDropDownPaymentRecurrenceExpand(val isExpand: Boolean) : SharedActions
    data object OnCreateClick : SharedActions
    data class ShowFloatingActionButton(val isVisible: Boolean) : SharedActions

    sealed interface TransactionsActions: SharedActions {
        data object NavigateBack : TransactionsActions
        data object ExportData : TransactionsActions
    }

    sealed interface DashboardActions: SharedActions {
        data object NavigateToExportData : DashboardActions
        data object NavigateToSettings : DashboardActions
        data class SelectedTransaction(val selectedTransactionItem: TransactionItem) : DashboardActions
        data object ShowAll : DashboardActions
        data class ShowFloatingActionButton(val isVisible: Boolean) : DashboardActions
    }

}