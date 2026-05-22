package com.anddd.nevera.feature.auth.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.auth.main.LoginScreen
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

fun NavGraphBuilder.loginScreen(
    googleAuthClient: GoogleAuthClient,
    onNavigateToHome: () -> Unit,
    onNavigateToSignup: () -> Unit,
) {
    composable<LoginRoute> {
        LoginScreen(
            googleAuthClient = googleAuthClient,
            onNavigateToHome = onNavigateToHome,
            onNavigateToSignup = onNavigateToSignup,
        )
    }
}
