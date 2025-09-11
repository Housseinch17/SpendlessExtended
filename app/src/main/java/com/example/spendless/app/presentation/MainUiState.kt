package com.example.spendless.app.presentation

data class MainUiState(
    val isLoggedInPreviously: Boolean = false,
    val isCheckingAuth: Boolean = true
)