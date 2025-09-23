package com.example.spendless.features.auth.presentation.ui.screens.repeatPin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import com.example.spendless.R
import com.example.spendless.features.auth.presentation.designsystem.components.AuthHeader
import com.example.spendless.features.auth.presentation.designsystem.components.PinBody
import com.example.spendless.features.auth.presentation.designsystem.components.SpendlessBackButton
import com.example.spendless.features.auth.presentation.designsystem.components.SpendlessBanner
import com.example.spendless.features.auth.presentation.ui.common.PinActions

@Composable
fun RepeatPinScreen(
    modifier: Modifier = Modifier,
    repeatPinUiState: RepeatPinUiState,
    pinActions: (PinActions) -> Unit
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            SpendlessBanner(
                modifier = Modifier.fillMaxWidth(),
                text = repeatPinUiState.bannerText
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            AuthHeader(
                modifier = Modifier,
                header = stringResource(R.string.repeat_your_pin),
                body = buildAnnotatedString { append(stringResource(R.string.enter_yourPin_again)) }
            ) { repeatPinModifier ->
                PinBody(
                    modifier = repeatPinModifier.fillMaxWidth(),
                    basePinUiState = repeatPinUiState,
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
}
