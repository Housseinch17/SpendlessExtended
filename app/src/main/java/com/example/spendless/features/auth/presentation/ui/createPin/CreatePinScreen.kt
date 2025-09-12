package com.example.spendless.features.auth.presentation.ui.createPin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.spendless.R
import com.example.spendless.features.auth.presentation.designsystem.components.AuthHeader
import com.example.spendless.features.auth.presentation.designsystem.components.PinBody
import com.example.spendless.features.auth.presentation.designsystem.components.SpendlessBackButton
import com.example.spendless.features.auth.presentation.ui.common.PinActions

@Composable
fun CreatePinScreen(
    modifier: Modifier = Modifier,
    createPinUiState: CreatePinUiState,
    pinActions: (PinActions) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        AuthHeader(
            modifier = Modifier.fillMaxWidth(),
            header = stringResource(R.string.create_pin),
            body = stringResource(R.string.use_pin_to_login)
        ) { createPinModifier ->
            PinBody(
                modifier = createPinModifier.fillMaxWidth(),
                basePinUiState = createPinUiState,
                pinActions = pinActions
            )
        }
        SpendlessBackButton(
            modifier = Modifier,
            onBackClick = {
                pinActions(PinActions.NavigateBack)
            }
        )
    }
}
