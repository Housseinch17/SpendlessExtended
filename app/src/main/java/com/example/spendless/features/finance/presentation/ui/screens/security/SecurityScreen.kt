package com.example.spendless.features.finance.presentation.ui.screens.security

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.spendless.R
import com.example.spendless.core.presentation.designsystem.components.SpendlessButton
import com.example.spendless.core.presentation.designsystem.components.SpendlessSeparatorList
import com.example.spendless.features.auth.data.model.CounterPerTimeUnit
import com.example.spendless.features.finance.presentation.designsystem.components.FinanceTopBar

@Composable
fun SecurityScreen(
    modifier: Modifier = Modifier,
    securityUiState: SecurityUiState,
    securityActions: (SecurityActions) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FinanceTopBar(
                modifier = Modifier,
                isEnabled = securityUiState.isButtonsEnabled,
                onClick = {
                    securityActions(SecurityActions.NavigateBack)
                },
                iconRes = R.string.back,
                titleRes = R.string.security
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                SecurityBiometric(
                    modifier = Modifier.fillMaxWidth(),
                    biometricList = securityUiState.biometricList,
                    enabledItem = securityUiState.selectedBiometric,
                    onClick = { biometric ->
                        securityActions(SecurityActions.UpdateBiometric(biometric))
                    }
                )

                ExpiryDuration(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    biometricList = securityUiState.expiryDurationList,
                    enabledItem = securityUiState.selectedExpiryDuration,
                    onClick = { sessionExpiry ->
                        securityActions(SecurityActions.UpdateExpiryDuration(sessionExpiry))
                    },
                )

                LockedOutDuration(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    biometricList = securityUiState.lockedDurationList,
                    enabledItem = securityUiState.selectedLockedDuration,
                    onClick = { lockedDuration ->
                        securityActions(SecurityActions.UpdateLockedDuration(lockedDuration))
                    },
                )

                SpendlessButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    enabled = securityUiState.isButtonsEnabled,
                    onClick = {
                        securityActions(SecurityActions.OnSave)
                    },
                    content = {
                        if (!(securityUiState.isButtonLoading)) {
                            Text(
                                text = stringResource(R.string.save),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    textAlign = TextAlign.Center
                                )
                            )
                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            }
            if (securityUiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SecurityBiometric(
    modifier: Modifier = Modifier,
    biometricList: List<Int>,
    enabledItem: Int,
    onClick: (Int) -> Unit,
) {
    SecurityItem(
        modifier = modifier,
        title = R.string.biometrics_for_pin_prompt
    ) { contentModifier ->
        SpendlessSeparatorList(
            modifier = contentModifier.fillMaxWidth(),
            list = biometricList,
            enabledItem = enabledItem,
            onClick = onClick
        )
    }
}

@Composable
fun ExpiryDuration(
    modifier: Modifier = Modifier,
    biometricList: List<CounterPerTimeUnit>,
    enabledItem: CounterPerTimeUnit,
    onClick: (CounterPerTimeUnit) -> Unit,
) {
    SecurityItem(
        modifier = modifier,
        title = R.string.session_expiry_duration
    ) { contentModifier ->
        SpendlessSeparatorList(
            modifier = contentModifier.fillMaxWidth(),
            list = biometricList,
            enabledItem = enabledItem,
            onClick = onClick
        )
    }
}

@Composable
fun LockedOutDuration(
    modifier: Modifier = Modifier,
    biometricList: List<CounterPerTimeUnit>,
    enabledItem: CounterPerTimeUnit,
    onClick: (CounterPerTimeUnit) -> Unit,
) {
    SecurityItem(
        modifier = modifier,
        title = R.string.locked_out_duration
    ) { contentModifier ->
        SpendlessSeparatorList(
            modifier = contentModifier.fillMaxWidth(),
            list = biometricList,
            enabledItem = enabledItem,
            onClick = onClick
        )
    }
}

@Composable
fun SecurityItem(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    content: @Composable (Modifier) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(title),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        content(
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}