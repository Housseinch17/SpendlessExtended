package com.example.spendless.features.auth.data.di

import com.example.spendless.features.auth.data.dataSource.BiometricImpl
import com.example.spendless.features.auth.data.dataSource.user.UserImpl
import com.example.spendless.features.auth.domain.BiometricRepository
import com.example.spendless.features.auth.domain.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindsUserRepository(userImpl: UserImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindsBiometricRepository(biometricImpl: BiometricImpl): BiometricRepository
}