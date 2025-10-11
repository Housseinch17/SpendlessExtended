package com.example.spendless.features.finance.presentation.ui.screens.transactions

import com.example.spendless.core.data.constant.Constants
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.core.presentation.ui.amountFormatter
import com.example.spendless.features.finance.data.model.ExportFormat
import com.example.spendless.features.finance.data.model.ExportRange
import com.example.spendless.features.finance.data.model.TransactionItem
import com.example.spendless.features.finance.presentation.ui.common.BottomSheetUiState

data class TransactionsUiState(
    val username: String = "",
    val isLoading: Boolean = true,
    val bottomSheetUiState: BottomSheetUiState = BottomSheetUiState(),
    val transactionsByDate: Map<UiText, List<TransactionItem>> = emptyMap(),
    val showExportBottomSheet: Boolean = false,
    val exportRangeList: List<ExportRange> = Constants.exportRangeList,
    val selectedExportRange: ExportRange = Constants.exportRangeList.first(),
    val isExportRangeExpand: Boolean = false,
    val exportFormatList: List<ExportFormat> = Constants.exportFormatList,
    val selectedExportFormat: ExportFormat = Constants.exportFormatList.first(),
    val isExportFormatExpand: Boolean = false,
    val isExportButtonLoading: Boolean = false,
    val transactionsToExport: List<TransactionItem>? = null
){
    val isExportButtonEnabled: Boolean = (selectedExportRange !is ExportRange.SpecificMonth || (selectedExportRange.date != null)) && !isExportButtonLoading
    val dropDownMenusEnabled: Boolean = !isExportButtonLoading
    val formattedTransactionToExport = transactionsToExport?.map {
        it.copy(
            price = amountFormatter(
                total = it.price,
                isExpense = false,
                preferencesFormat = bottomSheetUiState.preferencesFormat
            )
        )
    }
}