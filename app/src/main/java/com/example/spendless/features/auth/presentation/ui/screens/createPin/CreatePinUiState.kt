package com.example.spendless.features.auth.presentation.ui.screens.createPin

import com.example.spendless.features.auth.presentation.ui.common.BasePinUiState

data  class CreatePinUiState(
    override val username: String = "",
    val pin: String = ""
): BasePinUiState(pinValue = pin)