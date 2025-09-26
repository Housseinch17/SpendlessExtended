package com.example.spendless.features.finance.presentation.ui.screens.dashboard

import com.example.spendless.core.data.model.Category
import com.example.spendless.core.database.user.model.PreferencesFormat
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.core.presentation.ui.amountFormatter
import com.example.spendless.features.finance.data.model.TransactionItem
import com.example.spendless.features.finance.presentation.ui.common.groupTransactionsByDate
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
    val preferencesFormat: PreferencesFormat = PreferencesFormat(),
    val total: String = "${preferencesFormat.currency.symbol}0.00",
    val largestTransaction: TransactionItem? = null,
    val listOfTransactions: List<TransactionItem> = emptyList<TransactionItem>(),
    val selectedTransaction: TransactionItem = TransactionItem()
) {
    val amountSpent: String = amountFormatter(
        total = total,
        preferencesFormat = preferencesFormat
    )

    val largestCategoryExpense: Category? = largestTransaction?.category

    val largestTransactionAmount: String =
        amountFormatter(
            total = largestTransaction?.price ?: "0.00",
            preferencesFormat = preferencesFormat
        )

    val previousWeekSpent: String =
        if (listOfTransactions.isEmpty()) preferencesFormat.currency.symbol + "0" else totalSpentPreviousWeek(
            listOfTransactions,
            preferencesFormat
        )

    val transactionsByDate: Map<UiText, List<TransactionItem>> =
        groupTransactionsByDate(transactions = listOfTransactions)



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
            .filter {
                val itemDate = LocalDate.parse(it.date)
                itemDate in start..end
            }
            .sumOf { it.price.toDoubleOrNull() ?: 0.0 } // use Double
        return amountFormatter(total.toString(), preferencesFormat = preferencesFormat)
    }
}
