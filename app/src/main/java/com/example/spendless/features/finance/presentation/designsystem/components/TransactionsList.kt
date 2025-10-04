package com.example.spendless.features.finance.presentation.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.spendless.R
import com.example.spendless.core.presentation.designsystem.bodyXSmall
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.features.finance.data.model.TransactionItem
import com.example.spendless.features.finance.presentation.ui.screens.dashboard.TransactionItem

@Composable
fun TransactionsList(
    modifier: Modifier = Modifier,
    onShowAllClick: () -> Unit = {},
    showTransactionText: Boolean = true,
    transactionsByDate: Map<UiText, List<TransactionItem>>,
    selectedTransactionItem: TransactionItem,
    onSelectTransaction: (TransactionItem) -> Unit,
    showFloatingActionButton: (Boolean) -> Unit,
) {
    val state = rememberLazyListState()
    val showFab by remember {
        derivedStateOf {
            !(state.isScrollInProgress)
        }
    }

    val isLastItemVisible by remember {
        derivedStateOf {
            val lastVisibleItem = state.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index == state.layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(showFab) {
        showFloatingActionButton(showFab)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 12.dp),
        state = state,
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        if (showTransactionText) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.latest_transactions),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    TextButton(
                        modifier = Modifier,
                        onClick = onShowAllClick
                    ) {
                        Text(
                            modifier = Modifier,
                            text = stringResource(R.string.show_all),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        transactionsByDate.forEach { date, transactionList ->
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 4.dp,
                            bottom = 12.dp
                        ),
                    text = date.asString(),
                    style = MaterialTheme.typography.bodyXSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            items(transactionList) { transactionItem ->
                TransactionItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    transactionItem = transactionItem,
                    selectedTransactionItem = selectedTransactionItem,
                    onClick = {
                        if (transactionItem.content != null) {
                            onSelectTransaction(transactionItem)
                        }
                    }
                )
            }
        }

        if (isLastItemVisible) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(R.string.reached_end),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}