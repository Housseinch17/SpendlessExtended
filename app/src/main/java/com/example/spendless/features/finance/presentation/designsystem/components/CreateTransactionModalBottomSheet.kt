package com.example.spendless.features.finance.presentation.designsystem.components

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.spendless.R
import com.example.spendless.core.data.database.user.model.PreferencesFormat
import com.example.spendless.core.data.model.Category
import com.example.spendless.core.presentation.designsystem.SpendLessIcons
import com.example.spendless.core.presentation.designsystem.components.DropDownMenu
import com.example.spendless.core.presentation.designsystem.onPrimaryFixed
import com.example.spendless.core.presentation.designsystem.onSurfaceOpacity30
import com.example.spendless.core.presentation.designsystem.primaryContainerOpacity8
import com.example.spendless.core.presentation.designsystem.success
import com.example.spendless.features.finance.data.model.PaymentRecurrence
import com.example.spendless.features.finance.presentation.ui.common.BottomSheetUiState
import com.example.spendless.features.finance.presentation.ui.common.SharedActions
import com.example.spendless.features.finance.presentation.ui.screens.transactions.util.BuildStyledAmount
import com.example.spendless.features.finance.presentation.ui.screens.transactions.util.CurrencyVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTransactionModalBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetUiState: BottomSheetUiState,
    transactionsActions: (SharedActions) -> Unit,
) {
    //skipPartiallyExpanded = false means it will show first the content as half of screen size
    //skipPartiallyExpanded = true the modal bottom sheet will take it's max size at first
    //by default skipPartiallyExpanded is false
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val verticalScroll = rememberScrollState()

    if (bottomSheetUiState.showBottomSheet) {
        ModalBottomSheet(
            modifier = modifier,
            sheetState = sheetState,
            onDismissRequest = {
                transactionsActions(SharedActions.DismissBottomSheet)
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(state = verticalScroll)
                    .padding(start = 16.dp, bottom = 8.dp, end = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            end = 4.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.create_transaction),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    IconButton(
                        modifier = Modifier.padding(start = 4.dp),
                        onClick = {
                            transactionsActions(SharedActions.DismissBottomSheet)
                        }
                    ) {
                        Icon(
                            imageVector = SpendLessIcons.Close,
                            contentDescription = stringResource(R.string.close)
                        )
                    }
                }

                ExpensesAndIncome(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    isExpense = bottomSheetUiState.isExpense,
                    onClick = { isExpense ->
                        transactionsActions(SharedActions.UpdateExpense(isExpense))
                    },
                )

                SenderOrReceiverTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 34.dp),
                    placeHolder = bottomSheetUiState.placeHolder,
                    value = bottomSheetUiState.textFieldValue,
                    onValueChange = { newValue ->
                        transactionsActions(SharedActions.UpdateTextFieldValue(newValue))
                    },
                )

                AmountSpentTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    amountValue = bottomSheetUiState.amountTextFieldValue,
                    amountPlaceHolder = bottomSheetUiState.amountPlaceHolder,
                    preferencesFormat = bottomSheetUiState.preferencesFormat,
                    isExpense = bottomSheetUiState.isExpense,
                    onAmountValueChange = { newValue ->
                        transactionsActions(SharedActions.UpdateAmountTextFieldValue(newValue))
                    },
                )

                AddNote(
                    modifier = Modifier.fillMaxWidth(),
                    note = bottomSheetUiState.noteValue ?: "",
                    onNoteChange = { newNote ->
                        transactionsActions(SharedActions.UpdateNote(newNote))
                    },
                )

                if (bottomSheetUiState.isExpense) {
                    ExpenseDropDownMenu(
                        modifier = Modifier.fillMaxWidth(),
                        itemList = bottomSheetUiState.categoriesList,
                        selectedItem = bottomSheetUiState.selectedCategory,
                        onSelectItem = { selectedCategory ->
                            transactionsActions(
                                SharedActions.UpdateSelectedCategory(
                                    selectedCategory
                                )
                            )
                        },
                        isExpanded = bottomSheetUiState.isDropDownCategoryExpand,
                        onExpand = { isExpand ->
                            transactionsActions(SharedActions.UpdateDropDownCategoryExpand(isExpand))
                        }
                    )
                }

                PaymentRecurrenceDropDownMenu(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = if (bottomSheetUiState.isExpense) 8.dp else 0.dp),
                    itemList = bottomSheetUiState.paymentRecurrenceList,
                    selectedItem = bottomSheetUiState.selectedPaymentRecurrence,
                    onSelectItem = { paymentRecurrence ->
                        transactionsActions(
                            SharedActions.UpdateSelectedPaymentRecurrence(
                                paymentRecurrence
                            )
                        )
                    },
                    isExpanded = bottomSheetUiState.isDropDownPaymentRecurrenceExpand,
                    onExpand = { isExpand ->
                        transactionsActions(
                            SharedActions.UpdateDropDownPaymentRecurrenceExpand(
                                isExpand
                            )
                        )
                    }
                )
                FeatureFinanceButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    buttonResId = R.string.create,
                    isButtonEnabled = bottomSheetUiState.isButtonEnabled,
                    isButtonLoading = bottomSheetUiState.isOnCreateLoading,
                    onClick = {
                        transactionsActions(SharedActions.OnCreateClick)
                    }
                )
            }
        }
    }
}


