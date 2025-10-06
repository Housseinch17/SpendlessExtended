package com.example.spendless.core.data.database.user.mapper

import com.example.spendless.core.data.database.user.model.UserEntity
import com.example.spendless.core.domain.model.User

fun UserEntity.toUser(): User{
    return User(
        username = username,
        pin = pin,
        preferences = preferences,
        security = security
    )
}

fun User.toUserEntity(): UserEntity{
    return UserEntity(
        username = username,
        pin = pin,
        preferences = preferences,
        security = security
    )
}