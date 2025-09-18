package com.example.spendless.features.auth.presentation.ui.common

import com.example.spendless.features.auth.presentation.designsystem.Constants
import com.example.spendless.features.auth.presentation.designsystem.Constants.keysWithFingerPrint

open class BasePinUiState(
    open val username: String = "",
    open val pinValue: String = "",
    open val withBiometric: Boolean = false,
    open val enabledButtons: Boolean = true,
) {
    val keys: List<String?> = if(!withBiometric) Constants.keys else keysWithFingerPrint

    val ellipsesList: List<Boolean> = List(5) { index ->
        pinValue.length >= index + 1
    }

    val isEnabled: Boolean = pinValue.length < 5

    val isBackspaceEnabled: Boolean = pinValue.isNotEmpty()
}