@Composable
fun PaymentRecurrenceDropDownMenu(
    modifier: Modifier = Modifier,
    itemList: List<PaymentRecurrence>,
    selectedItem: PaymentRecurrence,
    onSelectItem: (PaymentRecurrence) -> Unit,
    isExpanded: Boolean,
    onExpand: (Boolean) -> Unit,
) {
    DropDownMenu(
        modifier = modifier,
        itemsList = itemList,
        selectedItem = selectedItem,
        onSelectItem = onSelectItem,
        text = { paymentRecurrence ->
            Text(
                modifier = Modifier,
                text = stringResource(paymentRecurrence.categoryRes),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        leading = { paymentRecurrence ->
            Image(
                modifier = Modifier.size(40.dp),
                painter = painterResource(SpendLessIcons.Recurrence),
                contentDescription = stringResource(paymentRecurrence.categoryRes)
            )
        },
        isExpanded = isExpanded,
        onExpand = {
            onExpand(true)
        },
        closeExpand = {
            onExpand(false)
        },
        showLeading = false
    )
}

@Composable
fun ExpenseDropDownMenu(
    modifier: Modifier = Modifier,
    itemList: List<Category>,
    selectedItem: Category,
    onSelectItem: (Category) -> Unit,
    isExpanded: Boolean,
    onExpand: (Boolean) -> Unit,
) {
    DropDownMenu(
        modifier = modifier,
        itemsList = itemList,
        selectedItem = selectedItem,
        onSelectItem = onSelectItem,
        text = { category ->
            Text(
                modifier = Modifier,
                text = stringResource(category.categoryName.categoryRes),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        leading = { category ->
            Image(
                modifier = Modifier.size(40.dp),
                painter = painterResource(category.image),
                contentDescription = stringResource(category.categoryName.categoryRes)
            )
        },
        isExpanded = isExpanded,
        onExpand = {
            onExpand(true)
        },
        closeExpand = {
            onExpand(false)
        },
        showLeading = true
    )
}

@Composable
fun AddNote(
    modifier: Modifier = Modifier,
    note: String,
    onNoteChange: (String) -> Unit,
) {
    TextField(
        modifier = modifier,
        value = note,
        onValueChange = { newNote ->
            onNoteChange(newNote)
        },
        placeholder = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = SpendLessIcons.Add,
                    contentDescription = stringResource(R.string.add),
                    tint = LocalContentColor.current
                )
                Text(
                    modifier = Modifier,
                    text = stringResource(R.string.add_note),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceOpacity30,
                    ),
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedPlaceholderColor = Color.Transparent,
            focusedPlaceholderColor = Color.Transparent,
            disabledPlaceholderColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
        ),
        textStyle = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        ),
        maxLines = 2,
    )
}

@Composable
fun SenderOrReceiverTextField(
    modifier: Modifier = Modifier,
    @StringRes placeHolder: Int,
    value: String,
    onValueChange: (String) -> Unit,
) {
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = { newValue ->
            onValueChange(newValue)
        },
        placeholder = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(placeHolder),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceOpacity30,
                    textAlign = TextAlign.Center
                )
            )
        },
        textStyle = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedPlaceholderColor = Color.Transparent,
            focusedPlaceholderColor = Color.Transparent,
            disabledPlaceholderColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            errorSupportingTextColor = MaterialTheme.colorScheme.error
        ),
        maxLines = 2
    )
}

