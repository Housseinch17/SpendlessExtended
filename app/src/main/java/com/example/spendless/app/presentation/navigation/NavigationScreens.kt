package com.example.spendless.app.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavigationScreens {

    //Auth Graph
    @Serializable
    data object Register: NavigationScreens

    @Serializable
    data class CreatePin(val username: String = ""): NavigationScreens

    @Serializable
    data class RepeatPin(val username: String = "", val pin: String = ""): NavigationScreens

    @Serializable
    data class Onboarding(val username: String = "", val pin: String = ""): NavigationScreens

    @Serializable
    data object Login: NavigationScreens

    @Serializable
    data object PinPrompt: NavigationScreens

    //Finance Graph
    @Serializable
    data object Dashboard: NavigationScreens

    @Serializable
    data class Transactions(val showExportRange: Boolean = false): NavigationScreens

    @Serializable
    data object Settings: NavigationScreens

}