package com.anddd.nevera.feature.auth.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.anddd.nevera.core.navigation.Navigator
import com.anddd.nevera.feature.auth.api.LoginRoute
import com.anddd.nevera.feature.auth.api.SignupRoute
import com.anddd.nevera.feature.auth.main.LoginScreen
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.auth.signup.SignupScreen

/**
 * 인증 화면들.
 *
 * @param onAuthenticated 인증에 성공했다. 앱 본문으로 갈아 끼우는 것은 화면 이동이 아니라
 *   세션 전이라 조립 지점이 처리한다.
 */
fun EntryProviderScope<NavKey>.authEntry(
    googleAuthClient: GoogleAuthClient,
    navigator: Navigator,
    onAuthenticated: () -> Unit,
) {
    entry<LoginRoute> {
        LoginScreen(
            googleAuthClient = googleAuthClient,
            onNavigateToHome = onAuthenticated,
            onNavigateToSignup = { navigator.navigate(SignupRoute) },
        )
    }

    entry<SignupRoute> {
        SignupScreen(onNavigateToLogin = navigator::goBack)
    }
}
