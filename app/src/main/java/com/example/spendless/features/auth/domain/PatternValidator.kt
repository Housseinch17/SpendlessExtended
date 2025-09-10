package com.example.spendless.features.auth.domain

import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.R

object PatternValidator {
    val usernameRegex = "^[A-Za-z0-9]{3,14}$".toRegex()

    fun isUsernameValid(username: String): Boolean {
        return username.matches(usernameRegex)
    }

    fun getUsernameError(username: String): UiText {
        return when {
            username.length < 3 -> UiText.StringResource(R.string.error_username_too_short)
            username.length > 14 -> UiText.StringResource(R.string.error_username_too_long)
            !username.all { it.isLetterOrDigit() } -> UiText.StringResource(R.string.error_username_invalid_chars)
            else -> {
                UiText.DynamicString("")
            }
        }
    }
}