package com.anddd.nevera.feature.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.anddd.nevera.core.navigation.Navigator
import com.anddd.nevera.feature.auth.api.AuthGraphRoute
import com.anddd.nevera.feature.auth.api.LoginRoute
import com.anddd.nevera.feature.auth.api.SignupRoute
import com.anddd.nevera.feature.auth.main.LoginScreen
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.auth.signup.SignupScreen

fun NavGraphBuilder.authNavGraph(
    googleAuthClient: GoogleAuthClient,
    navigator: Navigator,
    onNavigateToHome: () -> Unit,
) {
    navigation<AuthGraphRoute>(startDestination = LoginRoute) {
        composable<LoginRoute> {
            LoginScreen(
                googleAuthClient = googleAuthClient,
                onNavigateToHome = onNavigateToHome,
                onNavigateToSignup = { navigator.navigate(SignupRoute) },
            )
        }

        composable<SignupRoute> {
            SignupScreen(onNavigateToLogin = navigator::goBack)
        }
    }
}
