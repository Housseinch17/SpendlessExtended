package com.example.spendless.core.data.database.user.model

import androidx.annotation.Keep
import com.example.spendless.features.auth.data.model.CounterPerTimeUnit
import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit

@Keep
@Serializable
data class Security(
    val withBiometric: Boolean = true,
    val sessionExpiry: CounterPerTimeUnit = CounterPerTimeUnit(counter = 5, timeUnit = TimeUnit.MINUTES),
    val lockedOutDuration: CounterPerTimeUnit = CounterPerTimeUnit(counter = 30, timeUnit = TimeUnit.SECONDS)
)
