package com.example.spendless.app

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.graphics.toColorInt
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.spendless.app.presentation.MainViewModel
import com.example.spendless.app.presentation.navigation.NavigationRoot
import com.example.spendless.core.presentation.designsystem.SpendlessTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val mainViewModel by viewModels<MainViewModel>()
    private lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //restore state if the application is not destroy just paused/stopped
        //avoid re-launching navigation from start destination(initial)
        //it will restore the last state
        val restoreState = savedInstanceState?.getBundle("nav_state")

        val blackColor = "#000000".toColorInt()
        val whiteColor = "#FFFFFF".toColorInt()
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(scrim = whiteColor, darkScrim = blackColor))

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
                if (!mainUiState.isCheckingAuth) {
                    NavigationRoot(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        isLoggedInPreviously = mainUiState.isLoggedInPreviously,
                        navHostController = navController
                    )
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //saving state when app is in background
        if (::navController.isInitialized) {
            outState.putBundle("nav_state", navController.saveState())
        }
    }
}
