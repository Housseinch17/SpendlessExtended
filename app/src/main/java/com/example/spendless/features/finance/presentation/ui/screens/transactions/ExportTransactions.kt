package com.example.spendless.features.finance.presentation.ui.screens.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spendless.R
import com.example.spendless.core.presentation.designsystem.SpendLessIcons
import com.example.spendless.features.finance.data.model.ExportFormat
import com.example.spendless.features.finance.data.model.ExportRange
import com.example.spendless.features.finance.presentation.designsystem.components.ExportDataDropDownMenu
import com.example.spendless.features.finance.presentation.designsystem.components.FeatureFinanceButton
import com.example.spendless.features.finance.presentation.ui.common.SharedActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportTransactions(
    modifier: Modifier = Modifier,
    transactionsUiState: TransactionsUiState,
    transactionsActions: (SharedActions) -> Unit,
) {
    //skipPartiallyExpanded = false means it will show first the content as half of screen size
    //skipPartiallyExpanded = true the modal bottom sheet will take it's max size at first
    //by default skipPartiallyExpanded is false
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val verticalScroll = rememberScrollState()

    if (transactionsUiState.showExportBottomSheet) {
        ModalBottomSheet(
            modifier = modifier,
            sheetState = sheetState,
            onDismissRequest = {
                transactionsActions(SharedActions.TransactionsActions.DismissExportBottomSheet)
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            dragHandle = null
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(verticalScroll)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                ExportTransactionsHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    onClose = {
                        transactionsActions(SharedActions.TransactionsActions.DismissExportBottomSheet)
                    }
                )

                ExportTransactionsBody(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    exportRangeList = transactionsUiState.exportRangeList,
                    selectedExportRange = transactionsUiState.selectedExportRange,
                    isExpand = transactionsUiState.isExportRangeExpand,
                    enabled = transactionsUiState.dropDownMenusEnabled,
                    onExpand = { isExpand ->
                        transactionsActions(
                            SharedActions.TransactionsActions.UpdateDropDownExportRangeExpand(
                                isExpand
                            )
                        )
                    },
                    onSelectExportRange = { selectedExportRange ->
                        transactionsActions(
                            SharedActions.TransactionsActions.UpdateSelectedExportRange(
                                selectedExportRange
                            )
                        )
                    },
                    isExportButtonEnabled = transactionsUiState.isExportButtonEnabled,
                    exportFormatList = transactionsUiState.exportFormatList,
                    selectedExportFormat = transactionsUiState.selectedExportFormat,
                    isFormatExpand = transactionsUiState.isExportFormatExpand,
                    onFormatExpand = { isExpand ->
                        transactionsActions(
                            SharedActions.TransactionsActions.UpdateDropDownExportFormatExpand(
                                isExpand
                            )
                        )
                    },
                    onSelectExportFormat = { selectedExportFormat ->
                        transactionsActions(
                            SharedActions.TransactionsActions.UpdateSelectedExportFormat(
                                selectedExportFormat
                            )
                        )
                    },
                    isExportButtonLoading = transactionsUiState.isExportButtonLoading,
                    onExportButtonClick = {
                        transactionsActions(SharedActions.TransactionsActions.ExportData)
                    },
                    onShowSpecificMonthBack = {
                        transactionsActions(SharedActions.TransactionsActions.ShowExportRangeList)
                    }
                )
            }
        }
    }
}

@Composable
fun ExportTransactionsHeader(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.export),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                modifier = Modifier,
                text = stringResource(R.string.export_transactions_to_csv_or_pdf_format),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        IconButton(
            modifier = Modifier,
            onClick = onClose
        ) {
            Icon(
                imageVector = SpendLessIcons.Close,
                contentDescription = stringResource(R.string.close)
            )
        }
    }
}

@Composable
fun ExportTransactionsBody(
    modifier: Modifier = Modifier,
    exportRangeList: List<ExportRange>,
    selectedExportRange: ExportRange,
    isExpand: Boolean,
    enabled: Boolean,
    onExpand: (Boolean) -> Unit,
    onSelectExportRange: (ExportRange) -> Unit,
    exportFormatList: List<ExportFormat>,
    selectedExportFormat: ExportFormat,
    isExportButtonEnabled: Boolean,
    isFormatExpand: Boolean,
    onFormatExpand: (Boolean) -> Unit,
    onSelectExportFormat: (ExportFormat) -> Unit,
    isExportButtonLoading: Boolean,
    onExportButtonClick: () -> Unit,
    onShowSpecificMonthBack: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(R.string.export_range),
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        )
        ExportRangeDropDownMenu(
            modifier = Modifier,
            exportRangeList = exportRangeList,
            selectedExportRange = selectedExportRange,
            isExpand = isExpand,
            onExpand = onExpand,
            enabled = enabled,
            onSelectExportRange = onSelectExportRange,
            onShowSpecificMonthBack = onShowSpecificMonthBack
        )

        Text(
            modifier = Modifier.padding(top = 24.dp),
            text = stringResource(R.string.export_format),
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        )

        ExportFormatDropDownMenu(
            modifier = Modifier,
            exportFormatList = exportFormatList,
            selectedExportFormat = selectedExportFormat,
            isExpand = isFormatExpand,
            enabled = enabled,
            onExpand = onFormatExpand,
            onSelectExportFormat = onSelectExportFormat,
        )

        FeatureFinanceButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            buttonResId = R.string.export,
            isButtonEnabled = isExportButtonEnabled,
            isButtonLoading = isExportButtonLoading,
            onClick = onExportButtonClick
        )
    }
}

@Composable
fun ExportRangeDropDownMenu(
    modifier: Modifier = Modifier,
    exportRangeList: List<ExportRange>,
    selectedExportRange: ExportRange,
    isExpand: Boolean,
    enabled: Boolean,
    onExpand: (Boolean) -> Unit,
    onSelectExportRange: (ExportRange) -> Unit,
    onShowSpecificMonthBack: () -> Unit,
) {
    ExportDataDropDownMenu(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        itemsList = exportRangeList,
        selectedItem = selectedExportRange,
        onSelectItem = { selectedExportRange ->
            onSelectExportRange(selectedExportRange)
        },
        enabled = enabled,
        text = { exportRange ->
            Text(
                modifier = Modifier,
                text = if (exportRange is ExportRange.SpecificMonth && exportRange.date != null) exportRange.date else stringResource(exportRange.exportRes),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            )
        },
        isExpanded = isExpand,
        onExpand = {
            onExpand(true)
        },
        closeExpand = {
            onExpand(false)
        },
        onShowSpecificMonthBack = onShowSpecificMonthBack
    )
}

@Composable
fun ExportFormatDropDownMenu(
    modifier: Modifier = Modifier,
    exportFormatList: List<ExportFormat>,
    selectedExportFormat: ExportFormat,
    isExpand: Boolean,
    enabled: Boolean,
    onExpand: (Boolean) -> Unit,
    onSelectExportFormat: (ExportFormat) -> Unit,
) {
    ExportDataDropDownMenu(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        itemsList = exportFormatList,
        selectedItem = selectedExportFormat,
        onSelectItem = { selectedExportFormat ->
            onSelectExportFormat(selectedExportFormat)
        },
        enabled = enabled,
        text = { exportFormat ->
            Text(
                modifier = Modifier,
                text = exportFormat.exportFormat,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            )
        },
        isExpanded = isExpand,
        onExpand = {
            onExpand(true)
        },
        closeExpand = {
            onExpand(false)
        },
    )
}
