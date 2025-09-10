package com.example.spendless.core.data.dataSource.user

import com.example.spendless.core.database.user.dao.UserDao
import com.example.spendless.core.database.user.model.UserEntity
import com.example.spendless.core.domain.UserRepository
import com.example.spendless.core.domain.util.DataError
import com.example.spendless.core.domain.util.Result
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class UserImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun upsertUser(userEntity: UserEntity): Result<Unit, DataError.Local> {
        return try {
            userDao.upsertUser(userEntity = userEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Result.Error(error = DataError.Local.DiskFull)
        }
    }

    override suspend fun getPinByUsername(username: String): Result<String, DataError.Local> {
        return try {
            val result = userDao.getPinByUsername(username = username)
            Result.Success(result)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Result.Error(DataError.Local.Unknown(unknownError = e.localizedMessage ?: ""))
        }
    }

    override suspend fun doesUserExist(username: String): Result<Boolean, DataError.Local> {
        return try {
            val result = userDao.doesUserExist(username = username)
            Result.Success(result)
        }catch (e: Exception){
            if(e is CancellationException){
                throw e
            }
            Result.Error(error = DataError.Local.Unknown(unknownError = e.localizedMessage ?: ""))
        }
    }
}