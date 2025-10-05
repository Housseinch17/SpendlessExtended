package com.example.spendless.features.finance.presentation.ui.screens.dashboard

import com.example.spendless.core.data.model.Category
import com.example.spendless.core.database.user.model.PreferencesFormat
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.core.presentation.ui.amountFormatter
import com.example.spendless.features.finance.data.model.TransactionItem
import com.example.spendless.features.finance.presentation.ui.common.BottomSheetUiState


data class DashboardUiState(
    val username: String = "",
    val isLoading: Boolean = true,
    val preferencesFormat: PreferencesFormat = PreferencesFormat(),
    val total: String = "${preferencesFormat.currency.symbol}0.00",
    val largestTransaction: TransactionItem? = null,
    val listOfTransactions: List<TransactionItem> = emptyList<TransactionItem>(),
    val selectedTransaction: TransactionItem = TransactionItem(),
    val transactionsByDate: Map<UiText, List<TransactionItem>> = emptyMap(),
    val previousWeekSpent: String = "${preferencesFormat.currency.symbol}0.00",
    val bottomSheetUiState: BottomSheetUiState = BottomSheetUiState(),
) {
    val largestCategoryExpense: Category? = largestTransaction?.category

    val amountSpent: String = amountFormatter(
        total = if (total.startsWith("-")) total.drop(1) else total,
        isExpense = total.startsWith("-"),
        preferencesFormat = preferencesFormat,
    )

    val largestTransactionAmount: String =
        amountFormatter(
            total = largestTransaction?.price ?: "0.00",
            preferencesFormat = preferencesFormat
        )
}
