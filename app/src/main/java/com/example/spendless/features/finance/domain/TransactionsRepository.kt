package com.example.spendless.features.finance.domain

import com.example.spendless.core.domain.util.DataError
import com.example.spendless.core.domain.util.Result
import com.example.spendless.features.finance.data.model.TransactionItem
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    suspend fun insertTransaction(transactionItem: TransactionItem): Result<Unit, DataError.Local>
    suspend fun getAllTransactions(): Flow<List<TransactionItem>>
    suspend fun getTransactionsForTodayAndYesterday(): Flow<List<TransactionItem>>
    suspend fun getTransactionsForLastTwoDates(): Flow<List<TransactionItem>>
    suspend fun getNetTotalForUser(): Flow<String>
    suspend fun getLargestTransaction(): Flow<TransactionItem?>
    suspend fun getTotalSpentPreviousWeek(): Flow<String>
    suspend fun getAllTransactionsForAllData(): List<TransactionItem>
    suspend fun getTransactionsCurrentMonth(): List<TransactionItem>
    suspend fun getTransactionsLastMonth(): List<TransactionItem>
    suspend fun getTransactionsLastThreeMonths(): List<TransactionItem>
    suspend fun getTransactionsSpecificMonth(specificMonth: Int, specificYear: Int): List<TransactionItem>
}