package com.example.spendless.features.auth.presentation.ui.repeatPin

import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.features.auth.presentation.ui.common.BasePinUiState

data class RepeatPinUiState(
    override val username: String = "",
    val pin: String = "",
    val repeatPin: String = "",
    val bannerText: UiText? = null,
): BasePinUiState(pinValue = repeatPin)
