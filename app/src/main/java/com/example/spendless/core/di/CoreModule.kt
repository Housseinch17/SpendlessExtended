package com.example.spendless.core.di

import android.content.Context
import androidx.room.Room
import com.example.spendless.core.data.dataSource.user.UserImpl
import com.example.spendless.core.database.Database
import com.example.spendless.core.database.user.dao.UserDao
import com.example.spendless.core.domain.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {

    companion object{
        @Provides
        @Singleton
        fun provideDataBase(@ApplicationContext context: Context): Database{
            return Room.databaseBuilder(context, Database::class.java, "MobileDevSpendless")
                .build()
        }

        @Provides
        @Singleton
        fun provideUserDao(database: Database): UserDao{
            return database.userDao()
        }
    }

    @Binds
    @Singleton
    abstract fun provideUserRepository(userImpl: UserImpl): UserRepository

}