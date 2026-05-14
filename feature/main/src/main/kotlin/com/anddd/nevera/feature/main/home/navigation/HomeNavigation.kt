package com.anddd.nevera.feature.main.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.main.home.HomeScreen

const val HOME_ROUTE = "home"

fun NavGraphBuilder.homeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMyPage: () -> Unit,
) {
    composable(route = HOME_ROUTE) {
        HomeScreen(
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToMyPage = onNavigateToMyPage,
        )
    }
}
