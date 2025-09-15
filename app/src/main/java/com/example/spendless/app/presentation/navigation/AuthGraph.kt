package com.example.spendless.app.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.spendless.core.presentation.ui.ObserveAsEvents
import com.example.spendless.features.auth.presentation.ui.common.PinEvents
import com.example.spendless.features.auth.presentation.ui.createPin.CreatePinScreen
import com.example.spendless.features.auth.presentation.ui.createPin.CreatePinViewModel
import com.example.spendless.features.auth.presentation.ui.logIn.LogInEvents
import com.example.spendless.features.auth.presentation.ui.logIn.LogInScreen
import com.example.spendless.features.auth.presentation.ui.logIn.LogInViewModel
import com.example.spendless.features.auth.presentation.ui.onBoarding.OnBoardingEvents
import com.example.spendless.features.auth.presentation.ui.onBoarding.OnBoardingScreen
import com.example.spendless.features.auth.presentation.ui.onBoarding.OnBoardingViewModel
import com.example.spendless.features.auth.presentation.ui.registration.RegisterEvents
import com.example.spendless.features.auth.presentation.ui.registration.RegisterScreen
import com.example.spendless.features.auth.presentation.ui.registration.RegisterViewModel
import com.example.spendless.features.auth.presentation.ui.repeatPin.RepeatPinScreen
import com.example.spendless.features.auth.presentation.ui.repeatPin.RepeatPinViewModel

fun NavGraphBuilder.authGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    navigation<NavigationGraphs.AuthGraph>(startDestination = NavigationScreens.Login) {
        composable<NavigationScreens.Login> {
            val logInViewModel = hiltViewModel<LogInViewModel>()
            val logInUiState by logInViewModel.state.collectAsStateWithLifecycle()

            ObserveAsEvents(logInViewModel.events) { events ->
                when (events) {
                    is LogInEvents.NavigateToDashboard -> navHostController.navigate(
                        NavigationScreens.Dashboard(username = events.username)
                    ) {
                        //remove all backstack, keep Dashboard only
                        popUpTo(0) {
                            inclusive = true
                        }
                    }

                    LogInEvents.NavigateToRegister -> {
                        //remove LogIn from backstack but save it's state and restore Register state if already saved
                        navHostController.navigate(NavigationScreens.Register) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(NavigationScreens.Login) {
                                inclusive = true
                                saveState = true
                            }
                        }
                    }
                }
            }
            LogInScreen(
                modifier = modifier,
                logInUiState = logInUiState,
                logInActions = logInViewModel::onActions
            )
        }

        composable<NavigationScreens.Register> {
            val registerViewModel = hiltViewModel<RegisterViewModel>()
            val registerState by registerViewModel.state.collectAsStateWithLifecycle()

            ObserveAsEvents(registerViewModel.events) { events ->
                when (events) {
                    RegisterEvents.NavigateToLogIn -> {
                        //remove Register from backstack but save it's state and restore LogIn state if already saved
                        navHostController.navigate(NavigationScreens.Login) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(NavigationScreens.Register) {
                                inclusive = true
                                saveState = true
                            }
                        }
                    }

                    is RegisterEvents.NavigateToCreatePin -> navHostController.navigate(
                        NavigationScreens.CreatePin(username = events.username)
                    )
                }
            }

            RegisterScreen(
                modifier = modifier,
                registerUiState = registerState,
                registerActions = registerViewModel::onActions
            )
        }

        composable<NavigationScreens.CreatePin> {
            val createPinViewModel = hiltViewModel<CreatePinViewModel>()
            val createPinUiState by createPinViewModel.state.collectAsStateWithLifecycle()

            ObserveAsEvents(createPinViewModel.events) { events ->
                when (events) {
                    PinEvents.CreatePinEvents.NavigateBack -> navHostController.navigateUp()
                    is PinEvents.CreatePinEvents.NavigateToRepeatPin -> navHostController.navigate(
                        NavigationScreens.RepeatPin(username = events.username, pin = events.pin)
                    )
                }
            }

            CreatePinScreen(
                modifier = modifier,
                createPinUiState = createPinUiState,
                pinActions = createPinViewModel::onActions
            )
        }

        composable<NavigationScreens.RepeatPin> {
            val repeatPinViewModel = hiltViewModel<RepeatPinViewModel>()
            val repeatPinUiState by repeatPinViewModel.state.collectAsStateWithLifecycle()

            ObserveAsEvents(repeatPinViewModel.events) { events ->
                when (events) {
                    PinEvents.RepeatPinEvents.NavigateBack -> navHostController.navigateUp()
                    is PinEvents.RepeatPinEvents.NavigateToOnBoarding -> navHostController.navigate(
                        NavigationScreens.Onboarding(username = events.username, pin = events.pin)
                    ) {
                        popUpTo<NavigationScreens.RepeatPin> {
                            inclusive = true
                        }
                    }
                }
            }

            RepeatPinScreen(
                modifier = modifier,
                repeatPinUiState = repeatPinUiState,
                pinActions = repeatPinViewModel::onActions
            )
        }

        composable<NavigationScreens.Onboarding> {
            val onBoardingViewModel = hiltViewModel<OnBoardingViewModel>()
            val onBoardingUiState by onBoardingViewModel.state.collectAsStateWithLifecycle()

            ObserveAsEvents(onBoardingViewModel.events) { events ->
                when (events) {
                    is OnBoardingEvents.Dashboard -> navHostController.navigate(
                        NavigationScreens.Dashboard(
                            username = events.username
                        )
                    ) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }

                    OnBoardingEvents.NavigateBack -> navHostController.navigateUp()
                }
            }
            OnBoardingScreen(
                modifier = modifier,
                onBoardingUiState = onBoardingUiState,
                onBoardingActions = onBoardingViewModel::onActions
            )

        }

        composable<NavigationScreens.VerifyPin> {

        }
    }
}