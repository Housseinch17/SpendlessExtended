package com.example.spendless.features.auth.presentation.ui.common

import androidx.appcompat.app.AppCompatActivity

sealed interface PinActions {
    data class UpdatePin(val newPin: String, val activity: AppCompatActivity? = null) : PinActions
    data object NavigateBack : PinActions

    sealed interface PromptPinActions: PinActions{
        data object LogOut: PromptPinActions
    }
}