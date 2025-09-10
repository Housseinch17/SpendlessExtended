package com.example.spendless.core.domain

import com.example.spendless.core.database.user.model.UserEntity
import com.example.spendless.core.domain.util.DataError
import com.example.spendless.core.domain.util.Result

interface UserRepository {
    suspend fun upsertUser(userEntity: UserEntity): Result<Unit, DataError.Local>
    suspend fun getPinByUsername(username: String): Result<String, DataError.Local>
    suspend fun doesUserExist(username: String): Result<Boolean, DataError.Local>
}