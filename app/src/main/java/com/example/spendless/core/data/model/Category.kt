package com.example.spendless.core.data.model

import com.example.spendless.features.finance.data.model.CategoryItem
import com.example.spendless.R

data class Category(
    val image: Int = R.drawable.food,
    val categoryName: CategoryItem = CategoryItem.Food
)
