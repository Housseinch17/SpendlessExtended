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
)
