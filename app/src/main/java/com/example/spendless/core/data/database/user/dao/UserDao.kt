package com.example.spendless.core.data.database.user.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.spendless.core.data.database.user.model.PreferencesFormat
import com.example.spendless.core.data.database.user.model.Security
import com.example.spendless.core.data.database.user.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(userEntity: UserEntity)

    @Query("UPDATE User SET preferences = :preferencesFormat WHERE username = :username")
    suspend fun updatePreferencesFormat(
        username: String,
        preferencesFormat: PreferencesFormat
    )

    @Query("UPDATE User SET security = :security WHERE username = :username")
    suspend fun updateSecurity(
        username: String,
        security: Security
    )

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE username = :username)")
    suspend fun doesUserExist(username: String): Boolean

    @Query("Select pin From User Where username = :username Limit 1 ")
    suspend fun getPinByUsername(username: String): String

    @Query("Select security from User Where username = :username Limit 1")
    suspend fun getSecurityByUsername(username: String): Security

    @Query("Select preferences from User Where username = :username Limit 1")
    suspend fun getPreferencesByUsername(username: String): PreferencesFormat

    @Query("Select preferences from User Where username = :username Limit 1")
    fun getPreferencesByUsernameAsFlow(username: String): Flow<PreferencesFormat>

    @Query("SELECT * FROM User WHERE username = :username LIMIT 1")
    fun getUserByUsername(username: String): Flow<UserEntity>

}