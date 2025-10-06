package com.example.spendless.features.auth.domain

import com.example.spendless.core.data.database.user.model.PreferencesFormat
import com.example.spendless.core.data.database.user.model.Security
import com.example.spendless.core.domain.model.User
import com.example.spendless.core.domain.util.DataError
import com.example.spendless.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insertUser(user: User): Result<Unit, DataError.Local>
    suspend fun getPinByUsername(username: String): Result<String, DataError.Local>
    suspend fun doesUserExist(username: String): Result<Boolean, DataError.Local>
    suspend fun getSecurityByUsername(username: String): Result<Security, DataError.Local>
    suspend fun getPreferencesByUsername(username: String): Result<PreferencesFormat, DataError.Local>
    fun getUserByUsername(username: String): Flow<User?>

}