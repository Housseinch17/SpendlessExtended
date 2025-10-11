package com.example.spendless.core.data.constant

import com.example.spendless.core.data.model.Category
import com.example.spendless.core.data.database.user.model.Currency
import com.example.spendless.core.presentation.designsystem.SpendLessIcons
import com.example.spendless.features.finance.data.model.CategoryItem
import com.example.spendless.features.finance.data.model.ExportFormat
import com.example.spendless.features.finance.data.model.ExportRange
import com.example.spendless.features.finance.data.model.PaymentRecurrence

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
            image = SpendLessIcons.Entertainment,
            categoryName = CategoryItem.Entertainment
        ),
        Category(
            image = SpendLessIcons.Food,
            categoryName = CategoryItem.Food
        ),
        Category(
            image = SpendLessIcons.Clothing,
            categoryName = CategoryItem.Clothing
        ),
        Category(
            image = SpendLessIcons.Home,
            categoryName = CategoryItem.Home
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
            image = SpendLessIcons.Education,
            categoryName = CategoryItem.Education
        ),
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

    val paymentRecurrenceList = listOf<PaymentRecurrence>(
        PaymentRecurrence.DoesNotRepeat,
        PaymentRecurrence.Daily,
        PaymentRecurrence.WeeklyOn,
        PaymentRecurrence.MonthlyOnThe,
        PaymentRecurrence.YearlyOnFeb
    )

    val exportRangeList = listOf<ExportRange>(
        ExportRange.CurrentMonth,
        ExportRange.LastMonth,
        ExportRange.LastThreeMonths,
        ExportRange.AllData,
        ExportRange.SpecificMonth()
    )

    val exportFormatList = listOf<ExportFormat>(
        ExportFormat.CSV,
        ExportFormat.PDF
    )
}