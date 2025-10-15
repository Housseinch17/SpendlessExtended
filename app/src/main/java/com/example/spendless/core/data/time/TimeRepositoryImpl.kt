package com.example.spendless.core.data.time

import android.annotation.SuppressLint
import com.example.spendless.core.domain.time.TimeRepository
import com.example.spendless.features.auth.data.model.CounterPerTimeUnit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TimeRepositoryImpl @Inject constructor() : TimeRepository {
    @SuppressLint("NewApi")
    override suspend fun getCurrentTime(): String {
        val currentDateTime = getCurrentTimeAsDateTime()
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
        val formattedCurrentTime = currentDateTime.format(formatter)

        return formattedCurrentTime
    }

    @SuppressLint("NewApi")
    override suspend fun getUpdatedTime(currentDateTime: String, sessionExpiryDuration: CounterPerTimeUnit): String {
        val currentDateTime = getCurrentTimeAsDateTime()
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")

        val updatedDateTime = when (sessionExpiryDuration.timeUnit) {
            TimeUnit.SECONDS -> currentDateTime.plusSeconds(sessionExpiryDuration.counter.toLong())
            TimeUnit.MINUTES -> currentDateTime.plusMinutes(sessionExpiryDuration.counter.toLong())
            TimeUnit.HOURS -> currentDateTime.plusHours(sessionExpiryDuration.counter.toLong())
            TimeUnit.DAYS -> currentDateTime.plusDays(sessionExpiryDuration.counter.toLong())
            else -> currentDateTime
        }

        val formattedUpdatedTime = updatedDateTime.format(formatter)
        return formattedUpdatedTime
    }

    @SuppressLint("NewApi")
    private fun getCurrentTimeAsDateTime(): LocalDateTime{
        val currentDateTime = LocalDateTime.now()

        return currentDateTime
    }
}