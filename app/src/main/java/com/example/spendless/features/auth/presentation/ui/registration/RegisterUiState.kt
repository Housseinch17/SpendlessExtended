package com.example.spendless.features.auth.presentation.ui.registration

import com.example.spendless.core.presentation.ui.UiText

data class RegisterUiState(
    val username: String = "",
    val pin: String = "",
    val isUsernameError: Boolean = false,
    val usernameError: UiText? = null,
    val isNextEnabled: Boolean = false,
    val bannerText: UiText? = null
){
    val isEnabled: Boolean
        get() = (isUsernameError != true && username.length > 1 && isNextEnabled)
}
