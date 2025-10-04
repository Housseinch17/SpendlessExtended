package com.example.spendless.features.finance.presentation.ui.screens.dashboard

import com.example.spendless.core.data.model.Category
import com.example.spendless.core.database.user.model.PreferencesFormat
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.core.presentation.ui.amountFormatter
import com.example.spendless.features.finance.data.model.TransactionItem
import com.example.spendless.features.finance.presentation.ui.common.BottomSheetUiState
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime


data class DashboardUiState(
    val username: String = "",
    val isLoading: Boolean = true,
    val preferencesFormat: PreferencesFormat = PreferencesFormat(),
    val total: String = "${preferencesFormat.currency.symbol}0.00",
    val largestTransaction: TransactionItem? = null,
    val listOfTransactions: List<TransactionItem> = emptyList<TransactionItem>(),
    val selectedTransaction: TransactionItem = TransactionItem(),
    val transactionsByDate: Map<UiText, List<TransactionItem>> = emptyMap(),

    val isFloatingActionButtonVisible: Boolean = true,
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

    val previousWeekSpent: String =
        if (listOfTransactions.isEmpty()) amountFormatter(
            total = largestTransaction?.price ?: "0.00",
            preferencesFormat = preferencesFormat,
        ) else totalSpentPreviousWeek(
            listOfTransactions,
            preferencesFormat
        )

    private fun previousWeekRange(
        today: LocalDate = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
    ): Pair<LocalDate, LocalDate> {
        //Monday = 1, Tuesday = 2,... Sunday = 7
        val dayOfWeek = today.dayOfWeek.isoDayNumber

        val startOfCurrentWeek = today.minus(dayOfWeek - 1, DateTimeUnit.DAY)
        val startOfPreviousWeek = startOfCurrentWeek.minus(7, DateTimeUnit.DAY)
        val endOfPreviousWeek = startOfPreviousWeek.plus(6, DateTimeUnit.DAY)

        return startOfPreviousWeek to endOfPreviousWeek
    }

    //total spent previous week
    private fun totalSpentPreviousWeek(
        transactions: List<TransactionItem>,
        preferencesFormat: PreferencesFormat
    ): String {
        val (start, end) = previousWeekRange()

        val total = transactions
            .filter { it.isExpense }
            .mapNotNull { transaction ->
                val date = transaction.date.takeIf { it.isNotEmpty() }?.let { LocalDate.parse(it) }
                if (date != null && date in start..end) transaction.price.toDoubleOrNull() else null
            }
            .sumOf { it }

        return amountFormatter(total.toString(), preferencesFormat = preferencesFormat)
    }
}
