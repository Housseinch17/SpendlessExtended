@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.spendless.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.spendless.R
import com.example.spendless.core.database.user.model.Currency
import com.example.spendless.core.presentation.designsystem.SpendLessIcons

@Composable
fun CurrenciesDropDownMenu(
    modifier: Modifier = Modifier,
    currenciesList: List<Currency>,
    selectedItem: Currency,
    onSelectItem: (Currency) -> Unit,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    closeExpand: () -> Unit
) {
    DropDownMenu(
        modifier = modifier,
        itemsList = currenciesList,
        selectedItem = selectedItem,
        onSelectItem = onSelectItem,
        text = { currency ->
            Text(
                text = "${currency.name} (${currency.code})",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        leading = { currency ->
            Text(
                text = currency.symbol,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        isExpanded = isExpanded,
        onExpand = onExpand,
        closeExpand = closeExpand,
        showLeading = true
    )
}

@Composable
fun <T> DropDownMenu(
    modifier: Modifier = Modifier,
    itemsList: List<T>,
    selectedItem: T,
    onSelectItem: (T) -> Unit,
    text: @Composable ((T) -> Unit),
    leading: @Composable ((T) -> Unit),
    isExpanded: Boolean,
    onExpand: () -> Unit,
    closeExpand: () -> Unit,
    showLeading: Boolean,
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
                SelectedDropDownMenuItem(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                        .menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = true
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
                    leading = { item ->
                        leading(item)
                    },
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
                    DropDownMenuItemsList(
                        modifier = Modifier.fillMaxWidth(),
                        itemsList = itemsList,
                        selectedItem = selectedItem,
                        onSelectItem = onSelectItem,
                        closeExpand = closeExpand,
                        text = text,
                        leading = leading,
                        showLeading = showLeading
                    )
                }
            }
        )
    }
}

@Composable
fun <T> DropDownMenuItemsList(
    modifier: Modifier = Modifier,
    itemsList: List<T>,
    selectedItem: T,
    onSelectItem: (T) -> Unit,
    closeExpand: () -> Unit,
    text: @Composable (T) -> Unit,
    leading: @Composable ((T) -> Unit),
    showLeading: Boolean,
) {
    itemsList.forEach { item ->
        DropDownMenuItem(
            modifier = modifier,
            item = item,
            selectedItem = selectedItem,
            onSelectItem = onSelectItem,
            closeExpand = closeExpand,
            text = text,
            leading = leading,
            showLeading = showLeading
        )
    }
}

@Composable
fun <T> DropDownMenuItem(
    modifier: Modifier = Modifier,
    item: T,
    selectedItem: T,
    onSelectItem: (T) -> Unit,
    closeExpand: () -> Unit,
    text: @Composable (T) -> Unit,
    leading: @Composable (T) -> Unit,
    showLeading: Boolean,
) {
    DropdownMenuItem(
        modifier = modifier,
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        text = {
            text(item)
        },
        onClick = {
            onSelectItem(item)
            closeExpand()
        },
        leadingIcon = {
            if(showLeading) {
                leading(item)
            }
        },
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
fun <T> SelectedDropDownMenuItem(
    modifier: Modifier = Modifier,
    text: @Composable (T) -> Unit,
    item: T,
    isExpanded: Boolean,
    leading: @Composable (T) -> Unit
) {
    DropdownMenuItem(
        modifier = modifier,
        text = {
            text(item)
        },
        onClick = {
        },
        leadingIcon = {
            leading(item)
        },
        trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
        },
        enabled = true,
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
    )
}