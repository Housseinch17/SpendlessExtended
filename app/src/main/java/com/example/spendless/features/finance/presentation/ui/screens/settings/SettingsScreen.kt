package com.example.spendless.features.finance.presentation.ui.screens.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.spendless.R
import com.example.spendless.core.presentation.designsystem.SpendLessIcons
import com.example.spendless.features.finance.presentation.designsystem.components.FinanceTopBar

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    settingsUiState: SettingsUiState,
    settingsActions: (SettingsActions) -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FinanceTopBar(
                modifier = Modifier,
                isEnabled = settingsUiState.isButtonsEnabled,
                onClick = {
                    settingsActions(SettingsActions.NavigateBack)
                },
                iconRes = R.string.back,
                titleRes = R.string.settings
            )
        }
    ) { innerPadding ->
        Box(
          modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 2.dp,
                            shape = MaterialTheme.shapes.medium,
                            clip = false
                        ),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                ) {
                    SettingsItem(
                        modifier = Modifier.fillMaxWidth(),
                        textResId = R.string.preferences,
                    ) {
                        Image(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .clickable(
                                    enabled = settingsUiState.isButtonsEnabled,
                                    onClick = {
                                        settingsActions(SettingsActions.NavigateToPreferences)
                                    }
                                ),
                            painter = painterResource(SpendLessIcons.Preferences),
                            contentDescription = stringResource(R.string.preferences)
                        )
                    }

                    SettingsItem(
                        modifier = Modifier.fillMaxWidth(),
                        textResId = R.string.security,
                    ) {
                        Image(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .clickable(
                                    enabled = settingsUiState.isButtonsEnabled,
                                    onClick = {
                                        settingsActions(SettingsActions.NavigateToSecurity)
                                    }
                                ),
                            painter = painterResource(SpendLessIcons.Lock),
                            contentDescription = stringResource(R.string.security)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .shadow(
                            elevation = 2.dp,
                            shape = MaterialTheme.shapes.medium,
                            clip = false
                        )
                        .clip(MaterialTheme.shapes.medium),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                ) {
                    SettingsItem(
                        modifier = Modifier.fillMaxWidth(),
                        textResId = R.string.logout,
                        color = MaterialTheme.colorScheme.error
                    ) {
                        Image(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .clickable(
                                    enabled = settingsUiState.isButtonsEnabled,
                                    onClick = {
                                        settingsActions(SettingsActions.LogOut)
                                    }
                                ),
                            painter = painterResource(SpendLessIcons.LogoutAsImage),
                            contentDescription = stringResource(R.string.logout)
                        )
                    }
                }
            }

            if(settingsUiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(200.dp).align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    @StringRes textResId: Int,
    color: Color = MaterialTheme.colorScheme.onSurface,
    icon: @Composable () -> Unit,
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Text(
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            text = stringResource(textResId),
            style = MaterialTheme.typography.labelMedium.copy(
                color = color
            )
        )
    }
}