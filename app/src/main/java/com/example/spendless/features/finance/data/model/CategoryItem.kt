package com.example.spendless.features.finance.data.model

import androidx.annotation.StringRes
import com.example.spendless.R

enum class CategoryItem(@StringRes val categoryRes: Int) {
    Clothing(R.string.clothing),
    Accessories(R.string.accessories),
    Health(R.string.health),
    Food(R.string.food),
    Entertainment(R.string.entertainment),
    Education(R.string.education)
}
