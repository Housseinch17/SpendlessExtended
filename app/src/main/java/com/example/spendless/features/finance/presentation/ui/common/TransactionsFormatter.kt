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
import timber.log.Timber

fun groupTransactionsByDate(transactions: List<TransactionItem>, showAllDates: Boolean = true): Map<UiText, List<TransactionItem>> {
    if (transactions.isNotEmpty()) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val yesterday = today.minus(1, DateTimeUnit.DAY)

        Timber.tag("MyTag").d("showAllDates: $showAllDates")
        //show all dates
        val transactionsToGroup = if (showAllDates) {
            transactions
        } else {
            //filter only today/yesterday is not show all
            transactions.filter {
                val transactionDate = LocalDate.parse(it.date)
                transactionDate == today || transactionDate == yesterday
            }
        }
        Timber.tag("MyTag").d("${transactionsToGroup == transactions}")

        val grouped = transactionsToGroup.groupBy { LocalDate.parse(it.date) }

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