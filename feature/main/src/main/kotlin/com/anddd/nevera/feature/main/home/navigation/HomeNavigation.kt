package com.anddd.nevera.feature.main.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.anddd.nevera.feature.main.home.HomeScreen

const val HOME_ROUTE = "home/{userId}"

fun homeRoute(userId: String) = "home/$userId"

fun NavGraphBuilder.homeScreen() {
    composable(
        route = HOME_ROUTE,
        arguments = listOf(navArgument("userId") { type = NavType.StringType })
    ) {
        HomeScreen()
    }
}
