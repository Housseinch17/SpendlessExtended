package com.example.spendless.features.auth.presentation.ui.onBoarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendless.R
import com.example.spendless.core.data.constant.Constants
import com.example.spendless.core.database.user.model.Currency
import com.example.spendless.core.database.user.model.PreferencesFormat
import com.example.spendless.core.presentation.designsystem.components.CurrenciesDropDownMenu
import com.example.spendless.core.presentation.designsystem.components.SpendlessButton
import com.example.spendless.core.presentation.designsystem.primaryContainerOpacity8
import com.example.spendless.features.auth.presentation.designsystem.components.SpendlessBackButton

@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    onBoardingUiState: OnBoardingUiState,
    onBoardingActions: (OnBoardingActions) -> Unit,
) {
    val verticalScrollState = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScroll(state = verticalScrollState)
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 20.dp
            )
    ) {
        SpendlessBackButton(
            modifier = Modifier
                .size(32.dp)
                .offset(x = (-6).dp),
            onBackClick = {
                onBoardingActions(OnBoardingActions.NavigateBack)
            }
        )

        OnBoardingHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            amount = onBoardingUiState.amountSpent
        )

        OnBoardingBody(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            preferencesFormat = onBoardingUiState.preferencesFormat,
            isButtonLoading = onBoardingUiState.isButtonLoading,
            onExpensesClick = { selectedExpenses ->
                onBoardingActions(OnBoardingActions.UpdateExpensesFormat(expenses = selectedExpenses))
            },
            onDecimalClick = { selectedDecimal ->
                onBoardingActions(OnBoardingActions.UpdateDecimalSeparator(decimal = selectedDecimal))

            },
            onThousandsClick = { selectedThousands ->
                onBoardingActions(OnBoardingActions.UpdateThousandsSeparator(thousands = selectedThousands))
            },
            startTracking = {
                onBoardingActions(OnBoardingActions.StartTracking)
            },
            currenciesList = onBoardingUiState.currenciesList,
            selectedItem = onBoardingUiState.preferencesFormat.currency,
            onSelectItem = { selectedCurrency ->
                onBoardingActions(OnBoardingActions.UpdateSelectedCurrency(currency = selectedCurrency))
            },
            isExpanded = onBoardingUiState.isExpanded,
            onExpand = {
                onBoardingActions(OnBoardingActions.OnExpand)
            },
            closeExpand = {
                onBoardingActions(OnBoardingActions.CloseExpand)
            },
        )
    }
}

@Composable
fun OnBoardingHeader(
    modifier: Modifier = Modifier,
    amount: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = stringResource(R.string.set_preferences),
            style = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 0.1.sp
            )
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(R.string.you_can_change_in_settings),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .shadow(
                    elevation = 7.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false
                )
                .background(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainerLowest
                )
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = amount,
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 0.1.sp
                )
            )
            Text(
                text = stringResource(R.string.spend_this_month)
            )
        }
    }
}

@Composable
fun OnBoardingBody(
    modifier: Modifier = Modifier,
    preferencesFormat: PreferencesFormat,
    onExpensesClick: (String) -> Unit,
    onDecimalClick: (String) -> Unit,
    onThousandsClick: (String) -> Unit,
    startTracking: () -> Unit,
    isButtonLoading: Boolean,
    currenciesList: List<Currency>,
    selectedItem: Currency,
    onSelectItem: (Currency) -> Unit,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    closeExpand: () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(R.string.expenses_format),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        ExpensesFormat(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            expensesFormatList = Constants.expensesFormatList,
            enabledItem = preferencesFormat.expenses,
            onExpensesClick = onExpensesClick
        )

        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(R.string.currency),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )

        CurrencyDropDownMenu(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            currenciesList = currenciesList,
            selectedItem = selectedItem,
            onSelectItem = onSelectItem,
            isExpanded = isExpanded,
            onExpand = onExpand,
            closeExpand = closeExpand
        )

        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(R.string.decimal_separator),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        DecimalSeparator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            decimalSeparatorList = Constants.decimalSeparatorList,
            enabledItem = preferencesFormat.decimalSeparator,
            onDecimalClick = onDecimalClick,
        )
    }

    Text(
        modifier = Modifier.padding(top = 16.dp),
        text = stringResource(R.string.thousands_separator),
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface
        )
    )
    ThousandsSeparator(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        thousandsSeparatorList = Constants.thousandsSeparatorList,
        enabledItem = preferencesFormat.thousandsSeparator,
        onThousandsClick = onThousandsClick,
    )

    SpendlessButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 34.dp),
        enabled = true,
        onClick = startTracking,
        content = {
            if (!isButtonLoading) {
                Text(
                    text = stringResource(R.string.start_tracking),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                )
            } else {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    )
}

@Composable
fun SeparatorItem(
    modifier: Modifier = Modifier,
    item: String,
    enabledItem: String,
    onClick: () -> Unit,
) {
    Text(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(
                color = if (item == enabledItem) MaterialTheme.colorScheme.surfaceContainerLowest else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        text = item,
        style = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
fun SeparatorList(
    modifier: Modifier = Modifier,
    list: List<String>,
    enabledItem: String,
    onClick: (String) -> Unit,
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = fadeOut(animationSpec = tween(durationMillis = 300))
    ) {
        Row(
            modifier = modifier.background(
                color = MaterialTheme.colorScheme.primaryContainerOpacity8,
                shape = MaterialTheme.shapes.medium
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            list.forEach { item ->
                SeparatorItem(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    item = item,
                    enabledItem = enabledItem,
                    onClick = {
                        onClick(item)
                    }
                )
            }
        }
    }
}

@Composable
fun ExpensesFormat(
    modifier: Modifier = Modifier,
    expensesFormatList: List<String>,
    enabledItem: String,
    onExpensesClick: (String) -> Unit,
) {
    SeparatorList(
        modifier = modifier,
        list = expensesFormatList,
        enabledItem = enabledItem,
        onClick = onExpensesClick
    )
}

@Composable
fun CurrencyDropDownMenu(
    modifier: Modifier = Modifier,
    currenciesList: List<Currency>,
    selectedItem: Currency,
    onSelectItem: (Currency) -> Unit,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    closeExpand: () -> Unit
) {
    CurrenciesDropDownMenu(
        modifier = modifier,
        currenciesList = currenciesList,
        selectedItem = selectedItem,
        onSelectItem = onSelectItem,
        isExpanded = isExpanded,
        onExpand = onExpand,
        closeExpand = closeExpand
    )
}

@Composable
fun DecimalSeparator(
    modifier: Modifier = Modifier,
    decimalSeparatorList: List<String>,
    enabledItem: String,
    onDecimalClick: (String) -> Unit,
) {
    SeparatorList(
        modifier = modifier,
        list = decimalSeparatorList,
        enabledItem = enabledItem,
        onClick = onDecimalClick
    )
}

@Composable
fun ThousandsSeparator(
    modifier: Modifier = Modifier,
    thousandsSeparatorList: List<String>,
    enabledItem: String,
    onThousandsClick: (String) -> Unit,
) {
    SeparatorList(
        modifier = modifier,
        list = thousandsSeparatorList,
        enabledItem = enabledItem,
        onClick = onThousandsClick
    )
}



