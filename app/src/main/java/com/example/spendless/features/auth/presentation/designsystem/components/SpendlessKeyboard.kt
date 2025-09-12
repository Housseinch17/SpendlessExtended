package com.example.spendless.features.auth.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendless.R
import com.example.spendless.core.presentation.designsystem.SpendLessIcons
import com.example.spendless.core.presentation.designsystem.onPrimaryFixed
import com.example.spendless.core.presentation.designsystem.onSurfaceOpacity12
import com.example.spendless.core.presentation.designsystem.primaryFixed
import com.example.spendless.features.auth.presentation.designsystem.Constants.DELETE_CHAR


@Composable
fun SpendlessKeyboard(
    modifier: Modifier = Modifier,
    keys: List<String?>,
    isEnabled: Boolean,
    isBackspaceEnabled: Boolean,
    onItemClick: (String) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(bottom = 20.dp),
    ) {
        items(keys) { key ->
            KeyboardItem(
                modifier = Modifier.fillMaxWidth(),
                text = key,
                isEnabled = isEnabled,
                isBackspaceEnabled = isBackspaceEnabled,
                onItemClick = { key ->
                    onItemClick(key)
                }
            )
        }
    }
}

@Composable
fun KeyboardItem(
    modifier: Modifier = Modifier,
    text: String?,
    isEnabled: Boolean,
    isBackspaceEnabled: Boolean,
    onItemClick: (String) -> Unit,
) {
    val enabled by rememberSaveable(isEnabled, isBackspaceEnabled) {
        mutableStateOf(if (text == DELETE_CHAR) isBackspaceEnabled else isEnabled)
    }
    text?.let { key ->
        //aspectRation(1f) width = height
        Box(
            modifier = modifier
                .clip(MaterialTheme.shapes.extraLarge)
                .aspectRatio(1f)
                .clickable(
                    enabled = enabled,
                    onClick = { onItemClick(key) },
                )
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = if (enabled) MaterialTheme.colorScheme.primaryFixed else MaterialTheme.colorScheme.onSurfaceOpacity12
                ),
            contentAlignment = Alignment.Center
        ) {
            if(key != DELETE_CHAR) {
                Text(
                    modifier = Modifier,
                    text = key,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = MaterialTheme.colorScheme.onPrimaryFixed,
                        letterSpacing = 0.1.sp
                    )
                )
            }else{
                Icon(
                    modifier = Modifier.size(50.dp),
                    imageVector = SpendLessIcons.Backspace,
                    contentDescription = stringResource(R.string.backspace),
                    tint = MaterialTheme.colorScheme.onPrimaryFixed
                )
            }
        }
    }
}