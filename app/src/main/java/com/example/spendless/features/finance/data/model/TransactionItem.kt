package com.example.spendless.features.finance.data.model

import androidx.annotation.StringRes
import com.example.spendless.core.data.model.Category
import com.example.spendless.R

data class TransactionItem(
    val category: Category = Category(),
    val isExpense: Boolean = true,
    val title: String = "",
    @StringRes val description: Int = 0,
    val content: String? = null,
    val image: Int = R.drawable.food,
    val price: String = "",
    val date: String = "",
) {
    fun toCsv(): String {
        val expenseType = if (isExpense) "Expense" else "Income"
        return listOf(
            title,
            category.categoryName.name,
            price,
            expenseType,
            date
        ).joinToString(",")
    }

    fun toPdfString(): String {
        val expenseType = if (isExpense) "Expense" else "Income"
        return "%-20s | %-15s | %-10s | %-8s | %-12s".format(
            title.take(20),
            category.categoryName.name.take(15),
            price.take(10),
            expenseType,
            date
        )
    }
}
