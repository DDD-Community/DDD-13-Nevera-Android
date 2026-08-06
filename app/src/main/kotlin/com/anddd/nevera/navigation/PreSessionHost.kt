package com.anddd.nevera.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.anddd.nevera.core.navigation.Navigator
import com.anddd.nevera.feature.auth.api.AuthGraphRoute
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.auth.navigation.authNavGraph
import com.anddd.nevera.feature.splash.main.SplashScreen

/**
 * 인증 이전 화면들. 메인 그래프와 백스택을 공유하지 않는다.
 *
 * 여기서 홈·냉장고 같은 메인 목적지로 가는 길은 **존재하지 않는다**.
 * 인증이 끝나면 [onAuthenticated]로 알리고, :app이 메인 그래프로 통째로 갈아 끼운다.
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

    val navController = rememberNavController()
    val navigator = Navigator(navController)

    NavHost(
        navController = navController,
        startDestination = AuthGraphRoute,
        modifier = modifier,
    ) {
        authNavGraph(
            googleAuthClient = googleAuthClient,
            navigator = navigator,
            onNavigateToHome = onAuthenticated,
        )
    }
}
