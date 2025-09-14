package com.example.spendless.core.database.user.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.spendless.core.database.user.model.PreferencesFormat
import com.example.spendless.core.database.user.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(userEntity: UserEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE username = :username)")
    suspend fun doesUserExist(username: String): Boolean

    @Query("Select pin From User Where username = :username Limit 1 ")
    suspend fun getPinByUsername(username: String): String

    @Query("SELECT * FROM User WHERE username = :username LIMIT 1")
    fun getUserByUsername(username: String): Flow<UserEntity>

}