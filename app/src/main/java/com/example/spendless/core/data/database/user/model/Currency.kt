package com.example.spendless.core.data.database.user.model

import kotlinx.serialization.Serializable

@Serializable
data class Currency(
    val symbol: String,
    val name: String,
    val code: String
)
