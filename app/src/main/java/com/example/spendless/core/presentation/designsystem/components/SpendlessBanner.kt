package com.example.spendless.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SpendlessBanner(
    modifier: Modifier = Modifier,
    text: String,
    ){
    Text(
        modifier = modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.error).padding(
            horizontal = 16.dp,
            vertical = 12.dp
        ),
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.colorScheme.onError,
        ),
        textAlign = TextAlign.Center
    )
}