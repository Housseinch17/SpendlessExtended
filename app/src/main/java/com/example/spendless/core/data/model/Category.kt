package com.example.spendless.core.data.model

import com.example.spendless.features.finance.data.model.CategoryItem

data class Category(
    val image: String = "R.drawable.food",
    val categoryName: CategoryItem = CategoryItem.Food
)
