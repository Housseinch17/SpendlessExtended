package com.example.spendless.core.domain.model

import com.example.spendless.core.database.user.model.PreferencesFormat

data class User(
    val username: String,
    val pin: String,
    val total: String,
    val preferences: PreferencesFormat
)
