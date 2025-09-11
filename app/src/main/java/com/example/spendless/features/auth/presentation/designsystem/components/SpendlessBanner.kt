package com.example.spendless.features.auth.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.spendless.core.presentation.ui.UiText

@Composable
fun SpendlessBanner(
    modifier: Modifier = Modifier,
    text: UiText?,
) {
    text?.let { bannerText ->
        Text(
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.error)
                .imePadding()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
            text = bannerText.asString(),
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onError,
            ),
            textAlign = TextAlign.Center
        )
    }
}