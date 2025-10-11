package com.example.spendless.features.finance.data.model

import androidx.annotation.StringRes
import com.example.spendless.R

sealed class ExportRange(@StringRes val exportRes: Int) {
    data object CurrentMonth : ExportRange(R.string.current_month)
    data object LastMonth : ExportRange(R.string.last_month)
    data object LastThreeMonths : ExportRange(R.string.last_three_months)
    data object AllData : ExportRange(R.string.all_data)

    data class SpecificMonth(val date: String? = null,val year: Int = 0, val monthNumber: Int = 0) : ExportRange(R.string.specific_month)
}