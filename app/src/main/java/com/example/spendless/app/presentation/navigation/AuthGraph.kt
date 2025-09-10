package com.example.spendless.app.presentation.navigation

import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.spendless.features.auth.presentation.ui.registration.RegisterScreen
import com.example.spendless.features.auth.presentation.ui.registration.RegisterViewModel
import androidx.compose.runtime.getValue

fun NavGraphBuilder.authGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
){
    navigation<NavigationGraphs.AuthGraph>(startDestination = NavigationScreens.Login){
        composable<NavigationScreens.Login>{

        }

        composable<NavigationScreens.Register>{
            val registerViewModel = hiltViewModel<RegisterViewModel>()
            val registerState by registerViewModel.state.collectAsStateWithLifecycle()

            RegisterScreen(
                modifier = modifier,
                registerUiState = registerState,
                registerActions = registerViewModel::onActions
            )
        }

        composable<NavigationScreens.CreatePin> {

        }

        composable<NavigationScreens.Preferences>{

        }

        composable<NavigationScreens.VerifyPin>{

        }
    }
}