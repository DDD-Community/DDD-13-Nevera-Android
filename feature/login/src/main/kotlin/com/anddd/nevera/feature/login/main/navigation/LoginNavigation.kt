package com.anddd.nevera.feature.login.main.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.login.main.LoginScreen
import com.anddd.nevera.feature.login.main.google.GoogleAuthClient

const val LOGIN_ROUTE = "login"

fun NavGraphBuilder.loginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSignup: () -> Unit
) {
    composable(route = LOGIN_ROUTE) {
        val googleAuthClient = remember { GoogleAuthClient() }
        LoginScreen(
            onNavigateToHome = onNavigateToHome,
            onNavigateToSignup = onNavigateToSignup,
            googleAuthClient = googleAuthClient
        )
    }
}
