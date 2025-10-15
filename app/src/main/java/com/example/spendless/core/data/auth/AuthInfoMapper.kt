package com.example.spendless.core.data.auth

import com.example.spendless.core.domain.auth.AuthInfo

fun AuthInfo.toAuthInfoSerializable(): AuthInfoSerializable {
    return AuthInfoSerializable(
        username = username,
        currentTimeLoggedIn = currentTimeLoggedIn,
//        security = security,
//        preferencesFormat = preferencesFormat
    )
}

fun AuthInfoSerializable.toAuthInfo(): AuthInfo {
    return AuthInfo(
        username = username,
        currentTimeLoggedIn = currentTimeLoggedIn,
//        security = security,
//        preferencesFormat = preferencesFormat
    )
}