package com.example.spendless.core.presentation.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.spendless.core.presentation.designsystem.onSurfaceOpacity12
import com.example.spendless.core.presentation.designsystem.onSurfaceOpacity30

@Composable
fun SpendlessButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
){
    Button(
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceOpacity12,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceOpacity30
        )
    ) {
        content()
    }
}