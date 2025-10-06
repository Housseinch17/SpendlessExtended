package com.example.spendless.features.finance.data.di

import com.example.spendless.core.data.database.Database
import com.example.spendless.features.finance.data.datasource.TransactionsImpl
import com.example.spendless.features.finance.data.database.dao.TransactionDao
import com.example.spendless.features.finance.domain.TransactionsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FinanceModule {
    @Binds
    @Singleton
    abstract fun provideTransactionRepository(transactionsImpl: TransactionsImpl): TransactionsRepository

    companion object{
        @Provides
        @Singleton
        fun provideTransactionDao(database: Database): TransactionDao{
            return database.transactionDao()
        }
    }
}