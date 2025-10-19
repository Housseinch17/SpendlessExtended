package com.example.spendless.features.finance.data.model

import com.example.spendless.core.data.model.Category

data class TransactionItem(
    val category: Category = Category(),
    val isExpense: Boolean = true,
    val title: String = "",
    val description: String = "",
    val content: String? = null,
    val image: String = "R.drawable.food",
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
}
