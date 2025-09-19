package com.example.spendless.core.data.auth

import androidx.annotation.Keep
import com.example.spendless.core.database.user.model.PreferencesFormat
import com.example.spendless.core.database.user.model.Security
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class AuthInfoSerializable(
    val username: String = "",
    val security: Security = Security(),
    val preferencesFormat: PreferencesFormat = PreferencesFormat(),
)