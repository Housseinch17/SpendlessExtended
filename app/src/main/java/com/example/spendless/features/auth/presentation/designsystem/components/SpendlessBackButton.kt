package com.example.spendless.features.auth.presentation.designsystem.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.spendless.R
import com.example.spendless.core.presentation.designsystem.SpendLessIcons

@Composable
fun SpendlessBackButton(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = onBackClick
    ) {
        Icon(
            modifier = Modifier,
            imageVector = SpendLessIcons.ArrowBack,
            contentDescription = stringResource(R.string.back)
        )
    }
}