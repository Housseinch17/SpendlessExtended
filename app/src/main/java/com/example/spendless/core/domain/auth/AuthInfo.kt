package com.example.spendless.core.domain.auth

import com.example.spendless.core.data.database.user.model.PreferencesFormat
import com.example.spendless.core.data.database.user.model.Security

data class AuthInfo(
    val username: String = "",
    val currentTimeLoggedIn: String = "",
//    val security: Security = Security(),
//    val preferencesFormat: PreferencesFormat = PreferencesFormat()
)
