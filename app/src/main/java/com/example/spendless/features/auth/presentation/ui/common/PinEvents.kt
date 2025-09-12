package com.example.spendless.features.auth.presentation.ui.common

sealed interface PinEvents {

    sealed interface CreatePinEvents : PinEvents {
        data object NavigateBack : CreatePinEvents
        data class NavigateToRepeatPin(val username: String, val pin: String) : CreatePinEvents
    }

    sealed interface RepeatPinEvents: PinEvents{
        data object NavigateBack : RepeatPinEvents
        data class NavigateToOnBoarding(val username: String, val pin: String) : RepeatPinEvents
    }
}