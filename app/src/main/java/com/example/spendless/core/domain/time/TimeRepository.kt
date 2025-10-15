package com.example.spendless.core.domain.time

import com.example.spendless.features.auth.data.model.CounterPerTimeUnit

interface TimeRepository {
    suspend fun getCurrentTime(): String
    suspend fun getUpdatedTime(currentDateTime: String,sessionExpiryDuration: CounterPerTimeUnit): String
}