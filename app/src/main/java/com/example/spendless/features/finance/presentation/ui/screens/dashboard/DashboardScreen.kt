package com.example.spendless.features.finance.presentation.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.spendless.R
import com.example.spendless.core.data.model.Category
import com.example.spendless.core.presentation.designsystem.SpendLessIcons
import com.example.spendless.core.presentation.designsystem.bodyXSmall
import com.example.spendless.core.presentation.designsystem.onPrimaryFixed
import com.example.spendless.core.presentation.designsystem.onPrimaryOpacity12
import com.example.spendless.core.presentation.designsystem.onPrimaryOpacity20
import com.example.spendless.core.presentation.designsystem.primaryFixed
import com.example.spendless.core.presentation.designsystem.secondaryFixed
import com.example.spendless.core.presentation.designsystem.secondaryFixedDim
import com.example.spendless.core.presentation.designsystem.success
import com.example.spendless.core.presentation.ui.UiText
import com.example.spendless.features.finance.data.model.TransactionItem
import com.example.spendless.features.finance.presentation.designsystem.components.CreateTransactionModalBottomSheet
import com.example.spendless.features.finance.presentation.designsystem.components.TransactionsList
import com.example.spendless.features.finance.presentation.designsystem.utils.toDrawableRes
import com.example.spendless.features.finance.presentation.designsystem.utils.toStringRes
import com.example.spendless.features.finance.presentation.ui.common.SharedActions
import com.example.spendless.features.finance.presentation.ui.common.SharedActions.DashboardActions

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    dashboardUiState: DashboardUiState,
    dashboardActions: (SharedActions) -> Unit
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.onPrimaryFixed
                    ),
                    center = Offset(0f,0f),
                    radius = 1200f
                )
            ),
        contentAlignment = Alignment.TopStart
    ) {
        if (dashboardUiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.surfaceContainerLowest
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    DashboardTopBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(top = 8.dp)
                            .padding(horizontal = 16.dp),
                        username = dashboardUiState.username.uppercase(),
                        onExportClick = {
                            dashboardActions(DashboardActions.NavigateToExportData)
                        },
                        onSettingsClick = {
                            dashboardActions(DashboardActions.NavigateToSettings)
                        }
                    )
                },
                floatingActionButton = {
                    if (dashboardUiState.bottomSheetUiState.isFloatingActionButtonVisible) {
                        FloatingActionButton(
                            modifier = Modifier.size(64.dp),
                            onClick = {
                                dashboardActions(SharedActions.ShowBottomBar)
                            },
                            shape = MaterialTheme.shapes.medium,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            Icon(
                                imageVector = SpendLessIcons.Add,
                                contentDescription = stringResource(R.string.create_transaction),
                                tint = LocalContentColor.current
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = modifier
                        .padding(top = innerPadding.calculateTopPadding()),
                ) {
                    DashboardHeader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        amountSpent = dashboardUiState.amountSpent,
                    )

                    DashboardBody(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        dashboardUiState = dashboardUiState
                    )

                    DashboardBottom(
                        modifier = Modifier.weight(1f),
                        onShowAllClick = {
                            dashboardActions(DashboardActions.ShowAll)
                        },
                        transactionsByDate = dashboardUiState.transactionsByDate,
                        selectedTransactionItem = dashboardUiState.bottomSheetUiState.selectedTransactionItem,
                        onSelectTransaction = { transactionItem ->
                            dashboardActions(DashboardActions.SelectedTransaction(transactionItem))
                        },
                        showFloatingActionButton = { showFab ->
                            dashboardActions(DashboardActions.ShowFloatingActionButton(showFab))
                        }
                    )
                }

                CreateTransactionModalBottomSheet(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .imePadding()
                        .padding(top = 10.dp),
                    bottomSheetUiState = dashboardUiState.bottomSheetUiState,
                    transactionsActions = dashboardActions,
                )
            }
        }
    }
}

