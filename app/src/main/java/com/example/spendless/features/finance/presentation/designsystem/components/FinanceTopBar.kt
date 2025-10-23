package com.example.spendless.features.finance.presentation.designsystem.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.spendless.core.presentation.designsystem.SpendLessIcons

@Composable
fun FinanceTopBar(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    onClick: () -> Unit,
    @StringRes iconRes: Int,
    @StringRes titleRes: Int,
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, end = 16.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            enabled = isEnabled,
            onClick = onClick,
        ) {
            Icon(
                imageVector = SpendLessIcons.ArrowBack,
                contentDescription = stringResource(iconRes),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}