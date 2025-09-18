package com.example.spendless.features.auth.presentation.ui.logIn

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.spendless.R
import com.example.spendless.core.presentation.designsystem.components.SpendlessButton
import com.example.spendless.core.presentation.designsystem.components.SpendlessTextButton
import com.example.spendless.features.auth.presentation.designsystem.components.AuthHeader
import com.example.spendless.features.auth.presentation.designsystem.components.SpendlessBanner

@Composable
fun LogInScreen(
    modifier: Modifier = Modifier,
    logInUiState: LogInUiState,
    logInActions: (LogInActions) -> Unit,
) {
    val state = rememberScrollState()
    Scaffold(
        modifier = modifier,
        bottomBar = {
            SpendlessBanner(
                modifier = Modifier.fillMaxWidth(),
                text = logInUiState.bannerText
            )
        }
    ) { innerPadding ->
        AuthHeader(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = state)
                .padding(bottom = innerPadding.calculateBottomPadding()),
            header = stringResource(R.string.welcome_back),
            body = buildAnnotatedString { append(stringResource(R.string.enter_your_login_details))}
        ) { logInModifier ->
            LogInBody(
                modifier = logInModifier.fillMaxWidth(),
                logInUiState = logInUiState,
                logInActions = logInActions
            )
        }
    }
}

@Composable
fun LogInBody(
    modifier: Modifier = Modifier,
    logInUiState: LogInUiState,
    logInActions: (LogInActions) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        LogInTextField(
            modifier = Modifier.fillMaxWidth(),
            value = logInUiState.username,
            placeHolderResId = R.string.username,
            isError = logInUiState.isUsernameError,
            errorText = logInUiState.usernameError?.asString() ?: "",
            onValueChange = { newUsername ->
                logInActions(LogInActions.UpdateUsername(username = newUsername))
            }
        )

        LogInTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            value = logInUiState.pin,
            placeHolderResId = R.string.pin,
            isError = logInUiState.isPinError,
            errorText = logInUiState.pinError?.asString() ?: "",
            isKeyboardNumber = true,
            isLastField = true,
            onDone = {
                logInActions(LogInActions.LogIn)
            },
            onValueChange = { newPin ->
                logInActions(LogInActions.UpdatePin(pin = newPin))
            }
        )
        SpendlessButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            enabled = logInUiState.isLogInButtonEnabled,
            onClick = {
                logInActions(LogInActions.LogIn)
            }
        ) {
            if (!logInUiState.isLogInButtonLoading) {
                Text(
                    text = stringResource(R.string.login),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (logInUiState.isLogInButtonEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        SpendlessTextButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.new_to_spendless)
        ) {
            logInActions(LogInActions.NavigateToRegister)
        }
    }
}

@Composable
fun LogInTextField(
    modifier: Modifier = Modifier,
    value: String,
    @StringRes placeHolderResId: Int,
    isError: Boolean,
    errorText: String,
    isKeyboardNumber: Boolean = false,
    isLastField: Boolean = false,
    onDone: () -> Unit = {},
    onValueChange: (String) -> Unit,
) {
    //keyboard controller to show or hide keyboard
    val keyboardController = LocalSoftwareKeyboardController.current
    //current focus manager if focused or not
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = true,
                    ambientColor = Color.Transparent,
                    spotColor = MaterialTheme.colorScheme.onSurface
                ),
            shape = MaterialTheme.shapes.medium,
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            placeholder = {
                Text(
                    text = stringResource(placeHolderResId),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            },
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                errorContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (!isKeyboardNumber) KeyboardType.Text else KeyboardType.NumberPassword,
                imeAction = if (!isLastField) ImeAction.Next else ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                },
                onDone = {
                    //close keyboard
                    keyboardController?.hide()
                    //clear focus
                    focusManager.clearFocus()

                    //do the function
                    onDone()
                }
            ),
            maxLines = 1
        )

        if (isError) {
            Text(
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                text = errorText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.error,
                )
            )
        }

    }
}