package com.example.spendless.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spendless.app.presentation.MainViewModel
import com.example.spendless.app.presentation.navigation.NavigationRoot
import com.example.spendless.core.presentation.designsystem.SpendlessTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val mainViewModel by viewModels<MainViewModel>()
    private lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //restore state if the application is not destroy just paused/stopped
        //avoid re-launching navigation from start destination(initial)
        //it will restore the last state
        val restoreState = savedInstanceState?.getBundle("nav_state")

        enableEdgeToEdge()
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                mainViewModel.state.value.isCheckingAuth
            }
        }

        setContent {
            val mainUiState by mainViewModel.state.collectAsStateWithLifecycle()

            navController = rememberNavController()
            navController.restoreState(restoreState)

            SpendlessTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    NavigationRoot(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        isLoggedInPreviously = mainUiState.isLoggedInPreviously,
                        navHostController = navController
                    )
                }
            }
        }
    }
}
