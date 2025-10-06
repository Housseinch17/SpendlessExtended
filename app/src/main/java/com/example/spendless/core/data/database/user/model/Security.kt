package com.example.spendless.core.data.database.user.model

import com.example.spendless.features.auth.data.model.CounterPerTimeUnit
import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit

@Serializable
data class Security(
    val withBiometric: Boolean = true,
    val sessionExpiry: CounterPerTimeUnit = CounterPerTimeUnit(counter = 5, timeUnit = TimeUnit.MINUTES),
    val lockedOutDuration: CounterPerTimeUnit = CounterPerTimeUnit(counter = 15, timeUnit = TimeUnit.SECONDS)
)
