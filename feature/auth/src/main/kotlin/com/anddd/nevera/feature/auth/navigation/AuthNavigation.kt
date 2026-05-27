package com.anddd.nevera.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.anddd.nevera.feature.auth.main.LoginScreen
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.auth.signup.SignupScreen
import kotlinx.serialization.Serializable

@Serializable
data object AuthGraphRoute

@Serializable
internal data object LoginRoute

@Serializable
internal data object SignupRoute

fun NavGraphBuilder.authNavGraph(
    googleAuthClient: GoogleAuthClient,
    navController: NavController,
    onNavigateToHome: () -> Unit,
) {
    navigation<AuthGraphRoute>(startDestination = LoginRoute) {
        composable<LoginRoute> {
            LoginScreen(
                googleAuthClient = googleAuthClient,
                onNavigateToHome = onNavigateToHome,
                onNavigateToSignup = { navController.navigate(SignupRoute) }
            )
        }

        composable<SignupRoute> {
            SignupScreen(onNavigateToLogin = { navController.popBackStack() })
        }
    }
}