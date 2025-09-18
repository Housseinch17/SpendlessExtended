package com.example.spendless.features.auth.domain

import androidx.appcompat.app.AppCompatActivity
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.features.auth.presentation.ui.common.PinEvents

interface BiometricRepository {
    suspend fun showBiometricPrompt(title: UiText, subtitle: UiText,activity: AppCompatActivity): PinEvents.BiometricResult
}