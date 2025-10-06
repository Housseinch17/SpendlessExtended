package com.example.spendless.features.auth.data.dataSource.user

import com.example.spendless.core.data.encryption.EncryptionHelper
import com.example.spendless.core.data.database.user.dao.UserDao
import com.example.spendless.core.data.database.user.mapper.toUser
import com.example.spendless.core.data.database.user.mapper.toUserEntity
import com.example.spendless.core.data.database.user.model.PreferencesFormat
import com.example.spendless.core.data.database.user.model.Security
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
            val pin = user.pin
            val encryptedPin = EncryptionHelper.encryptedValue(value = pin)
            val newUser = user.copy(
                pin = encryptedPin
            )
            val userEntity = newUser.toUserEntity()
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
            val pin = userDao.getPinByUsername(username = username)
            val decryptedPin = EncryptionHelper.decryptedValue(pin)
            Result.Success(decryptedPin)
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

    override suspend fun getSecurityByUsername(username: String): Result<Security, DataError.Local> {
        return try {
            val security = userDao.getSecurityByUsername(username)
            Result.Success(security)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Result.Error(error = DataError.Local.Unknown(unknownError = e.localizedMessage ?: ""))
        }
    }

    override suspend fun getPreferencesByUsername(username: String): Result<PreferencesFormat, DataError.Local> {
        return try {
            val preferencesFormat = userDao.getPreferencesByUsername(username)
            Result.Success(preferencesFormat)
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