package com.example.spendless.core.presentation.ui
import com.example.spendless.core.data.database.user.model.PreferencesFormat
import java.util.Locale


fun amountFormatter(
    total: String,
    preferencesFormat: PreferencesFormat,
    isExpense: Boolean = true,
): String {
    if (total.isEmpty()) return ""

    val totalLong = total.toLongOrNull() ?: 0L

    val decimalSeparator = if (preferencesFormat.decimalSeparator.contains(",")) "," else "."

    val thousandsSeparator = when {
        preferencesFormat.thousandsSeparator.contains(".") -> "."
        preferencesFormat.thousandsSeparator.contains(",") -> ","
        preferencesFormat.thousandsSeparator.contains(" ") -> " "
        else -> ","
    }

    val integerPart = (totalLong / 100).toString()
    val decimalPart = (totalLong % 100).toString().padStart(2, '0')

    val formattedInteger = integerPart.reversed()
        .chunked(3)
        .joinToString(thousandsSeparator)
        .reversed()

    val formattedNumber = "$formattedInteger$decimalSeparator$decimalPart"
    val withCurrency = "${preferencesFormat.currency.symbol}$formattedNumber"

    return when {
        preferencesFormat.expenses.startsWith("-") -> if (isExpense) "-$withCurrency" else withCurrency
        preferencesFormat.expenses.startsWith("(") -> if (isExpense) "-($withCurrency)" else "($withCurrency)"
        else -> withCurrency
    }
}




fun Int.formatCounter(): String {
    val minutes = this / 60
    val secs = this % 60
    return String.format(Locale.US,"%02d:%02d", minutes, secs)
}
