package com.example.spendless.features.auth.presentation.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.spendless.R
import com.example.spendless.core.presentation.designsystem.SpendLessIcons

@Composable
fun PinEllipses(
    modifier: Modifier = Modifier,
    ellipsesList: List<Boolean>,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ellipsesList.forEach { ellipse ->
            Icon(
                imageVector = if (ellipse) SpendLessIcons.EllipseOn else SpendLessIcons.EllipseOff,
                contentDescription = if (ellipse) stringResource(R.string.ellipse_on) else stringResource(
                    R.string.ellipse_off
                ),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}