package com.example.spendless.core.presentation.ui

import com.example.spendless.core.database.user.model.PreferencesFormat
import java.util.Locale


fun amountFormatter(
    total: String,
    preferencesFormat: PreferencesFormat
): String {
    if(total.isNotEmpty()) {
        //Parse total as Long
        val totalLong = total.toLongOrNull() ?: 0L

        //Determine decimal separator: "1.00" -> ".", "1,00" -> ","
        val decimalSeparator = if (preferencesFormat.decimalSeparator.contains(",")) "," else "."

        //Determine thousands separator
        val thousandsSeparator = when {
            preferencesFormat.thousandsSeparator.contains(".") -> "."
            preferencesFormat.thousandsSeparator.contains(",") -> ","
            preferencesFormat.thousandsSeparator.contains(" ") -> " "
            else -> ","
        }

        //Split total into integer part and decimal part (last 2 digits are decimals)
        val integerPart = (totalLong / 100).toString() //1038245 / 100 = 10382
        val decimalPart = (totalLong % 100).toString().padStart(2, '0') //45

        //Format integer part with thousands separator
        val formattedInteger = integerPart.reversed()
            .chunked(3)
            .joinToString(thousandsSeparator)
            .reversed()

        val formattedNumber = "$formattedInteger$decimalSeparator$decimalPart"

        //Add currency symbol
        val withCurrency = "${preferencesFormat.currency.symbol}$formattedNumber"

        //Apply expenses format
        return when {
            preferencesFormat.expenses.startsWith("-") -> "-$withCurrency"
            preferencesFormat.expenses.startsWith("(") -> "($withCurrency)"
            else -> withCurrency
        }
    }
    else{
        return ""
    }
}

fun Int.formatCounter(): String {
    val minutes = this / 60
    val secs = this % 60
    return String.format(Locale.US,"%02d:%02d", minutes, secs)
}
