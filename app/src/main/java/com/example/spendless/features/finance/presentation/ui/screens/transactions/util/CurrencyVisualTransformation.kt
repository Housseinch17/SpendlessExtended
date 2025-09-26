package com.example.spendless.features.finance.presentation.ui.screens.transactions.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.spendless.core.database.user.model.PreferencesFormat
import com.example.spendless.core.presentation.ui.amountFormatter

class CurrencyVisualTransformation(
    private val preferencesFormat: PreferencesFormat,
    private val isExpense: Boolean,
    private val negativeColor: Color,
    private val positiveColor: Color
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }
        val number = digits.toLongOrNull() ?: 0L

        var formatted = if (digits.isNotEmpty()) {
            amountFormatter(number.toString(), preferencesFormat)
        } else {
            ""
        }

        // If not an expense, remove only leading "-" but keep parentheses
        if (!isExpense && formatted.startsWith("-")) {
            formatted = formatted.removePrefix("-")
        }

        val annotated = buildAnnotatedString {
            when {
                formatted.startsWith("-") -> {
                    // Expense negative formatting
                    pushStyle(SpanStyle(color = negativeColor))
                    append(formatted.take(2)) // "-$"
                    pop()
                    append(formatted.drop(2))
                }
                formatted.startsWith("(") && formatted.endsWith(")") -> {
                    // Parentheses formatting (kept even if isExpense = false)
                    pushStyle(SpanStyle(color = if (isExpense) negativeColor else positiveColor))
                    append(formatted.take(2)) // "($"
                    pop()
                    append(formatted.substring(2, formatted.length - 1)) // digits
                    pushStyle(SpanStyle(color = if (isExpense) negativeColor else positiveColor))
                    append(formatted.last()) // ")"
                    pop()
                }

                else -> {
                    // Positive number, optionally color currency symbol
                    pushStyle(SpanStyle(color = if (isExpense) negativeColor else positiveColor))
                    append(formatted.take(1)) // currency symbol
                    pop()
                    append(formatted.drop(1)) // rest of digits
                }
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val diff = annotated.length - digits.length
                return (offset + diff).coerceIn(0, annotated.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val diff = annotated.length - digits.length
                return (offset - diff).coerceIn(0, digits.length)
            }
        }

        return TransformedText(annotated, offsetMapping)
    }
}

