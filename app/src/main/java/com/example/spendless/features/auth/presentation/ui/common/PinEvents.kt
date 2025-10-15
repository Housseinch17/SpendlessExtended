package com.example.spendless.features.auth.presentation.ui.common

sealed interface PinEvents {
    data object NavigateBack : PinEvents
    data class NavigateToRepeatPin(val username: String, val pin: String) : PinEvents

    sealed interface RepeatPinEvents : PinEvents {
        data class NavigateToOnBoarding(val username: String, val pin: String) : RepeatPinEvents
    }

    sealed interface PinPromptEvents : PinEvents {
        data object NavigateToLogIn : PinPromptEvents
        data object VerifiedSuccessfully: PinPromptEvents

    }

    sealed interface BiometricResult : PinEvents {
        data object HardwareUnavailable : BiometricResult
        data object FeatureUnavailable : BiometricResult
        data class AuthenticationError(val error: String) : BiometricResult
        data object AuthenticationFailed : BiometricResult
        data object AuthenticationSuccess : BiometricResult
        data object AuthenticationNotSet : BiometricResult
    }
}