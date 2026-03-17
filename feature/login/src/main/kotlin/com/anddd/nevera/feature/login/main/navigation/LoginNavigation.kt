package com.anddd.nevera.feature.login.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.login.main.LoginScreen

const val LOGIN_ROUTE = "login"

fun NavGraphBuilder.loginScreen(
    onLoginSuccess: (String) -> Unit,
    onGoogleLoginClick: () -> Unit,
    onKakaoLoginClick: () -> Unit
) {
    composable(route = LOGIN_ROUTE) {
        LoginScreen(
            onLoginSuccess = onLoginSuccess,
            onGoogleLoginClick = onGoogleLoginClick,
            onKakaoLoginClick = onKakaoLoginClick
        )
    }
}
