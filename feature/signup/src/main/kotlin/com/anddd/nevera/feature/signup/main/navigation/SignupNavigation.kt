package com.anddd.nevera.feature.signup.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.signup.main.SignupScreen

const val SIGNUP_ROUTE = "signup"

fun NavGraphBuilder.signupScreen(
    onNavigateToLogin: () -> Unit
) {
    composable(route = SIGNUP_ROUTE) {
        SignupScreen(onNavigateToLogin = onNavigateToLogin)
    }
}
