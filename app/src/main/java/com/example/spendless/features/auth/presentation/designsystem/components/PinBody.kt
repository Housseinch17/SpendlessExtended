package com.example.spendless.features.auth.presentation.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spendless.features.auth.presentation.ui.common.PinActions
import com.example.spendless.features.auth.presentation.ui.common.BasePinUiState

@Composable
fun PinBody(
    modifier: Modifier = Modifier,
    basePinUiState: BasePinUiState,
    pinActions: (PinActions) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PinEllipses(
            modifier = Modifier,
            ellipsesList = basePinUiState.ellipsesList
        )

        SpendlessKeyboard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            keys = basePinUiState.keys,
            isEnabled = basePinUiState.isEnabled,
            isBackspaceEnabled = basePinUiState.isBackspaceEnabled,
            onItemClick = { pin ->
                pinActions(PinActions.UpdatePin(newPin = pin))
            }
        )
    }
}