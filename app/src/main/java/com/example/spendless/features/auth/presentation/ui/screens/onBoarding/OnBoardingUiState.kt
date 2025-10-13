package com.example.spendless.features.auth.presentation.ui.screens.onBoarding

import com.example.spendless.core.data.constant.Constants
import com.example.spendless.core.data.database.user.model.Currency
import com.example.spendless.core.data.database.user.model.PreferencesFormat
import com.example.spendless.core.presentation.ui.amountFormatter

data class OnBoardingUiState(
    val currenciesList: List<Currency> = Constants.currenciesList,
    val username: String = "",
    val pin: String = "",
    val preferencesFormat: PreferencesFormat = PreferencesFormat(),
    val total: String = "1038245",
    val isExpanded: Boolean = false,
    val isButtonLoading: Boolean = false,
) {
    //first means if will return the first found char and if none found we can use
    //firstOrNull so it returns null if didn't find anything
    //get separators "." or "," or " "
    private val decimalChar: Char = preferencesFormat.decimalSeparator.first { it == '.' || it == ',' }
    private val thousandsChar: Char =
        preferencesFormat.thousandsSeparator.first { it == '.' || it == ',' || it == ' ' }

    val isStartTrackingEnabled: Boolean = decimalChar != thousandsChar

    val amountSpent: String = amountFormatter(
        total = if (total.startsWith("-")) total.drop(1) else total,
        isExpense = total.startsWith("-"),
        preferencesFormat = preferencesFormat,
    )
}