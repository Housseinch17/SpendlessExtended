package com.example.spendless.core.data.constant

import com.example.spendless.core.data.model.Category
import com.example.spendless.core.database.user.model.Currency
import com.example.spendless.core.presentation.designsystem.SpendLessIcons

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
            icon = SpendLessIcons.Clothing,
            categoryName = "Clothing"
        ),
        Category(
            icon = SpendLessIcons.Accessories,
            categoryName = "Accessories"
        ),
        Category(
            icon = SpendLessIcons.Health,
            categoryName = "Health"
        ),
        Category(
            icon = SpendLessIcons.Food,
            categoryName = "Food"
        ),
        Category(
            icon = SpendLessIcons.Entertainment,
            categoryName = "Entertainment"
        ),
        Category(
            icon = SpendLessIcons.Education,
            categoryName = "Education"
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