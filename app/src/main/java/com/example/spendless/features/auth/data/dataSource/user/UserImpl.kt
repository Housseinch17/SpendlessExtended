package com.example.spendless.features.auth.data.dataSource.user

import com.example.spendless.core.database.user.dao.UserDao
import com.example.spendless.core.database.user.mapper.toUser
import com.example.spendless.core.database.user.mapper.toUserEntity
import com.example.spendless.core.domain.model.User
import com.example.spendless.core.domain.util.DataError
import com.example.spendless.core.domain.util.Result
import com.example.spendless.features.auth.domain.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun insertUser(user: User): Result<Unit, DataError.Local> {
        return try {
            val userEntity = user.toUserEntity()
            userDao.insertUser(userEntity = userEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Result.Error(error = DataError.Local.Unknown(unknownError = e.localizedMessage ?: ""))
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
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Result.Error(error = DataError.Local.Unknown(unknownError = e.localizedMessage ?: ""))
        }
    }

    override fun getUserByUsername(username: String): Flow<User?> {
        return try {
            userDao.getUserByUsername(username = username).map {
                it.toUser()
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            //emit null if error
            flowOf(null)
        }
    }
}