package com.anddd.nevera.feature.splash.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.splash.api.SplashRoute
import com.anddd.nevera.feature.splash.main.SplashScreen

fun NavGraphBuilder.splashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    composable<SplashRoute> {
        SplashScreen(
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToHome = onNavigateToHome
        )
    }
}
