package com.example.spendless.features.finance.presentation.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
    onSelectTransaction: (TransactionItem) -> Unit
) {
    val state = rememberLazyListState()

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
                        .padding(top = 4.dp),
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
                        .padding(bottom = 12.dp),
                    transactionItem = transactionItem,
                    selectedTransactionItem = selectedTransactionItem,
                    onClick = {
                        onSelectTransaction(transactionItem)
                    }

                )
            }
        }

    }
}