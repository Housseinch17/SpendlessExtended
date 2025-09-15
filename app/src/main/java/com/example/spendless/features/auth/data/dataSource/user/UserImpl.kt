package com.example.spendless.features.auth.data.dataSource.user

import com.example.spendless.core.data.encryption.EncryptionHelper
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
import timber.log.Timber
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
            Timber.tag("MyTag").d("insertUser: success")
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Timber.tag("MyTag").e("insertUser: error ${e.localizedMessage}")
            Result.Error(error = DataError.Local.Unknown(unknownError = e.localizedMessage ?: ""))
        }
    }

    override suspend fun getPinByUsername(username: String): Result<String, DataError.Local> {
        return try {
            val pin = userDao.getPinByUsername(username = username)
            val decryptedPin = EncryptionHelper.decryptedValue(pin)
            Timber.tag("MyTag").d("getPinByUsername: success")
            Result.Success(decryptedPin)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Timber.tag("MyTag").e("getPinByUsername: error ${e.localizedMessage}")
            Result.Error(DataError.Local.Unknown(unknownError = e.localizedMessage ?: ""))
        }
    }

    override suspend fun doesUserExist(username: String): Result<Boolean, DataError.Local> {
        return try {
            val result = userDao.doesUserExist(username = username)
            Timber.tag("MyTag").d("doesUserExist: doesUserExist")
            Result.Success(result)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Timber.tag("MyTag").e("doesUserExist: error: ${e.localizedMessage}")
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