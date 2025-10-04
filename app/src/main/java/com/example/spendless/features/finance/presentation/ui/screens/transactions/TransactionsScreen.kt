package com.example.spendless.features.finance.presentation.ui.screens.transactions

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.spendless.R
import com.example.spendless.core.presentation.designsystem.SpendLessIcons
import com.example.spendless.features.finance.presentation.designsystem.components.CreateTransactionModalBottomSheet
import com.example.spendless.features.finance.presentation.designsystem.components.TransactionsList
import com.example.spendless.features.finance.presentation.ui.common.SharedActions
import com.example.spendless.features.finance.presentation.ui.common.SharedActions.TransactionsActions

@Composable
fun TransactionsScreen(
    modifier: Modifier = Modifier,
    transactionsUiState: TransactionsUiState,
    transactionsActions: (SharedActions) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(64.dp),
                onClick = {
                    transactionsActions(SharedActions.ShowBottomBar)
                },
                shape = MaterialTheme.shapes.medium,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ) {
                Icon(
                    imageVector = SpendLessIcons.Add,
                    contentDescription = stringResource(R.string.create_transaction),
                    tint = LocalContentColor.current
                )
            }
        },
        topBar = {
            TransactionsTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                onBackClick = {
                    transactionsActions(TransactionsActions.NavigateBack)
                },
                onDownloadClick = {
                    transactionsActions(TransactionsActions.ExportData)
                }
            )
        },
    ) { innerPadding ->
        TransactionsList(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            showTransactionText = false,
            transactionsByDate = transactionsUiState.bottomSheetUiState.transactionsByDate,
            selectedTransactionItem = transactionsUiState.bottomSheetUiState.selectedTransactionItem,
            onSelectTransaction = { transactionItem ->
                transactionsActions(SharedActions.SelectedTransaction(transactionItem))
            },
            showFloatingActionButton = { showFab->
                transactionsActions(SharedActions.ShowFloatingActionButton(showFab))
            },
        )
        CreateTransactionModalBottomSheet(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .padding(top = 10.dp),
            transactionsUiState = transactionsUiState.bottomSheetUiState,
            transactionsActions = transactionsActions,
        )
    }
}

@Composable
fun TransactionsTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Row(
        modifier = modifier.padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                imageVector = SpendLessIcons.ArrowBack,
                contentDescription = stringResource(R.string.navigate_back),
                tint = Color.Unspecified
            )
        }
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            text = stringResource(R.string.all_transactions),
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        IconButton(
            onClick = onDownloadClick
        ) {
            Icon(
                imageVector = SpendLessIcons.Download,
                contentDescription = stringResource(R.string.export_data),
                tint = Color.Unspecified
            )
        }
    }
}