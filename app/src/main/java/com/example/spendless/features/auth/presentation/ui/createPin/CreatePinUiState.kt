package com.example.spendless.features.auth.presentation.ui.createPin

import com.example.spendless.features.auth.presentation.designsystem.Constants

data class CreatePinUiState(
    val username: String = "",
    val pin: String = "",
    val keys: List<String?> = Constants.keys
) {
    val ellipsesList: List<Boolean>
        get() = List(5) { index ->
            pin.length >= index + 1
        }
    val isEnabled: Boolean
        get() = pin.length < 5

    val isBackspaceEnabled: Boolean
        get() = pin.isNotEmpty()
}
