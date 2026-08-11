package com.anddd.nevera.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.anddd.nevera.feature.auth.api.LoginRoute
import com.anddd.nevera.feature.auth.api.SignupRoute
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.auth.navigation.authEntry
import com.anddd.nevera.feature.splash.main.SplashScreen

/**
 * 인증 이전 화면들. 앱 본문과 백스택을 공유하지 않는다.
 *
 * 여기서 홈·냉장고 같은 본문 목적지로 가는 길은 **존재하지 않는다**.
 * 인증이 끝나면 [onAuthenticated]로 알리고, :app이 앱 본문으로 통째로 갈아 끼운다.
 */
@Composable
fun PreSessionHost(
    googleAuthClient: GoogleAuthClient,
    isChecking: Boolean,
    onAuthChecked: (authenticated: Boolean) -> Unit,
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isChecking) {
        SplashScreen(
            onNavigateToHome = { onAuthChecked(true) },
            onNavigateToLogin = { onAuthChecked(false) },
        )
        return
    }

    val backStack = rememberNavBackStack(LoginRoute)
    val onBack = {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = onBack,
        entryProvider = entryProvider {
            authEntry(
                googleAuthClient = googleAuthClient,
                onNavigateToSignup = { backStack.add(SignupRoute) },
                onNavigateBack = onBack,
                onNavigateToHome = onAuthenticated,
            )
        },
    )
}
