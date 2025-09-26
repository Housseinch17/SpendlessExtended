package com.example.spendless.features.finance.data.model

import androidx.annotation.StringRes
import com.example.spendless.R

enum class PaymentRecurrence(@StringRes val categoryRes: Int) {
    DoesNotRepeat(R.string.does_not_repeat),
    Daily(R.string.daily),
    WeeklyOn(R.string.weekly_on),
    MonthlyOnThe(R.string.monthly_on_the),
    YearlyOnFeb(R.string.yearly_on)
}