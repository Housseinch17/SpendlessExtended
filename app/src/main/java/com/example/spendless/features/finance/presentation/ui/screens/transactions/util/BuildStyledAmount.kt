package com.example.spendless.features.finance.presentation.ui.screens.transactions.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString

object BuildStyledAmount {
    fun buildStyledAmount(
        formatted: String,
        isExpense: Boolean,
        color: Color,
    ): AnnotatedString {
        // Remove leading "-" if it's not an expense
        val displayText = if (!isExpense && formatted.startsWith("-")) {
            formatted.removePrefix("-")
        } else {
            formatted
        }

        return buildAnnotatedString {
            when {
                displayText.startsWith("-") -> {
                    pushStyle(SpanStyle(color = color))
                    append(displayText.take(2)) // "-$"
                    pop()
                    append(displayText.drop(2)) // rest
                }
                displayText.startsWith("(") && displayText.endsWith(")") -> {
                    pushStyle(SpanStyle(color = color))
                    append(displayText.take(2)) // "($"
                    pop()
                    append(displayText.substring(2, displayText.length - 1)) // digits
                    pushStyle(SpanStyle(color = color))
                    append(displayText.last()) // ")"
                    pop()
                }
                else -> append(displayText)
            }
        }
    }
}
