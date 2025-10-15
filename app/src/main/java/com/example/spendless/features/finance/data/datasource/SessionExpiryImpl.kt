package com.example.spendless.features.finance.data.datasource

import android.annotation.SuppressLint
import com.example.spendless.core.domain.auth.SessionStorage
import com.example.spendless.core.domain.time.TimeRepository
import com.example.spendless.core.domain.util.Result
import com.example.spendless.features.auth.domain.UserRepository
import com.example.spendless.features.finance.domain.SessionExpiryUseCase
import timber.log.Timber
import javax.inject.Inject

class SessionExpiryImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val timeRepository: TimeRepository,
    private val sessionStorage: SessionStorage
) : SessionExpiryUseCase {
    @SuppressLint("NewApi")
    override suspend operator  fun invoke(): Boolean {
        val username = sessionStorage.getAuthInfo()!!.username
        //to get session expiry duration
        val security = userRepository.getSecurityByUsername(username = username)
        //last time logged in to add the session expiry duration to it
        val currentTimeLoggedIn = sessionStorage.getAuthInfo()!!.currentTimeLoggedIn
        Timber.tag("MyTag").d("currentTimeLoggedIn: $currentTimeLoggedIn")
        //current time now
        val currentTime = timeRepository.getCurrentTime()
        Timber.tag("MyTag").d("currentTime: $currentTime")
        if (security is Result.Success) {
            val sessionExpiryDuration = security.data.sessionExpiry
            Timber.tag("MyTag").d("sessionExpiryDuration: $sessionExpiryDuration")
            val sessionExpiryTime = timeRepository.getUpdatedTime(
                currentDateTime = currentTimeLoggedIn,
                sessionExpiryDuration = sessionExpiryDuration
            )
            Timber.tag("MyTag").d("expire at: $sessionExpiryTime")
            Timber.tag("MyTag").d("sessionExpired: ${sessionExpiryTime < currentTime}")
            return sessionExpiryTime < currentTime
        }
        Timber.tag("MyTag").d("sessionExpired: false")
        return false
    }
}