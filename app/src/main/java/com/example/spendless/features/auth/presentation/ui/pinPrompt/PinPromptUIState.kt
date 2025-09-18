package com.example.spendless.features.auth.presentation.ui.pinPrompt

import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.features.auth.data.model.CounterPerTimeUnit
import com.example.spendless.features.auth.presentation.ui.common.BasePinUiState
import com.example.spendless.R

data class PinPromptUIState(
    override val username: String = "rockefeller74",
    val pin: String = "",
    val pinPromptPin: String = "",
    private val _withBiometric: Boolean = true,
    val pinErrorCounter: Int = 0,
    val biometricErrorCounter: Int = 0,
    val bannerText: UiText? = null,
    override val enabledButtons: Boolean = true,
    val counterPerTimeUnit: CounterPerTimeUnit = CounterPerTimeUnit(),

    val headerUiText: UiText = UiText.StringResource(R.string.hello),
    val headerText: String? = "$username!",
    val bodyUiText: UiText = UiText.StringResource(R.string.enter_your_pin),

    val counter: String? = null,
) : BasePinUiState(
    pinValue = pinPromptPin,
    withBiometric = _withBiometric,
    enabledButtons = enabledButtons
)
