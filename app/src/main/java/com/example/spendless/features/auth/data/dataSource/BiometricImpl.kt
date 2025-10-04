package com.example.spendless.features.auth.data.dataSource

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.features.auth.domain.BiometricRepository
import com.example.spendless.features.auth.presentation.ui.common.PinEvents
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class BiometricImpl @Inject constructor() : BiometricRepository {
    lateinit var prompt: BiometricPrompt
    override suspend fun showBiometricPrompt(
        title: UiText,
        subtitle: UiText,
        activity: AppCompatActivity
    ): PinEvents.BiometricResult =
        suspendCancellableCoroutine { continuation ->

            val manager = BiometricManager.from(activity)
            val authenticators =
                if (Build.VERSION.SDK_INT >= 30) BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                else BiometricManager.Authenticators.BIOMETRIC_STRONG

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title.asString(activity))
                .setSubtitle(subtitle.asString(activity))
                .setAllowedAuthenticators(authenticators)

            if (Build.VERSION.SDK_INT < 30) {
                promptInfo.setNegativeButtonText("Cancel")
            }

            //here to check if we are able to use the biometric
            //if biometric not set we will request to set one
            when (manager.canAuthenticate(authenticators)) {
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    continuation.resume(PinEvents.BiometricResult.HardwareUnavailable)
                    return@suspendCancellableCoroutine
                }

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    continuation.resume(PinEvents.BiometricResult.FeatureUnavailable)
                    return@suspendCancellableCoroutine
                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    continuation.resume(PinEvents.BiometricResult.AuthenticationNotSet)
                    return@suspendCancellableCoroutine
                }

                else -> Unit
            }

            //after being able of using biometric check the result
            prompt = BiometricPrompt(
                activity,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        if (continuation.isActive) {
                            continuation.resume(PinEvents.BiometricResult.AuthenticationFailed)
                            prompt.cancelAuthentication()
                        }
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        if (continuation.isActive) continuation.resume(PinEvents.BiometricResult.AuthenticationSuccess)
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        if (continuation.isActive) {
                            continuation.resume(
                                PinEvents.BiometricResult.AuthenticationError(
                                    errString.toString()
                                )
                            )
                            prompt.cancelAuthentication()
                        }
                    }
                }
            )
            prompt.authenticate(promptInfo.build())
        }
}