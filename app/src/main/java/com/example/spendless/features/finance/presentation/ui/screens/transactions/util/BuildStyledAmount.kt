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
            val containsParanthesis = displayText.contains("(")
            when {
                displayText.startsWith("-") -> {
                    //for example if starts with -$ only color -$ but if starts with -($ should color -($
                    val count = if(containsParanthesis) 3 else 2
                    pushStyle(SpanStyle(color = color))
                    append(displayText.take(count))
                    pop()
                    if(containsParanthesis){
                        append(displayText.substring(count, displayText.length - 1))
                    }else{
                        append(displayText.drop(count))
                    }
                    if(displayText.endsWith(")")){
                        pushStyle(SpanStyle(color = color))
                        append(displayText.last())
                        pop()
                    }
                }
                else -> {
                    val count = if(containsParanthesis) 2 else 1
                    pushStyle(SpanStyle(color = color))
                    append(displayText.take(count))
                    pop()
                    if(containsParanthesis){
                        append(displayText.substring(count, displayText.length - 1))
                    }else{
                        append(displayText.drop(count))
                    }
                    if(containsParanthesis){
                        pushStyle(SpanStyle(color = color))
                        append(displayText.last())
                    }
                }
            }
        }
    }
}
