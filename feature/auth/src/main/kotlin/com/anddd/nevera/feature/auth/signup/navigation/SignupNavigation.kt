package com.anddd.nevera.feature.auth.signup.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.auth.signup.SignupScreen
import kotlinx.serialization.Serializable

@Serializable
data object SignupRoute

fun NavGraphBuilder.signupScreen(
    onNavigateToLogin: () -> Unit
) {
    composable<SignupRoute> {
        SignupScreen(onNavigateToLogin = onNavigateToLogin)
    }
}
