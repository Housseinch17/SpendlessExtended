package com.example.spendless.features.auth.domain

import com.example.spendless.core.database.user.model.PreferencesFormat
import com.example.spendless.core.database.user.model.UserEntity
import com.example.spendless.core.domain.model.User
import com.example.spendless.core.domain.util.DataError
import com.example.spendless.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insertUser(user: User): Result<Unit, DataError.Local>
    suspend fun getPinByUsername(username: String): Result<String, DataError.Local>
    suspend fun doesUserExist(username: String): Result<Boolean, DataError.Local>
    fun getUserByUsername(username: String): Flow<User?>

}