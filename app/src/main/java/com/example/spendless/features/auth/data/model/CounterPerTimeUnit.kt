package com.example.spendless.features.auth.data.model

import java.util.concurrent.TimeUnit

data class CounterPerTimeUnit(
    val counter: Int = 15,
    val timeUnit: TimeUnit = TimeUnit.SECONDS
)
