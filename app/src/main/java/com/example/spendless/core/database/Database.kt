package com.example.spendless.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.spendless.core.database.user.converters.UserConverters
import com.example.spendless.core.database.user.dao.UserDao
import com.example.spendless.core.database.user.model.UserEntity
import com.example.spendless.features.finance.database.converters.TransactionsConverters
import com.example.spendless.features.finance.database.dao.TransactionDao
import com.example.spendless.features.finance.database.model.TransactionEntity

@Database(entities = [UserEntity::class, TransactionEntity::class], version = 1)
@TypeConverters(UserConverters::class, TransactionsConverters::class)
abstract class Database: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
}