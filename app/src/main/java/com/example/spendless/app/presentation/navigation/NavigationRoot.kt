package com.example.spendless.app.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
    Scaffold { innerPadding->
        NavHost(
            modifier = modifier,
            startDestination = if (isLoggedInPreviously) NavigationGraphs.FinanceGraph else NavigationGraphs.AuthGraph,
            navController = navHostController
        ){
            authGraph(
                //here not using innerPadding for all the padding values because the
                //banner will take bottom padding when keyboard is open
                modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding()),
                navHostController = navHostController
            )

            financeGraph(
                modifier = Modifier.fillMaxSize(),
                navHostController = navHostController
            )
        }

    }
}