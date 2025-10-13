package com.example.spendless.features.finance.presentation.ui.common

import com.example.spendless.R
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.features.auth.data.model.CounterPerTimeUnit
import com.example.spendless.features.finance.data.model.TransactionItem
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import java.util.concurrent.TimeUnit

fun <T> formatItem(item: T): UiText {
    return when (item) {
        is Int -> UiText.StringResource(item)
        is CounterPerTimeUnit -> when (item.timeUnit) {
            TimeUnit.SECONDS -> UiText.DynamicString("${item.counter}s")
            TimeUnit.MINUTES -> UiText.DynamicString("${item.counter} min")
            TimeUnit.HOURS -> UiText.DynamicString("${item.counter} hour")
            else -> UiText.DynamicString("")
        }
        else -> {
            UiText.DynamicString(item.toString())
        }
    }
}

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
    } else {
        return emptyMap()
    }
}