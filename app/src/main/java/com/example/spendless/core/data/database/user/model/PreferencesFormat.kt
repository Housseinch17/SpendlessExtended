package com.example.spendless.core.data.database.user.model

import com.example.spendless.core.data.constant.Constants.currenciesList
import com.example.spendless.core.data.constant.Constants.decimalSeparatorList
import com.example.spendless.core.data.constant.Constants.expensesFormatList
import com.example.spendless.core.data.constant.Constants.thousandsSeparatorList
import kotlinx.serialization.Serializable

@Serializable
data class PreferencesFormat(
    val currency: Currency = currenciesList.first(),
    val expenses: String = expensesFormatList.first(),
    val decimalSeparator: String = decimalSeparatorList.first(),
    val thousandsSeparator: String = thousandsSeparatorList[1],
)
