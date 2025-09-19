package com.example.spendless.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.spendless.core.database.user.converters.UserConverters
import com.example.spendless.core.database.user.dao.UserDao
import com.example.spendless.core.database.user.model.UserEntity

@Database(entities = [UserEntity::class], version = 1)
@TypeConverters(UserConverters::class)
abstract class Database: RoomDatabase() {
    abstract fun userDao(): UserDao
}