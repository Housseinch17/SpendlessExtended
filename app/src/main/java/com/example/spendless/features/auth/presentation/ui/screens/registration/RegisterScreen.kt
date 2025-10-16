package com.example.spendless.features.auth.presentation.ui.screens.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.spendless.R
import com.example.spendless.core.presentation.designsystem.SpendLessIcons
import com.example.spendless.core.presentation.designsystem.components.SpendlessButton
import com.example.spendless.core.presentation.designsystem.components.SpendlessTextButton
import com.example.spendless.core.presentation.designsystem.onBackgroundOpacity8
import com.example.spendless.core.presentation.designsystem.onSurfaceOpacity30
import com.example.spendless.features.auth.presentation.designsystem.components.AuthHeader
import com.example.spendless.features.auth.presentation.designsystem.components.SpendlessBanner

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    registerUiState: RegisterUiState,
    registerActions: (RegisterActions) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            SpendlessBanner(
                modifier = Modifier.fillMaxWidth(),
                text = registerUiState.bannerText
            )
        }
    ) { innerPadding ->
        AuthHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            header = stringResource(R.string.register_header),
            body = buildAnnotatedString { append(stringResource(R.string.register_body)) }
        ) { registerBodyModifier ->
            RegisterBody(
                modifier = registerBodyModifier.fillMaxWidth(),
                registerUiState = registerUiState,
                registerActions = registerActions,
            )
        }
    }
}

@Composable
fun RegisterBody(
    modifier: Modifier = Modifier,
    registerUiState: RegisterUiState,
    registerActions: (RegisterActions) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //register textField
        RegisterTextField(
            modifier = Modifier.fillMaxWidth(),
            value = registerUiState.username,
            isError = registerUiState.isUsernameError,
            errorText = registerUiState.usernameError?.asString() ?: "",
            onValueChange = { newUsername ->
                registerActions(RegisterActions.UpdateUsername(username = newUsername))
            },
            enabled = registerUiState.isEnabled,
            onDone = {
                registerActions(RegisterActions.ClickNext)
            }
        )

        RegisterNextButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = registerUiState.isEnabled,
            isNextLoading = registerUiState.isNextLoading,
            onClick = {
                registerActions(RegisterActions.ClickNext)
            }
        )

        SpendlessTextButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(),
            text = stringResource(R.string.register_text_button),
            onClick = {
                registerActions(RegisterActions.ClickAlreadyHaveAnAccount)
            }
        )
    }
}

@Composable
fun RegisterTextField(
    modifier: Modifier = Modifier,
    value: String,
    isError: Boolean,
    errorText: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
) {
    //keyboard controller to show or hide keyboard
    val keyboardController = LocalSoftwareKeyboardController.current
    //current focus manager if focused or not
    val focusManager = LocalFocusManager.current
    TextField(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        maxLines = 2,
        value = value,
        onValueChange = { newUsername ->
            onValueChange(newUsername)
        },
        placeholder = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.username),
                style = MaterialTheme.typography.displayMedium.copy(
                    color = LocalContentColor.current,
                    textAlign = TextAlign.Center
                )
            )
        },
        isError = isError,
        supportingText =
            if (isError) {
                {
                    Text(
                        modifier = Modifier,
                        text = errorText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.error,
                        )
                    )
                }
            } else {
                null
            },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.onBackgroundOpacity8,
            unfocusedContainerColor = MaterialTheme.colorScheme.onBackgroundOpacity8,
            disabledContainerColor = MaterialTheme.colorScheme.onBackgroundOpacity8,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceOpacity30,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceOpacity30,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceOpacity30,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            errorSupportingTextColor = MaterialTheme.colorScheme.error
        ),
        textStyle = MaterialTheme.typography.displayMedium.copy(
            color = LocalContentColor.current,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                if (enabled) {
                    //close keyboard
                    keyboardController?.hide()
                    //clear focus
                    focusManager.clearFocus()
                    //do the function
                    onDone()
                }
            }
        ),
    )
}

@Composable
fun RegisterNextButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    isNextLoading: Boolean,
    onClick: () -> Unit,
) {
    SpendlessButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        content = {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isNextLoading) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(R.string.next),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = LocalContentColor.current,
                        )
                    )
                    Icon(
                        modifier = Modifier.padding(start = 8.dp),
                        imageVector = SpendLessIcons.NavigateNext,
                        contentDescription = stringResource(R.string.next),
                        tint = LocalContentColor.current
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    )
}