package com.example.spendless.app.presentation.navigation

import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.spendless.features.finance.presentation.ui.screens.dashboard.DashboardScreen
import com.example.spendless.features.finance.presentation.ui.screens.dashboard.DashboardViewModel
import androidx.compose.runtime.getValue
import com.example.spendless.core.presentation.ui.ObserveAsEvents
import com.example.spendless.features.finance.presentation.ui.screens.dashboard.DashboardEvents
import com.example.spendless.features.finance.presentation.ui.screens.dashboard.DashboardUiState

fun NavGraphBuilder.financeGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    navigation<NavigationGraphs.FinanceGraph>(startDestination = NavigationScreens.Dashboard) {
        composable<NavigationScreens.Dashboard> {
            val dashboardViewModel = hiltViewModel<DashboardViewModel>()
            val dashboardUiState by dashboardViewModel.state.collectAsStateWithLifecycle(
                initialValue = DashboardUiState()
            )

            ObserveAsEvents(dashboardViewModel.events) { events->
                when(events){
                    DashboardEvents.NavigateToExportData -> {
                        navHostController.navigate(NavigationScreens.Transactions)
                    }
                    DashboardEvents.NavigateToSettings -> {
                        navHostController.navigate(NavigationScreens.Settings)
                    }
                }
            }

            DashboardScreen(
                modifier = modifier,
                dashboardUiState = dashboardUiState,
                dashboardActions = dashboardViewModel::onActions,
            )
        }

        composable<NavigationScreens.Transactions> {

        }

        composable<NavigationScreens.Settings> {

        }
    }
}