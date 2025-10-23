package com.example.spendless.core.presentation.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.spendless.core.presentation.designsystem.primaryContainerOpacity8
import com.example.spendless.features.finance.presentation.ui.common.formatItem
import timber.log.Timber

@Composable
fun <T> SpendlessSeparatorList(
    modifier: Modifier = Modifier,
    list: List<T>,
    enabledItem: T,
    onClick: (T) -> Unit,
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = fadeOut(animationSpec = tween(durationMillis = 300))
    ) {
        Row(
            modifier = modifier.background(
                color = MaterialTheme.colorScheme.primaryContainerOpacity8,
                shape = MaterialTheme.shapes.medium
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            list.forEach { item ->
                SpendlessSeparatorItem(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    item = item,
                    enabledItem = enabledItem,
                    onClick = {
                        onClick(item)
                    }
                )
            }
        }
    }
}

@Composable
fun <T> SpendlessSeparatorItem(
    modifier: Modifier = Modifier,
    item: T,
    enabledItem: T,
    onClick: () -> Unit,
) {
    Text(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(
                color = if (item == enabledItem) MaterialTheme.colorScheme.surfaceContainerLowest else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .clickable(onClick = onClick)
            .padding(6.dp),
        //we might use item as StringRes or CounterPerTimeUnit
        text = formatItem(item).asString(),
        style = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    )
}