package com.example.spendless.features.auth.presentation.ui.screens.logIn

import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.features.auth.domain.PatternValidator

data class LogInUiState(
    val username: String = "",
    val pin: String = "",
    val isLogInButtonLoading: Boolean = false,
    val isUsernameError: Boolean = false,
    val usernameError: UiText? = null,
    val isPinError: Boolean = false,
    val pinError: UiText? = null,
    val bannerText: UiText? = null
) {
    val isLogInButtonEnabled: Boolean =
        PatternValidator.isUsernameValid(username) && PatternValidator.isPinValid(pin)
}
