package com.example.spendless.features.auth.presentation.ui.common

import com.example.spendless.features.auth.presentation.designsystem.Constants

open class BasePinUiState(
    open val username: String = "",
    open val pinValue: String = "",
    val keys: List<String?> = Constants.keys
) {
    val ellipsesList: List<Boolean>
        get() = List(5) { index ->
            pinValue.length >= index + 1
        }
    val isEnabled: Boolean
        get() = pinValue.length < 5

    val isBackspaceEnabled: Boolean
        get() = pinValue.isNotEmpty()
}

