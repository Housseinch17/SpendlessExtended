package com.example.spendless.core.data.auth

import androidx.datastore.core.DataStore
import com.example.spendless.core.data.di.AuthInfoDataStore
import com.example.spendless.core.domain.auth.AuthInfo
import com.example.spendless.core.domain.auth.SessionStorage
import com.example.spendless.core.domain.util.DataError
import com.example.spendless.core.domain.util.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SessionStorageImpl @Inject constructor(
    @AuthInfoDataStore
    private val authDataStore: DataStore<AuthInfoSerializable?>
) : SessionStorage {
    override suspend fun getAuthInfo(): AuthInfo? = withContext(Dispatchers.IO) {
        val data = authDataStore.data.firstOrNull()
        return@withContext if (data == null || data == AuthInfoSerializable()) null else data.toAuthInfo()
    }

    override suspend fun setAuthInfo(authInfo: AuthInfo?) {
        try {
            withContext(Dispatchers.IO) {
                authDataStore.updateData {

                    //if authInfo is set to null just use AuthInfoSerializable()
                    //later check if the data is null or AuthInfoSerializable() means it's null
                    //used like that to clear the data inside AuthInfo instead of just removing/deleting it
                    //which will remain the authInfo variables saved inside memory
                    authInfo?.toAuthInfoSerializable() ?: AuthInfoSerializable()
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Timber.tag("MyTag").e("setAuthInfo: ${e.localizedMessage}")
        }
    }

    override suspend fun setCurrentTimeLoggedIn(currentTimeLoggedIn: String) {
        try {
            withContext(Dispatchers.IO) {
                authDataStore.updateData { authInfo ->
                    val oldAuthInfo = authInfo ?: AuthInfoSerializable()
                    oldAuthInfo.copy(
                        currentTimeLoggedIn = currentTimeLoggedIn
                    )
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Timber.tag("MyTag").e("setCurrentTimeLoggedIn: ${e.localizedMessage}")
        }
    }

    //clearing authInfo
    override suspend fun clearAuthInfo(): Result<Unit, DataError.Local> {
        return try {
            setAuthInfo(null)
            Timber.tag("MyTag").d("clearAuthInfo: success")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.tag("MyTag").e("clearAuthInfo: ${e.localizedMessage}")
            Result.Error(error = DataError.Local.Unknown(unknownError = e.message.toString()))
        }
    }
}