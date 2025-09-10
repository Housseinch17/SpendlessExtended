package com.example.spendless.core.presentation.designsystem.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SpendlessTextButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
){
    TextButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Text(
            modifier = Modifier,
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}