package com.example.spendless.app.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavigationGraphs {
    @Serializable
    data object AuthGraph

    @Serializable
    data object FinanceGraph
}