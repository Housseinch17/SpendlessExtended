package com.example.spendless.features.auth.presentation.ui.pinPrompt

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spendless.R
import com.example.spendless.core.presentation.designsystem.SpendLessIcons
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.features.auth.presentation.designsystem.components.AuthHeader
import com.example.spendless.features.auth.presentation.designsystem.components.PinBody
import com.example.spendless.features.auth.presentation.designsystem.components.SpendlessBanner
import com.example.spendless.features.auth.presentation.ui.common.PinActions

@Composable
fun PinPromptScreen(
    modifier: Modifier = Modifier,
    promptUIState: PinPromptUIState,
    pinActions: (PinActions) -> Unit,
) {
    //disable backButton
    BackHandler(enabled = true) {
        //do nothing
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            SpendlessBanner(
                modifier = Modifier.fillMaxWidth(),
                text = promptUIState.bannerText
            )
        }
    ) { innerPadding ->
        PinPromptHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            headerUiText = promptUIState.headerUiText,
            headerText = promptUIState.headerText ?: "",
            bodyUiText = promptUIState.bodyUiText,
            bodyCounter = promptUIState.counter,
            onLogOut = {
                pinActions(PinActions.PromptPinActions.LogOut)
            },
        ){ pinPromptModifier->
            PinPromptBody(
                modifier = pinPromptModifier.fillMaxWidth(),
                promptUIState = promptUIState,
                pinActions = pinActions
            )
        }
    }
}

@Composable
fun PinPromptHeader(
    modifier: Modifier = Modifier,
    headerUiText: UiText,
    headerText: String,
    bodyUiText: UiText,
    bodyCounter: String? = null,
    onLogOut: () -> Unit,
    bodyContent: @Composable (Modifier) -> Unit,
) {
    Box(
        modifier = modifier,
    ) {
        AuthHeader(
            modifier = Modifier
                .fillMaxWidth(),
            header = headerUiText.asString() + headerText,
            body = buildAnnotatedString {
                append(bodyUiText.asString())
                bodyCounter?.let { bodyCounter ->
                    append(" ")
                    pushStyle(
                        MaterialTheme.typography.bodyMedium.toSpanStyle().copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    append(bodyCounter)
                    pop()
                }
            },
        ) { pinPromptModifier->
            bodyContent(pinPromptModifier)
        }

        LogOutIcon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 14.dp),
            onLogOut = onLogOut
        )
    }
}

@Composable
fun PinPromptBody(
    modifier: Modifier = Modifier,
    promptUIState: PinPromptUIState,
    pinActions: (PinActions) -> Unit,
){
    PinBody(
        modifier = modifier,
        basePinUiState = promptUIState,
        pinActions = pinActions
    )
}

@Composable
fun LogOutIcon(
    modifier: Modifier = Modifier,
    onLogOut: () -> Unit,
){
    IconButton(
        modifier = modifier,
        onClick = onLogOut,
    ){
        Icon(
            imageVector = SpendLessIcons.Logout,
            contentDescription = stringResource(R.string.logout),
            tint = Color.Unspecified
        )
    }
}