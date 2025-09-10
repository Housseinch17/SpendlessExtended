package com.example.spendless.core.database.user.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.spendless.core.database.user.model.UserEntity

@Dao
interface UserDao {
    @Upsert
    suspend fun upsertUser(userEntity: UserEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE username = :username)")
    suspend fun doesUserExist(username: String): Boolean

    @Query("Select pin From User Where username = :username Limit 1 ")
    suspend fun getPinByUsername(username: String): String



}