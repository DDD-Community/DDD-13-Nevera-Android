package com.anddd.nevera.feature.splash.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.anddd.nevera.core.navigation.Navigator
import com.anddd.nevera.core.navigation.replace
import com.anddd.nevera.feature.auth.api.LoginRoute
import com.anddd.nevera.feature.splash.api.SplashRoute
import com.anddd.nevera.feature.splash.main.SplashScreen

/**
 * 자동 로그인 확인 화면.
 *
 * @param onAuthChecked 확인 결과를 알린다. 인증됐다면 앱 본문으로 갈아 끼우는 것은
 *   화면 이동이 아니라 세션 전이라 조립 지점이 처리한다.
 */
fun EntryProviderScope<NavKey>.splashEntry(
    navigator: Navigator,
    onAuthChecked: (authenticated: Boolean) -> Unit,
) {
    entry<SplashRoute> {
        SplashScreen(
            onNavigateToHome = { onAuthChecked(true) },
            // 미인증이면 로그인으로. 확인이 끝난 화면이라 되돌아갈 이유가 없다.
            onNavigateToLogin = {
                onAuthChecked(false)
                navigator.replace<SplashRoute>(LoginRoute)
            },
        )
    }
}
