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
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class UserImpl @Inject constructor(
    private val userDao: UserDao,
) : UserRepository {
    override suspend fun insertUser(user: User): Result<Unit, DataError.Local> {
        return try {
            val pin = user.pin
            val encryptedPin = EncryptionHelper.encryptedValue(value = pin)
            val newUser = user.copy(
                pin = encryptedPin
            )
            val userEntity = newUser.toUserEntity().copy(
                username = newUser.username.lowercase()
            )
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
            val pin = userDao.getPinByUsername(username = username.lowercase())
            val decryptedPin = EncryptionHelper.decryptedValue(pin)
            Result.Success(decryptedPin)
        } catch (e: Exception) {
            Timber.tag("MyTag").e("getPinByUsername: $e ${e.localizedMessage}")
            if (e is CancellationException) {
                throw e
            }
            Result.Error(DataError.Local.Unknown(unknownError = e.localizedMessage ?: ""))
        }
    }

    override suspend fun doesUserExist(username: String): Result<Boolean, DataError.Local> {
        return try {
            val result = userDao.doesUserExist(username = username.lowercase())
            Result.Success(result)
        } catch (e: Exception) {
            Timber.tag("MyTag").e("doesUserExist: $e ${e.localizedMessage}")
            if (e is CancellationException) {
                throw e
            }
            Result.Error(error = DataError.Local.Unknown(unknownError = e.localizedMessage ?: ""))
        }
    }

    override suspend fun getSecurityByUsername(username: String): Result<Security, DataError.Local> {
        return try {
            val security = userDao.getSecurityByUsername(username.lowercase())
            Result.Success(security)
        } catch (e: Exception) {
            Timber.tag("MyTag").e("getSecurityByUsername: $e ${e.localizedMessage}")
            if (e is CancellationException) {
                throw e
            }
            Result.Error(error = DataError.Local.Unknown(unknownError = e.localizedMessage ?: ""))
        }
    }

    override suspend fun getPreferencesByUsername(username: String): Result<PreferencesFormat, DataError.Local> {
        return try {
            val preferencesFormat = userDao.getPreferencesByUsername(username.lowercase())
            Result.Success(preferencesFormat)
        } catch (e: Exception) {
            Timber.tag("MyTag").e("getPreferencesByUsername: $e ${e.localizedMessage}")
            if (e is CancellationException) {
                throw e
            }
            Result.Error(error = DataError.Local.Unknown(unknownError = e.localizedMessage ?: ""))
        }
    }

    override fun getPreferencesByUsernameAsFlow(username: String): Flow<PreferencesFormat> {
        return try {
            userDao.getPreferencesByUsernameAsFlow(username)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            emptyFlow<PreferencesFormat>()
        }
    }

    override fun getUserByUsername(username: String): Flow<User?> {
        return try {
            userDao.getUserByUsername(username = username.lowercase()).map {
                it.toUser()
            }
        } catch (e: Exception) {
            Timber.tag("MyTag").e("getUserByUsername: $e ${e.localizedMessage}")
            if (e is CancellationException) {
                throw e
            }
            //emit null if error
            flowOf(null)
        }
    }

    override suspend fun updatePreferencesFormat(
        username: String,
        preferencesFormat: PreferencesFormat
    ): Result<Unit, DataError.Local> {
        return try {
            userDao.updatePreferencesFormat(
                username = username.lowercase(),
                preferencesFormat = preferencesFormat
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Result.Error(error = DataError.Local.Unknown(unknownError = e.localizedMessage ?: ""))
        }
    }

    override suspend fun updateSecurity(
        username: String,
        security: Security
    ): Result<Unit, DataError.Local> {
        return try {
            userDao.updateSecurity(
                username = username.lowercase(),
                security = security
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Result.Error(error = DataError.Local.Unknown(unknownError = e.localizedMessage ?: ""))
        }
    }
}