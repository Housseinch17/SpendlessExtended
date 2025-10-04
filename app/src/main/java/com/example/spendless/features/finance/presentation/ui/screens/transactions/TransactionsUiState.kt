package com.example.spendless.features.finance.presentation.ui.screens.transactions

import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.features.finance.data.model.TransactionItem
import com.example.spendless.features.finance.presentation.ui.common.BottomSheetUiState

data class TransactionsUiState(
    val bottomSheetUiState: BottomSheetUiState = BottomSheetUiState(),
    val transactionsByDate: Map<UiText, List<TransactionItem>> = emptyMap(),
)
