package com.example.spendless.core.database.user.mapper

import com.example.spendless.core.database.user.model.UserEntity
import com.example.spendless.core.domain.model.User

fun UserEntity.toUser(): User{
    return User(
        username = username,
        pin = pin,
        total = total,
        preferences = preferences
    )
}

fun User.toUserEntity(): UserEntity{
    return UserEntity(
        username = username,
        pin = pin,
        total = total,
        preferences = preferences
    )
}