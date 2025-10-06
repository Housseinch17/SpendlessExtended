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
    val isButtonLoading: Boolean = false
) {
    val amountSpent: String = amountFormatter(
        total = total,
        preferencesFormat = preferencesFormat,
    )
}