package com.anddd.nevera.feature.login.main.navigation

import  androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.login.main.LoginScreen

const val LOGIN_ROUTE = "login"

fun NavGraphBuilder.loginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onGoogleLoginClick: () -> Unit
) {
    composable(route = LOGIN_ROUTE) {
        LoginScreen(
            onNavigateToHome = onNavigateToHome,
            onNavigateToSignup = onNavigateToSignup,
            onGoogleLoginClick = onGoogleLoginClick
        )
    }
}
