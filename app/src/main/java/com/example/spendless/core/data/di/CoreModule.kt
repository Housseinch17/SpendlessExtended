package com.example.spendless.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.room.Room
import com.example.spendless.core.data.auth.AuthInfoSerializable
import com.example.spendless.core.data.auth.AuthInfoSerializer
import com.example.spendless.core.data.auth.SessionStorageImpl
import com.example.spendless.core.data.database.Database
import com.example.spendless.core.data.database.user.dao.UserDao
import com.example.spendless.core.data.time.TimeRepositoryImpl
import com.example.spendless.core.domain.auth.SessionStorage
import com.example.spendless.core.domain.time.TimeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

val Context.authInfoDataStore: DataStore<AuthInfoSerializable?> by dataStore(
    fileName = "auth_info",
    serializer = AuthInfoSerializer
)

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {

    @Binds
    @Singleton
    abstract fun bindsSessionStorage(sessionStorageImpl: SessionStorageImpl): SessionStorage

    @Binds
    @Singleton
    abstract fun bindsTimeRepository(timeRepositoryImpl: TimeRepositoryImpl): TimeRepository

    companion object {
        @Provides
        @Singleton
        fun provideDataBase(@ApplicationContext context: Context): Database {
            return Room.databaseBuilder(context, Database::class.java, "MobileDevSpendless")
                .build()
        }

        @Provides
        @Singleton
        fun provideUserDao(database: Database): UserDao {
            return database.userDao()
        }

        @Provides
        @Singleton
        @AuthInfoDataStore
        fun provideAuthDataStore(
            @ApplicationContext
            context: Context
        ): DataStore<AuthInfoSerializable?> {
            return context.authInfoDataStore
        }
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInfoDataStore