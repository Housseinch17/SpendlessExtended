package com.example.spendless.core.domain.model

import com.example.spendless.core.database.user.model.PreferencesFormat
import com.example.spendless.core.database.user.model.Security

data class User(
    val username: String,
    val pin: String,
    val preferences: PreferencesFormat,
    val security: Security
)
