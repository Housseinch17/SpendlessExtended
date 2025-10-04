package com.example.spendless.features.finance.presentation.ui.common

import com.example.spendless.R
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.features.finance.data.model.TransactionItem
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

fun groupTransactionsByDate(transactions: List<TransactionItem>): Map<UiText, List<TransactionItem>> {
    if (transactions.isNotEmpty()) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val yesterday = today.minus(1, DateTimeUnit.DAY)

        val grouped = transactions.groupBy { LocalDate.parse(it.date) }
        return grouped.mapKeys { (itemDate, _) ->
            when (itemDate) {
                today -> UiText.StringResource(R.string.today)
                yesterday -> UiText.StringResource(R.string.yesterday)
                else -> UiText.DynamicString(itemDate.toString())
            }
        }
    }else{
        return emptyMap()
    }
}