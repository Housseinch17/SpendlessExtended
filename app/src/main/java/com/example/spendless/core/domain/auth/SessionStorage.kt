package com.example.spendless.core.domain.auth

import com.example.spendless.core.domain.util.DataError
import com.example.spendless.core.domain.util.Result

interface SessionStorage {
    suspend fun getAuthInfo(): AuthInfo?
    suspend fun setAuthInfo(authInfo: AuthInfo?)
    suspend fun clearAuthInfo(): Result<Unit, DataError.Local>
}