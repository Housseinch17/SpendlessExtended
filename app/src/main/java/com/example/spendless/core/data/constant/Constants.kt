package com.example.spendless.core.data.constant

import com.example.spendless.core.data.model.Category
import com.example.spendless.core.data.database.user.model.Currency
import com.example.spendless.core.presentation.designsystem.SpendLessIcons
import com.example.spendless.features.auth.data.model.CounterPerTimeUnit
import com.example.spendless.features.finance.data.model.CategoryItem
import com.example.spendless.features.finance.data.model.ExportFormat
import com.example.spendless.features.finance.data.model.ExportRange
import com.example.spendless.features.finance.data.model.PaymentRecurrence
import java.util.concurrent.TimeUnit
import com.example.spendless.R

object Constants {
    //here it will check if we entered biometric settings to avoid prompt pin after
    //coming to foreground since setting biometric will take us to biometric settings
    //which means foreGround will be true
    var isBiometricSettings: Boolean = false

    val currenciesList = listOf<Currency>(
        Currency(symbol = "$", name = "US Dollar", code = "USD"),
        Currency(symbol = "€", name = "Euro", code = "EUR"),
        Currency(symbol = "£", name = "British Pound Sterling", code = "GBP"),
        Currency(symbol = "¥", name = "Japanese Yen", code = "JPY"),
        Currency(symbol = "₣", name = "Swiss Franc", code = "FRANC"),
    )

    val categoriesList = listOf<Category>(
        Category(
            image = SpendLessIcons.ENTERTAINMENT.toString(),
            categoryName = CategoryItem.Entertainment
        ),
        Category(
            image = SpendLessIcons.FOOD,
            categoryName = CategoryItem.Food
        ),
        Category(
            image = SpendLessIcons.CLOTHING,
            categoryName = CategoryItem.Clothing
        ),
        Category(
            image = SpendLessIcons.HOME,
            categoryName = CategoryItem.Home
        ),
        Category(
            image = SpendLessIcons.ACCESSORIES,
            categoryName = CategoryItem.Accessories
        ),
        Category(
            image = SpendLessIcons.HEALTH,
            categoryName = CategoryItem.Health
        ),
        Category(
            image = SpendLessIcons.EDUCATION,
            categoryName = CategoryItem.Education
        ),
    )

    val drawableResMap = mapOf(
        "R.drawable.food" to R.drawable.food,
        "R.drawable.clothing" to R.drawable.clothing,
        "R.drawable.accessories" to R.drawable.accessories,
        "R.drawable.health" to R.drawable.health,
        "R.drawable.entertainment" to R.drawable.entertainment,
        "R.drawable.education" to R.drawable.education,
        "R.drawable.home" to R.drawable.home,
        "R.drawable.recurrence" to R.drawable.recurrence
    )

    val stringResMap = mapOf(
        "R.string.clothing" to R.string.clothing,
        "R.string.accessories" to R.string.accessories,
        "R.string.health" to R.string.health,
        "R.string.home" to R.string.home,
        "R.string.food" to R.string.food,
        "R.string.entertainment" to R.string.entertainment,
        "R.string.education" to R.string.education,
        "R.string.expense" to R.string.expense,
        "R.string.income" to R.string.income
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

    val biometricList = listOf<Int>(
        R.string.enable,
        R.string.disable
    )

    val expiryDuration = listOf<CounterPerTimeUnit>(
        CounterPerTimeUnit(
            counter = 5,
            timeUnit = TimeUnit.MINUTES
        ),
        CounterPerTimeUnit(
            counter = 15,
            timeUnit = TimeUnit.MINUTES
        ),
        CounterPerTimeUnit(
            counter = 30,
            timeUnit = TimeUnit.MINUTES
        ),
        CounterPerTimeUnit(
            counter = 1,
            timeUnit = TimeUnit.HOURS
        ),
    )

    val lockedDuration = listOf<CounterPerTimeUnit>(
        CounterPerTimeUnit(
            counter = 15,
            timeUnit = TimeUnit.SECONDS
        ),
        CounterPerTimeUnit(
            counter = 30,
            timeUnit = TimeUnit.SECONDS
        ),
        CounterPerTimeUnit(
            counter = 1,
            timeUnit = TimeUnit.MINUTES
        ),
        CounterPerTimeUnit(
            counter = 5,
            timeUnit = TimeUnit.MINUTES
        ),
    )
}