package com.example.spendless.app.presentation.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.financeGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
){
    navigation<NavigationGraphs.FinanceGraph>(startDestination = NavigationScreens.Dashboard){
        composable<NavigationScreens.Dashboard> {

        }

        composable<NavigationScreens.Transactions> {

        }

        composable<NavigationScreens.Settings> {

        }
    }
}