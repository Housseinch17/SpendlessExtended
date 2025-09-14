package com.example.spendless.core.database.user.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "User")
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("username") val username: String,
    @ColumnInfo("pin") val pin: String,
    @ColumnInfo("total") val total: String,
    @ColumnInfo("preferences") val preferences: PreferencesFormat
)