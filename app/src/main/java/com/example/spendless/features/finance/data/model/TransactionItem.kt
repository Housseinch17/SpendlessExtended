package com.example.spendless.features.finance.data.model

import com.example.spendless.core.data.model.Category
import com.example.spendless.R

data class TransactionItem(
    val category: Category = Category(),
    val isExpense: Boolean = true,
    val title: String = "",
    val description: String = "",
    val content: String? = null,
    val image: Int = R.drawable.food,
    val price: String = "",
    val date: String = "",
)
