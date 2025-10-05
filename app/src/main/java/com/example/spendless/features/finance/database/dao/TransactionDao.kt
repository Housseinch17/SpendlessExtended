package com.example.spendless.features.finance.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.spendless.features.finance.database.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transactionEntity: TransactionEntity)

    @Query("SELECT * FROM Transactions Where username = :username ORDER BY date DESC")
    fun getAllTransactions(username: String): Flow<List<TransactionEntity>?>

    @Query("SELECT * FROM Transactions WHERE username = :username AND date IN (:today, :yesterday)     ORDER BY date DESC")
    fun getTransactionsForTodayAndYesterday(
        username: String,
        today: String,
        yesterday: String
    ): Flow<List<TransactionEntity>?>

    //""" is used when we have multi-lines
    @Query(
        """SELECT IFNULL(
            CAST(SUM(
                CASE 
                    WHEN isExpense = 1 THEN -CAST(price AS INTEGER) 
                    ELSE CAST(price AS INTEGER) 
                END
            ) AS TEXT), '0'
        ) 
        FROM Transactions 
        WHERE username = :username
    """
    )
    fun getNetTotalForUser(username: String): Flow<String?>

    @Query(
        """
        SELECT * FROM Transactions
        WHERE username = :username AND isExpense = 1
        ORDER BY CAST(price AS REAL) DESC
        LIMIT 1
    """
    )
    fun getLargestTransaction(username: String): Flow<TransactionEntity?>
}