package com.example.spendless.features.finance.presentation.designsystem.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.spendless.core.presentation.designsystem.onSurfaceOpacity12

@Composable
fun FeatureFinanceButton(
    modifier: Modifier = Modifier,
    @StringRes buttonResId: Int,
    isButtonEnabled: Boolean,
    isButtonLoading: Boolean,
    onClick: () -> Unit,
){
    Button(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        onClick = onClick,
        enabled = isButtonEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceOpacity12
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (!isButtonLoading) {
                Text(
                    modifier = Modifier,
                    text = stringResource(buttonResId),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = LocalContentColor.current
                    )
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = LocalContentColor.current
                )
            }
        }
    }
}