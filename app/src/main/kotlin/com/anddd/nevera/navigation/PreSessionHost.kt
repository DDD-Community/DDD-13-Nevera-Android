package com.anddd.nevera.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.anddd.nevera.core.navigation.SingleStackNavigator
import com.anddd.nevera.feature.auth.api.LoginRoute
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.auth.navigation.authEntry
import com.anddd.nevera.feature.splash.api.SplashRoute
import com.anddd.nevera.feature.splash.navigation.splashEntry

/**
 * 인증 이전 화면들. 앱 본문과 백스택을 공유하지 않는다.
 *
 * 여기서 홈·냉장고 같은 본문 목적지로 가는 길은 **존재하지 않는다**.
 * 인증이 끝나면 [onAuthenticated]로 알리고, :app이 앱 본문으로 통째로 갈아 끼운다.
 *
 * 갈래가 없는 선형 흐름이라 스택 하나로 충분하다. 자동 로그인을 확인해야 하는 진입에서는
 * 스플래시가 루트이고, 로그아웃 직후처럼 확인이 필요 없는 진입에서는 로그인이 루트다.
 */
@Composable
fun PreSessionHost(
    googleAuthClient: GoogleAuthClient,
    isChecking: Boolean,
    onAuthChecked: (authenticated: Boolean) -> Unit,
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(if (isChecking) SplashRoute else LoginRoute)
    val navigator = remember(backStack) { SingleStackNavigator(backStack) }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = navigator::goBack,
        entryProvider = entryProvider {
            splashEntry(navigator, onAuthChecked = onAuthChecked)
            authEntry(googleAuthClient, navigator, onAuthenticated = onAuthenticated)
        },
    )
}
