package com.example.spendless.core.database.user.model

import com.example.spendless.core.data.constant.Constants.currenciesList
import com.example.spendless.core.data.constant.Constants.decimalSeparatorList
import com.example.spendless.core.data.constant.Constants.expensesFormatList
import com.example.spendless.core.data.constant.Constants.thousandsSeparatorList

data class PreferencesFormat(
    val currency: Currency = currenciesList.first(),
    val expenses: String = expensesFormatList.last(),
    val decimalSeparator: String = decimalSeparatorList.last(),
    val thousandsSeparator: String = thousandsSeparatorList.last(),
)
