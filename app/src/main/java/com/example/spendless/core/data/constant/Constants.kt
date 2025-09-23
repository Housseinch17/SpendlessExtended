package com.example.spendless.core.data.constant

import com.example.spendless.core.data.model.Category
import com.example.spendless.core.database.user.model.Currency
import com.example.spendless.core.presentation.designsystem.SpendLessIcons
import com.example.spendless.features.finance.data.model.CategoryItem

object Constants {
    val currenciesList = listOf<Currency>(
        Currency(symbol = "$", name = "US Dollar", code = "USD"),
        Currency(symbol = "€", name = "Euro", code = "EUR"),
        Currency(symbol = "£", name = "British Pound Sterling", code = "GBP"),
        Currency(symbol = "¥", name = "Japanese Yen", code = "JPY"),
        Currency(symbol = "₣", name = "Swiss Franc", code = "FRANC"),
    )

    val categoriesList = listOf<Category>(
        Category(
            image = SpendLessIcons.Food,
            categoryName = CategoryItem.Food
        ),
        Category(
            image = SpendLessIcons.Clothing,
            categoryName = CategoryItem.Clothing
        ),
        Category(
            image = SpendLessIcons.Accessories,
            categoryName = CategoryItem.Accessories
        ),
        Category(
            image = SpendLessIcons.Health,
            categoryName = CategoryItem.Health
        ),
        Category(
            image = SpendLessIcons.Entertainment,
            categoryName = CategoryItem.Entertainment
        ),
        Category(
            image = SpendLessIcons.Education,
            categoryName = CategoryItem.Education
        )
    )

    val expensesFormatList = listOf<String>(
        "-$10",
        "($10)"
    )

    val decimalSeparatorList = listOf<String>(
        "1.00",
        "1,00"
    )

    val thousandsSeparatorList = listOf<String>(
        "1.000",
        "1,000",
        "1 000"
    )
}