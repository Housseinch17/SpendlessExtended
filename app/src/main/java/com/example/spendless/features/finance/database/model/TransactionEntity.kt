package com.example.spendless.features.finance.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spendless.R
import com.example.spendless.core.data.model.Category

@Keep
@Entity(tableName = "Transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Int = 0,
    @ColumnInfo("username") val username: String = "",
    @ColumnInfo("category") val category: Category = Category(),
    @ColumnInfo("isExpense") val isExpense: Boolean = true,
    @ColumnInfo("title") val title: String = "",
    @ColumnInfo("description") val description: Int = 0,
    @ColumnInfo("content") val content: String? = null,
    @ColumnInfo("image") val image: Int = R.drawable.food,
    @ColumnInfo("price") val price: String = "",
    @ColumnInfo("date") val date: String = "",
)
