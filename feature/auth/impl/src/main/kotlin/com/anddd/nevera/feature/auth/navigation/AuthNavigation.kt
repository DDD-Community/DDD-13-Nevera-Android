package com.anddd.nevera.feature.auth.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.anddd.nevera.feature.auth.api.LoginRoute
import com.anddd.nevera.feature.auth.api.SignupRoute
import com.anddd.nevera.feature.auth.main.LoginScreen
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.auth.signup.SignupScreen

fun EntryProviderScope<NavKey>.authEntry(
    googleAuthClient: GoogleAuthClient,
    onNavigateToSignup: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    entry<LoginRoute> {
        LoginScreen(
            googleAuthClient = googleAuthClient,
            onNavigateToHome = onNavigateToHome,
            onNavigateToSignup = onNavigateToSignup,
        )
    }

    entry<SignupRoute> {
        SignupScreen(onNavigateToLogin = onNavigateBack)
    }
}
