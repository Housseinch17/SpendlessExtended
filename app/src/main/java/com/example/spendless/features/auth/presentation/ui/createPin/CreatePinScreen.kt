package com.example.spendless.features.auth.presentation.ui.createPin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.spendless.R
import com.example.spendless.core.presentation.designsystem.SpendLessIcons
import com.example.spendless.features.auth.presentation.designsystem.components.AuthHeader
import com.example.spendless.features.auth.presentation.designsystem.components.SpendlessBackButton
import com.example.spendless.features.auth.presentation.designsystem.components.SpendlessKeyboard

@Composable
fun CreatePinScreen(
    modifier: Modifier = Modifier,
    createPinUiState: CreatePinUiState,
    createPinActions: (CreatePinActions) -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        AuthHeader(
            modifier = Modifier.fillMaxWidth(),
            header = stringResource(R.string.create_pin),
            body = stringResource(R.string.use_pin_to_login)
        ) { createPinModifier ->
            CreatePinBody(
                modifier = createPinModifier.fillMaxWidth(),
                createPinUiState = createPinUiState,
                createPinActions = createPinActions
            )
        }
        SpendlessBackButton(
            modifier = Modifier,
            onBackClick = {
                createPinActions(CreatePinActions.NavigateBack)
            }
        )
    }
}

@Composable
fun CreatePinBody(
    modifier: Modifier = Modifier,
    createPinUiState: CreatePinUiState,
    createPinActions: (CreatePinActions) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CreatePinEllipses(
            modifier = Modifier,
            ellipsesList = createPinUiState.ellipsesList
        )

        SpendlessKeyboard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            keys = createPinUiState.keys,
            isEnabled = createPinUiState.isEnabled,
            isBackspaceEnabled = createPinUiState.isBackspaceEnabled,
            onItemClick = { pin ->
                createPinActions(CreatePinActions.UpdatePin(newPin = pin))
            }
        )
    }
}

@Composable
fun CreatePinEllipses(
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