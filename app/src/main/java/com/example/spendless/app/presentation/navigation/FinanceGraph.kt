package com.example.spendless.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.financeGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
){
    navigation<NavigationGraphs.FinanceGraph>(startDestination = NavigationScreens.Dashboard()){
        composable<NavigationScreens.Dashboard> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ){
                Text("This is DashBoard",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    ))
            }
        }

        composable<NavigationScreens.Transactions> {

        }

        composable<NavigationScreens.Settings> {

        }
    }
}