package com.example.spendless.features.finance.data.datasource

import com.example.spendless.core.domain.auth.SessionStorage
import com.example.spendless.core.domain.util.DataError
import com.example.spendless.core.domain.util.Result
import com.example.spendless.features.finance.data.database.dao.TransactionDao
import com.example.spendless.features.finance.data.database.mapper.toTransactionEntity
import com.example.spendless.features.finance.data.database.mapper.toTransactionItem
import com.example.spendless.features.finance.data.model.TransactionItem
import com.example.spendless.features.finance.domain.TransactionsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber
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
                username = username,
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

    override suspend fun getTransactionsForTodayAndYesterday(): Flow<List<TransactionItem>> =
        try {
            val username = sessionStorage.getAuthInfo()!!.username
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val yesterday = today.minus(1, DateTimeUnit.DAY)
            transactionDao.getTransactionsForTodayAndYesterday(
                username = username,
                today = today.toString(),
                yesterday = yesterday.toString()
            ).map { list ->
                list.orEmpty().map {
                    it.toTransactionItem()
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            emptyFlow()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getTransactionsForLastTwoDates(): Flow<List<TransactionItem>> =
        try {
            val username = sessionStorage.getAuthInfo()!!.username
            getLastTwoTransactionDates().flatMapLatest { dates->
                Timber.tag("MyTag").d("dates: $dates")
                transactionDao.getTransactionsForLastTwoDates(username = username, dates = dates).map {
                    it.map {
                        it.toTransactionItem()
                    }
                }
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

    override suspend fun getTotalSpentPreviousWeek(): Flow<String> {
        val username = sessionStorage.getAuthInfo()!!.username
        return transactionDao.getAllTransactions(username)
            .map { list ->
                val transactions = list.orEmpty().map { it.toTransactionItem() }
                val (start, end) = previousWeekRange()
                val total = transactions
                    .filter { it.isExpense }
                    .mapNotNull { transactionItem ->
                        val date = transactionItem.date.takeIf { it.isNotEmpty() }
                            ?.let { LocalDate.parse(it) }
                        if (date != null && date in start..end) transactionItem.price.toInt() else null
                    }
                    .sumOf { it }
                total.toString()
            }
    }

    override suspend fun getAllTransactionsForAllData(): List<TransactionItem> {
        val username = sessionStorage.getAuthInfo()!!.username
        return transactionDao.getAllTransactionsForAllData(username = username).map {
            it.toTransactionItem()
        }
    }

    override suspend fun getTransactionsCurrentMonth(): List<TransactionItem> {
        val username = sessionStorage.getAuthInfo()!!.username
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        //month should be 2 char and if it's 1 add 0 for example month = 2 -> 02
        val yearMonth = "${now.year}-${now.monthNumber.toString().padStart(2, '0')}"
        return transactionDao.getTransactionsForMonth(
            username = username,
            yearMonth = yearMonth
        )
            .map {
                it.toTransactionItem()
            }
    }

    override suspend fun getTransactionsLastMonth(): List<TransactionItem> {
        val username = sessionStorage.getAuthInfo()!!.username
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val lastMonth = now.minus(DatePeriod(months = 1))
        // Format year-month as "YYYY-MM"
        val yearMonth = "%04d-%02d".format(lastMonth.year, lastMonth.monthNumber)
        return transactionDao.getTransactionsForMonth(
            username = username,
            yearMonth = yearMonth
        )
            .map {
                it.toTransactionItem()
            }
    }


    override suspend fun getTransactionsLastThreeMonths(): List<TransactionItem> {
        val username = sessionStorage.getAuthInfo()!!.username
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val last3Months = (0..2).map {
            val d = now.minus(DatePeriod(months = it))
            "${d.year}-${d.monthNumber.toString().padStart(2, '0')}"
        }
        return transactionDao.getTransactionsLastThreeMonths(
            username = username,
            yearMonths = last3Months
        ).map {
            it.toTransactionItem()
        }
    }

    override suspend fun getTransactionsSpecificMonth(
        specificMonth: Int,
        specificYear: Int
    ): List<TransactionItem> {
        val username = sessionStorage.getAuthInfo()!!.username
        //Format yearMonth as "YYYY-MM" → e.g. "2025-03"
        val specificDate = "%04d-%02d".format(specificYear, specificMonth)
        return transactionDao.getTransactionsSpecificMonth(
            username = username,
            yearMonth = specificDate
        ).map {
            it.toTransactionItem()
        }
    }

    //use it as a flow to get the last two dates that are updated in table
    private suspend fun getLastTwoTransactionDates(): Flow<List<String>> =
        try {
            val username = sessionStorage.getAuthInfo()!!.username
            val lastTwoDates = transactionDao.getLastTwoTransactionDates(username = username)
            lastTwoDates
        } catch (e: Exception) {
            Timber.tag("MyTag").e("getLastTwoTransactionDates: $e: ${e.localizedMessage}")
            emptyFlow<List<String>>()
        }

    private fun previousWeekRange(
        today: LocalDate = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
    ): Pair<LocalDate, LocalDate> {
        //Monday = 1, Tuesday = 2,... Sunday = 7
        val dayOfWeek = today.dayOfWeek.isoDayNumber

        val startOfCurrentWeek = today.minus(dayOfWeek - 1, DateTimeUnit.DAY)
        val startOfPreviousWeek = startOfCurrentWeek.minus(7, DateTimeUnit.DAY)
        val endOfPreviousWeek = startOfPreviousWeek.plus(6, DateTimeUnit.DAY)

        return startOfPreviousWeek to endOfPreviousWeek
    }
}