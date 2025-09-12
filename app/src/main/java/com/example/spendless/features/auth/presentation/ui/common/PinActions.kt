package com.example.spendless.features.auth.presentation.ui.common

sealed interface PinActions {
    data class UpdatePin(val newPin: String) : PinActions
    data object NavigateBack : PinActions
}