@Composable
fun AmountSpentTextField(
    modifier: Modifier = Modifier,
    isExpense: Boolean,
    amountValue: TextFieldValue,
    amountPlaceHolder: String,
    preferencesFormat: PreferencesFormat,
    onAmountValueChange: (TextFieldValue) -> Unit,
) {
    TextField(
        modifier = modifier,
        value = amountValue,
        onValueChange = { newValue ->
            onAmountValueChange(newValue)
        },
        placeholder = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = BuildStyledAmount.buildStyledAmount(
                    amountPlaceHolder,
                    isExpense = isExpense,
                    color = if (isExpense) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.success
                ),
                style = MaterialTheme.typography.displayMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceOpacity30,
                    textAlign = TextAlign.Center
                ),
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedPlaceholderColor = Color.Transparent,
            focusedPlaceholderColor = Color.Transparent,
            disabledPlaceholderColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
        ),
        textStyle = MaterialTheme.typography.displayMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        visualTransformation = CurrencyVisualTransformation(
            preferencesFormat = preferencesFormat,
            isExpense = isExpense,
            negativeColor = MaterialTheme.colorScheme.error,
            positiveColor = MaterialTheme.colorScheme.success
        )
    )
}

@Composable
fun ExpensesAndIncome(
    modifier: Modifier = Modifier,
    isExpense: Boolean,
    onClick: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainerOpacity8,
                shape = MaterialTheme.shapes.medium
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExpenseButton(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            isExpense = isExpense,
            onClick = { isExpense ->
                onClick(isExpense)
            }
        )

        IncomeButton(
            modifier = Modifier.weight(1f),
            isExpense = isExpense,
            onClick = { isExpense ->
                onClick(isExpense)
            }
        )
    }
}

@Composable
fun ExpenseButton(
    modifier: Modifier = Modifier,
    isExpense: Boolean,
    onClick: (Boolean) -> Unit
) {
    Card(
        modifier = modifier
            .padding(4.dp),
        onClick = {
            onClick(true)
        },
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = if (isExpense) MaterialTheme.colorScheme.surfaceContainerLowest else Color.Transparent,
            contentColor = if (isExpense) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryFixed
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = SpendLessIcons.TrendingDown,
                contentDescription = stringResource(R.string.trendingDown),
                tint = LocalContentColor.current

            )
            Text(
                modifier = Modifier.padding(start = 7.5.dp),
                text = stringResource(R.string.expense),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = LocalContentColor.current
                )
            )
        }
    }
}

@Composable
fun IncomeButton(
    modifier: Modifier = Modifier,
    isExpense: Boolean,
    onClick: (Boolean) -> Unit
) {
    Card(
        modifier = modifier
            .padding(4.dp),
        onClick = {
            onClick(false)
        },
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = if (!isExpense) MaterialTheme.colorScheme.surfaceContainerLowest else Color.Transparent,
            contentColor = if (!isExpense) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryFixed
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = SpendLessIcons.TrendingUp,
                contentDescription = stringResource(R.string.trendingUp),
                tint = LocalContentColor.current
            )
            Text(
                modifier = Modifier.padding(start = 7.5.dp),
                text = stringResource(R.string.income),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = LocalContentColor.current
                )
            )
        }
    }
}