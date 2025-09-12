package com.example.spendless.features.auth.presentation.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.spendless.core.presentation.designsystem.SpendLessIcons
import com.example.spendless.R

@Composable
fun SpendlessBackButton(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    IconButton(
        modifier = modifier.padding(vertical = 12.dp, horizontal = 8.dp),
        onClick = onBackClick
    ) {
        Icon(
            imageVector = SpendLessIcons.ArrowBack,
            contentDescription = stringResource(R.string.back)
        )
    }
}