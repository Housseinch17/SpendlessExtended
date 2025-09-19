package com.example.spendless.features.auth.data.model

import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit

@Serializable
data class CounterPerTimeUnit(
    val counter: Int = 15,
    val timeUnit: TimeUnit = TimeUnit.SECONDS
)
