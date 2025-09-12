package com.example.spendless.features.auth.presentation.ui.common

sealed interface PinEvents {
    data object NavigateBack: PinEvents
    data class NavigateToRepeatPin(val username: String, val pin: String): PinEvents
}