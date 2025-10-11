@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.spendless.features.finance.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spendless.R
import com.example.spendless.core.presentation.designsystem.SpendLessIcons
import com.example.spendless.features.finance.data.model.ExportRange

@Composable
fun <T> ExportDataDropDownMenu(
    modifier: Modifier = Modifier,
    itemsList: List<T>,
    selectedItem: T,
    onSelectItem: (T) -> Unit,
    text: @Composable ((T) -> Unit),
    isExpanded: Boolean,
    enabled: Boolean,
    onExpand: () -> Unit,
    closeExpand: () -> Unit,
    onShowSpecificMonthBack: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier,
            expanded = isExpanded,
            onExpandedChange = { onExpand() },
            content = {
                SelectedExportRangeDropDownMenuItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                        .menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = enabled
                        )
                        .shadow(
                            elevation = 1.dp,
                            clip = true,
                            shape = MaterialTheme.shapes.medium
                        )
                        .background(
                            color =
                                MaterialTheme.colorScheme.surfaceContainerLowest,
                            shape = MaterialTheme.shapes.medium
                        ),
                    text = { item ->
                        text(item)
                    },
                    item = selectedItem,
                    isExpanded = isExpanded,
                )
                //here shouldn't use Modifier.fillMaxWidth() otherwise it will fill the width
                //and matchTextFieldWidth = true means it will take full width available
                ExposedDropdownMenu(
                    modifier = Modifier.clip(shape = MaterialTheme.shapes.medium),
                    expanded = isExpanded,
                    onDismissRequest = closeExpand,
                    scrollState = scrollState,
                    matchTextFieldWidth = true,
                    shape = MaterialTheme.shapes.medium,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                ) {
                    ExportDropDownMenuItemsList(
                        modifier = Modifier
                            .fillMaxWidth(),
                        itemsList = itemsList,
                        selectedItem = selectedItem,
                        onSelectItem = onSelectItem,
                        closeExpand = closeExpand,
                        onShowSpecificMonthBack = onShowSpecificMonthBack,
                        text = text,
                    )
                }
            }
        )
    }
}

@Composable
fun <T> ExportDropDownMenuItemsList(
    modifier: Modifier = Modifier,
    itemsList: List<T>,
    selectedItem: T,
    onSelectItem: (T) -> Unit,
    closeExpand: () -> Unit,
    onShowSpecificMonthBack: () -> Unit,
    text: @Composable ((T) -> Unit),
) {
    itemsList.forEachIndexed { index, item ->
        val firstItem = index == 0
        //when showing specific month drop down menu it should show first item that can take us back
        //to normal export range months
        val showSpecificMonthBackButton = item is ExportRange.SpecificMonth && item.date != null
        val isLast = index == itemsList.lastIndex

        Column {
            if (firstItem && showSpecificMonthBackButton) {
                Row(
                    modifier = modifier.background(color = MaterialTheme.colorScheme.surfaceContainer),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onShowSpecificMonthBack
                    ) {
                        Icon(
                            imageVector = SpendLessIcons.ExportBack,
                            contentDescription = stringResource(R.string.export_back),
                            tint = Color.Unspecified
                        )
                    }
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(ExportRange.SpecificMonth().exportRes),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            //check if last item and if it's SpecificMonth
            // and if it's date is null which means that it's not a custom month
            //since custom months have SpecificMonth as type but with a valid date and not null
            if (isLast && item is ExportRange.SpecificMonth && item.date.isNullOrEmpty()) {
                HorizontalDivider(modifier = modifier)
            }
            ExportRangeDropDownMenuItem(
                modifier = modifier,
                item = item,
                selectedItem = selectedItem,
                onSelectItem = onSelectItem,
                closeExpand = closeExpand,
                text = text,
            )
        }
    }
}


@Composable
fun <T> ExportRangeDropDownMenuItem(
    modifier: Modifier = Modifier,
    item: T,
    selectedItem: T,
    onSelectItem: (T) -> Unit,
    closeExpand: () -> Unit,
    text: @Composable (T) -> Unit,
) {
    DropdownMenuItem(
        modifier = modifier,
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        text = {
            text(item)
        },
        onClick = {
            onSelectItem(item)
            //if the selected item is not a custom month, don't close dropdown menu
            if (item !is ExportRange.SpecificMonth || item.date != null) {
                closeExpand()
            }
        },
        leadingIcon = null,
        trailingIcon = {
            if (selectedItem == item) {
                Icon(
                    imageVector = SpendLessIcons.Check,
                    contentDescription = stringResource(R.string.check),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        enabled = true,
    )
}

@Composable
fun <T> SelectedExportRangeDropDownMenuItem(
    modifier: Modifier = Modifier,
    text: @Composable (T) -> Unit,
    item: T,
    enabled: Boolean = true,
    isExpanded: Boolean,
) {
    DropdownMenuItem(
        modifier = modifier,
        text = {
            text(item)
        },
        onClick = {
        },
        leadingIcon = null,
        trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
        },
        enabled = enabled,
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
    )
}