package com.example.spendless.features.finance.data.datasource

import com.example.spendless.core.domain.auth.SessionStorage
import com.example.spendless.core.domain.util.DataError
import com.example.spendless.core.domain.util.Result
import com.example.spendless.features.finance.data.model.TransactionItem
import com.example.spendless.features.finance.database.dao.TransactionDao
import com.example.spendless.features.finance.database.mapper.toTransactionEntity
import com.example.spendless.features.finance.database.mapper.toTransactionItem
import com.example.spendless.features.finance.domain.TransactionsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionsImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val sessionStorage: SessionStorage
) : TransactionsRepository {
    override suspend fun insertTransaction(transactionItem: TransactionItem): Result<Unit, DataError.Local> {
        return try {
            //here we use getAuthInfo()!! because it can not be null if the user logged in
            val username = sessionStorage.getAuthInfo()!!.username
            val transactionEntity = transactionItem.toTransactionEntity().copy(
                username = username
            )
            transactionDao.insertTransaction(transactionEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Result.Error(DataError.Local.Unknown(unknownError = e.localizedMessage ?: ""))
        }
    }

    override suspend fun getAllTransactions(): Flow<List<TransactionItem>> {
        return try {
            val username = sessionStorage.getAuthInfo()!!.username
            transactionDao.getAllTransactions(username = username).map { list ->
                list.orEmpty().map { it.toTransactionItem() }
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            flowOf(emptyList())
        }
    }

    override suspend fun getNetTotalForUser(): Flow<String> =
        try {
            val username = sessionStorage.getAuthInfo()!!.username
            transactionDao.getNetTotalForUser(username).map {
                it.orEmpty()
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            emptyFlow()
        }

    override suspend fun getLargestTransaction(): Flow<TransactionItem?> =
        try {
            val username = sessionStorage.getAuthInfo()!!.username
            transactionDao.getLargestTransaction(username).map {
                it?.toTransactionItem()
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            emptyFlow()
        }
}