@Composable
fun DashboardTopBar(
    modifier: Modifier = Modifier,
    username: String,
    onExportClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = username,
            style = MaterialTheme.typography.displayMedium.copy(
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Normal
            )
        )
        Surface(
            modifier = Modifier
                .padding(start = 10.dp),
            onClick = onExportClick,
            color = MaterialTheme.colorScheme.onPrimaryOpacity12,
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                modifier = Modifier.padding(10.dp),
                imageVector = SpendLessIcons.Download,
                contentDescription = stringResource(R.string.export_data),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Surface(
            modifier = Modifier
                .padding(start = 10.dp),
            onClick = onSettingsClick,
            color = MaterialTheme.colorScheme.onPrimaryOpacity12,
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                modifier = Modifier.padding(10.dp),
                imageVector = SpendLessIcons.Settings,
                contentDescription = stringResource(R.string.settings),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun DashboardHeader(
    modifier: Modifier = Modifier,
    amountSpent: String,
) {
    Column(
        modifier = modifier.padding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            modifier = Modifier.padding(top = 38.dp),
            text = amountSpent,
            style = MaterialTheme.typography.displayLarge.copy(
                color = MaterialTheme.colorScheme.onPrimary
            )
        )

        Text(
            modifier = Modifier.padding(top = 2.dp, bottom = 38.dp),
            text = stringResource(R.string.account_balance),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

@Composable
fun DashboardBody(
    modifier: Modifier = Modifier,
    dashboardUiState: DashboardUiState,
) {
    Column(
        modifier = modifier
    ) {
        CategoryLargestExpense(
            modifier = Modifier,
            largestCategoryExpense = dashboardUiState.largestCategoryExpense
        )
        LargestTransaction(
            modifier = Modifier.fillMaxWidth(),
            dashboardUiState = dashboardUiState
        )
    }
}

@Composable
fun CategoryLargestExpense(
    modifier: Modifier = Modifier,
    largestCategoryExpense: Category?
) {
    largestCategoryExpense?.let { category ->
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimaryOpacity20
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    modifier = Modifier.size(56.dp),
                    painter = painterResource(category.image.toDrawableRes()),
                    contentDescription = null
                )
                Column(
                    modifier = Modifier.padding(start = 10.dp, top = 7.dp, bottom = 7.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(category.categoryName.categoryRes.toStringRes()),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    Text(
                        modifier = Modifier,
                        text = stringResource(R.string.most_popular_category),
                        style = MaterialTheme.typography.bodyXSmall.copy(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }

    }
}

@Composable
fun LargestTransaction(
    modifier: Modifier = Modifier,
    dashboardUiState: DashboardUiState,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            //both columns takes the same height which is the height of the tallest column
            .height(intrinsicSize = IntrinsicSize.Max)
            .padding(
                top = 8.dp,
                bottom = 16.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    color = MaterialTheme.colorScheme.primaryFixed,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(vertical = 14.dp, horizontal = 12.dp)
        ) {
            if (dashboardUiState.largestTransaction != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier,
                        text = "Adobe Yearly",
                        style = MaterialTheme.typography.titleLarge.copy(
                            MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        modifier = Modifier,
                        text = dashboardUiState.largestTransactionAmount,
                        style = MaterialTheme.typography.titleLarge.copy(
                            MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(R.string.largest_transaction),
                        style = MaterialTheme.typography.bodyXSmall.copy(
                            MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        modifier = Modifier,
                        text = dashboardUiState.largestTransaction.date,
                        style = MaterialTheme.typography.bodyXSmall.copy(
                            MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            } else {
                Text(
                    modifier = Modifier,
                    text = stringResource(R.string.your_largest_transaction_will_appear_here),
                    style = MaterialTheme.typography.titleMedium.copy(
                        MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    ),
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(
                    color = MaterialTheme.colorScheme.secondaryFixed,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(vertical = 13.dp, horizontal = 12.dp)
        ) {
            Text(
                modifier = Modifier,
                text = dashboardUiState.previousWeekSpentFormatted,
                style = MaterialTheme.typography.titleLarge.copy(
                    MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                modifier = Modifier,
                text = stringResource(R.string.previous_week),
                style = MaterialTheme.typography.bodyXSmall.copy(
                    MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}


@Composable
fun DashboardBottom(
    modifier: Modifier = Modifier,
    onShowAllClick: () -> Unit,
    transactionsByDate: Map<UiText, List<TransactionItem>>,
    selectedTransactionItem: TransactionItem,
    onSelectTransaction: (TransactionItem) -> Unit,
    showFloatingActionButton: (Boolean) -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
    ) {
        if (!transactionsByDate.isEmpty()) {
            TransactionsList(
                modifier = Modifier,
                onShowAllClick = onShowAllClick,
                showTransactionText = true,
                transactionsByDate = transactionsByDate,
                selectedTransactionItem = selectedTransactionItem,
                onSelectTransaction = onSelectTransaction,
                showFloatingActionButton = showFloatingActionButton
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier.size(96.dp),
                        painter = painterResource(R.drawable.money),
                        contentDescription = stringResource(R.string.money)
                    )
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = stringResource(R.string.no_transactions_to_show),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }

    }
}

@Composable
fun TransactionItem(
    modifier: Modifier = Modifier,
    transactionItem: TransactionItem,
    selectedTransactionItem: TransactionItem,
    onClick: () -> Unit,
) {
    val isSelected = transactionItem == selectedTransactionItem
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .shadow(
                elevation = if (!isSelected) 0.dp else 2.dp,
                shape = if (!isSelected) RectangleShape else MaterialTheme.shapes.medium
            )
            .background(
                color = if (!isSelected) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = if (!isSelected) RectangleShape else MaterialTheme.shapes.medium
            )
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 4.dp, bottom = 4.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(44.dp),
            ) {
                Image(
                    modifier = Modifier.matchParentSize(),
                    painter = painterResource(transactionItem.image.toDrawableRes()),
                    contentDescription = stringResource(transactionItem.description.toStringRes()),
                )
                if (transactionItem.content != null) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd),
                        shape = MaterialTheme.shapes.extraSmall,
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    ) {
                        Icon(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                            imageVector = SpendLessIcons.Vector,
                            contentDescription = stringResource(R.string.vector),
                            tint = if (transactionItem.isExpense) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryFixedDim
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
            ) {
                Text(
                    modifier = Modifier,
                    text = transactionItem.title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(
                    modifier = Modifier,
                    text = stringResource(transactionItem.description.toStringRes()),
                    style = MaterialTheme.typography.bodyXSmall.copy(
                        color = if (transactionItem.isExpense) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.success
                    )
                )
            }

            Text(
                modifier = Modifier,
                text = transactionItem.price,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = if (transactionItem.isExpense) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.success
                )
            )
        }

        AnimatedVisibility(
            visible = isSelected && transactionItem.content != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp, start = 58.dp, end = 12.dp),
                text = transactionItem.content ?: "",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                )
            )
        }
    }
}

