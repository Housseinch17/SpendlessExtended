package com.example.spendless.features.finance.presentation.ui.screens.transactions

import androidx.compose.ui.text.input.TextFieldValue
import com.example.spendless.core.database.user.model.PreferencesFormat
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.features.finance.data.model.TransactionItem
import com.example.spendless.features.finance.presentation.ui.common.groupTransactionsByDate
import com.example.spendless.R
import com.example.spendless.core.data.constant.Constants
import com.example.spendless.core.data.model.Category
import com.example.spendless.core.presentation.ui.amountFormatter
import com.example.spendless.features.finance.data.model.PaymentRecurrence

data class TransactionsUiState(
    val listOfTransactions: List<TransactionItem> = emptyList<TransactionItem>(),
    val selectedTransactionItem: TransactionItem = TransactionItem(),
    val showBottomSheet: Boolean = false,
    val preferencesFormat: PreferencesFormat = PreferencesFormat(),
    val isExpense: Boolean = true,
    val textFieldValue: String = "",
    val isTextFieldError: Boolean = false,
    val textFieldError: UiText? = null,
    val amountTextFieldValue: TextFieldValue = TextFieldValue(""),
    val noteValue: String? = null,

    val categoriesList: List<Category> = Constants.categoriesList,
    val selectedCategory: Category = Constants.categoriesList.first(),
    val isDropDownCategoryExpand: Boolean = false,

    val paymentRecurrenceList: List<PaymentRecurrence> = Constants.paymentRecurrenceList,
    val selectedPaymentRecurrence: PaymentRecurrence = Constants.paymentRecurrenceList.first(),
    val isDropDownPaymentRecurrenceExpand: Boolean = false,

    val isFloatingActionButtonVisible: Boolean = true,

    ) {
    val amountPlaceHolder: String = amountFormatter(
        total = "0",
        preferencesFormat = preferencesFormat
    )
    val transactionsByDate: Map<UiText, List<TransactionItem>> =
        groupTransactionsByDate(transactions = listOfTransactions)

    val placeHolder: Int = if (isExpense) R.string.receiver else R.string.sender

    val isButtonEnabled: Boolean =
        !isTextFieldError && textFieldValue.isNotBlank() && amountTextFieldValue.text.isNotBlank() && amountTextFieldValue.text.filter { it.isDigit() }
            .any { it != '0' }
}
