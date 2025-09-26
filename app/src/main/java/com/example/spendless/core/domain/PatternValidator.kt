package com.example.spendless.core.domain

import com.example.spendless.R
import com.example.spendless.core.presentation.ui.UiText

object PatternValidator {
    val usernameRegex = "^[A-Za-z0-9]{3,14}$".toRegex()

    fun isUsernameValid(username: String): Boolean {
        return username.matches(usernameRegex)
    }

    fun isPinValid(pin: String): Boolean {
        return pin.length == 5 && pin.all {
            it.isDigit()
        }
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

    fun getPinError(pin: String): UiText {
        return when {
            pin.any { !(it.isDigit()) } -> UiText.StringResource(R.string.error_pin_invalid_chars)
            pin.length > 5 -> UiText.StringResource(R.string.error_pin_too_long)
            pin.length < 5 -> UiText.StringResource(R.string.error_pin_too_short)
            else -> {
                UiText.DynamicString("")
            }
        }
    }
}