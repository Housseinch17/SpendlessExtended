package com.example.spendless.app.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.spendless.features.auth.presentation.ui.createPin.CreatePinScreen
import com.example.spendless.features.auth.presentation.ui.createPin.CreatePinViewModel
import com.example.spendless.features.auth.presentation.ui.registration.RegisterScreen
import com.example.spendless.features.auth.presentation.ui.registration.RegisterViewModel
import com.example.spendless.features.auth.presentation.ui.repeatPin.RepeatPinScreen
import com.example.spendless.features.auth.presentation.ui.repeatPin.RepeatPinViewModel

fun NavGraphBuilder.authGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    navigation<NavigationGraphs.AuthGraph>(startDestination = NavigationScreens.RepeatPin()) {
        composable<NavigationScreens.Login> {

        }

        composable<NavigationScreens.Register> {
            val registerViewModel = hiltViewModel<RegisterViewModel>()
            val registerState by registerViewModel.state.collectAsStateWithLifecycle()

            RegisterScreen(
                modifier = modifier,
                registerUiState = registerState,
                registerActions = registerViewModel::onActions
            )
        }

        composable<NavigationScreens.CreatePin> {
            val createPinViewModel = hiltViewModel<CreatePinViewModel>()
            val createPinUiState by createPinViewModel.state.collectAsStateWithLifecycle()

            CreatePinScreen(
                modifier = modifier,
                createPinUiState = createPinUiState,
                pinActions = createPinViewModel::onActions
            )
        }

        composable<NavigationScreens.RepeatPin> {
            val repeatPinViewModel = hiltViewModel<RepeatPinViewModel>()
            val repeatPinUiState by repeatPinViewModel.state.collectAsStateWithLifecycle()

            RepeatPinScreen(
                modifier = modifier,
                repeatPinUiState = repeatPinUiState,
                pinActions = repeatPinViewModel::onActions
            )
        }

        composable<NavigationScreens.Preferences> {

        }

        composable<NavigationScreens.VerifyPin> {

        }
    }
}