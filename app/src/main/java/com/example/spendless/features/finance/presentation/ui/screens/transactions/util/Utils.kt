package com.example.spendless.features.finance.presentation.ui.screens.transactions.util

import com.example.spendless.features.finance.data.model.ExportRange
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

object Utils {
    fun getPast12Months(): List<ExportRange.SpecificMonth> {
        val now = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        return (0..12).map { offset ->
            val pastDate = now.minus(DatePeriod(months = offset))
            val monthName = pastDate.month.name.lowercase()
                .replaceFirstChar { it.uppercase() }
            ExportRange.SpecificMonth(
                date = "$monthName ${pastDate.year}",
                year = pastDate.year,
                monthNumber = pastDate.monthNumber
            )
        }
    }
}