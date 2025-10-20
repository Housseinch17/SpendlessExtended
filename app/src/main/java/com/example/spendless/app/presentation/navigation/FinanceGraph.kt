package com.example.spendless.app.presentation.navigation

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.spendless.core.presentation.ui.ObserveAsEvents
import com.example.spendless.features.finance.presentation.ui.FinanceEvents
import com.example.spendless.features.finance.presentation.ui.FinanceViewModel
import com.example.spendless.features.finance.presentation.ui.screens.dashboard.DashboardEvents
import com.example.spendless.features.finance.presentation.ui.screens.dashboard.DashboardScreen
import com.example.spendless.features.finance.presentation.ui.screens.dashboard.DashboardViewModel
import com.example.spendless.features.finance.presentation.ui.screens.security.SecurityEvents
import com.example.spendless.features.finance.presentation.ui.screens.security.SecurityScreen
import com.example.spendless.features.finance.presentation.ui.screens.security.SecurityViewModel
import com.example.spendless.features.finance.presentation.ui.screens.settings.SettingsEvents
import com.example.spendless.features.finance.presentation.ui.screens.settings.SettingsScreen
import com.example.spendless.features.finance.presentation.ui.screens.settings.SettingsViewModel
import com.example.spendless.features.finance.presentation.ui.screens.transactions.TransactionsEvents
import com.example.spendless.features.finance.presentation.ui.screens.transactions.TransactionsScreen
import com.example.spendless.features.finance.presentation.ui.screens.transactions.TransactionsViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
fun NavGraphBuilder.financeGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    navigation<NavigationGraphs.FinanceGraph>(
        //if i navigate directly to dashBoard with promptPin = false
        //it will override promptPin to false but if i start directly the
        //finance graph like user already logged in, the promptPin = true
        startDestination = NavigationScreens.Dashboard(
            promptPin = true
        )
    ) {
        composable<NavigationScreens.Dashboard> { entry->
            //we use remember(navHostController) because when we clear FinanceGraph after logOut the app
            //will crash to avoid that we use it
            val parentBackStackEntry = remember(navHostController) {
                navHostController.getBackStackEntry(NavigationGraphs.FinanceGraph)
            }
            val financeViewModel = hiltViewModel<FinanceViewModel>(parentBackStackEntry)

            ObserveAsEvents(financeViewModel.events) { event ->
                when (event) {
                    FinanceEvents.PromptPin -> {
                        navHostController.navigate(NavigationScreens.PinPrompt)
                    }
                }
            }

            val dashboardViewModel = hiltViewModel<DashboardViewModel>()
            val dashboardUiState by dashboardViewModel.state.collectAsStateWithLifecycle()

            ObserveAsEvents(dashboardViewModel.events) { events ->
                when (events) {
                    DashboardEvents.NavigateToExportData -> {
                        navHostController.navigate(NavigationScreens.Transactions(true))
                    }

                    DashboardEvents.NavigateToSettings -> {
                        navHostController.navigate(NavigationScreens.Settings)
                    }

                    DashboardEvents.NavigateToTransactions -> {
                        navHostController.navigate(NavigationScreens.Transactions(false))
                    }

                    DashboardEvents.PromptPin -> {
                        navHostController.navigate(
                            NavigationScreens.PinPrompt
                        )
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
            val context = LocalContext.current
            val parentBackStackEntry = remember(navHostController) {
                navHostController.getBackStackEntry(NavigationGraphs.FinanceGraph)
            }
            val financeViewModel = hiltViewModel<FinanceViewModel>(parentBackStackEntry)

            ObserveAsEvents(financeViewModel.events) { event ->
                when (event) {
                    FinanceEvents.PromptPin -> navHostController.navigate(NavigationScreens.PinPrompt)
                }
            }

            val transactionsViewModel = hiltViewModel<TransactionsViewModel>()
            val transactionsUiState by transactionsViewModel.state.collectAsStateWithLifecycle()

            ObserveAsEvents(transactionsViewModel.events) { events ->
                when (events) {
                    TransactionsEvents.NavigateBack -> navHostController.navigateUp()
                    TransactionsEvents.PromptPin ->  navHostController.navigate(
                        NavigationScreens.PinPrompt
                    )

                    is TransactionsEvents.ShowToast ->
                        Toast.makeText(
                        context,
                        events.showText.asString(context),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            TransactionsScreen(
                modifier = modifier,
                transactionsUiState = transactionsUiState,
                transactionsActions = transactionsViewModel::onActions
            )
        }

        composable<NavigationScreens.Settings> {
            val parentBackStackEntry = remember(navHostController) {
                navHostController.getBackStackEntry(NavigationGraphs.FinanceGraph)
            }
            val financeViewModel = hiltViewModel<FinanceViewModel>(parentBackStackEntry)

            ObserveAsEvents(financeViewModel.events) { event ->
                when (event) {
                    FinanceEvents.PromptPin -> navHostController.navigate(NavigationScreens.PinPrompt)
                }
            }

            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            val settingsUiState by settingsViewModel.state.collectAsStateWithLifecycle()

            ObserveAsEvents(settingsViewModel.events) { events ->
                when (events) {
                    SettingsEvents.LogOut -> {
                        navHostController.navigate(
                            NavigationScreens.Login
                        ) {
                            //remove all backstack, keep Login only
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }

                    SettingsEvents.NavigateBack -> {
                        navHostController.navigateUp()
                    }

                    SettingsEvents.NavigateToPreferences -> {
                        navHostController.navigate(
                            NavigationScreens.Onboarding(
                                username = null,
                                pin = null
                            )
                        )
                    }

                    SettingsEvents.NavigateToSecurity -> {
                        navHostController.navigate(NavigationScreens.Security)
                    }

                    SettingsEvents.PromptPin ->  navHostController.navigate(
                        NavigationScreens.PinPrompt
                    )
                }
            }

            SettingsScreen(
                modifier = modifier,
                settingsUiState = settingsUiState,
                settingsActions = settingsViewModel::onActions
            )
        }

        composable<NavigationScreens.Security> {
            val parentBackStackEntry = remember(navHostController) {
                navHostController.getBackStackEntry(NavigationGraphs.FinanceGraph)
            }
            val financeViewModel = hiltViewModel<FinanceViewModel>(parentBackStackEntry)

            ObserveAsEvents(financeViewModel.events) { event ->
                when (event) {
                    FinanceEvents.PromptPin -> navHostController.navigate(NavigationScreens.PinPrompt)
                }
            }

            val securityViewModel = hiltViewModel<SecurityViewModel>()
            val securityUiState by securityViewModel.state.collectAsStateWithLifecycle()

            val context = LocalContext.current

            ObserveAsEvents(securityViewModel.events) { events ->
                when (events) {
                    SecurityEvents.NavigateBack -> navHostController.navigateUp()
                    is SecurityEvents.ShowToast -> {
                        Toast.makeText(
                            context,
                            events.showText.asString(context),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            SecurityScreen(
                modifier = modifier,
                securityUiState = securityUiState,
                securityActions = securityViewModel::onActions
            )
        }
    }
}