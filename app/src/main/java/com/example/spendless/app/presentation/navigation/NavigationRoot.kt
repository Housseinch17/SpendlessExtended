package com.example.spendless.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
    isLoggedInPreviously: Boolean,
    navHostController: NavHostController
) {
    NavHost(
        modifier = modifier,
        startDestination = if (isLoggedInPreviously) NavigationGraphs.FinanceGraph else NavigationGraphs.AuthGraph,
        navController = navHostController
    ){
        authGraph(
            modifier = Modifier,
            navHostController = navHostController
        )

        financeGraph(
            modifier = Modifier,
            navHostController = navHostController
        )
    }
}