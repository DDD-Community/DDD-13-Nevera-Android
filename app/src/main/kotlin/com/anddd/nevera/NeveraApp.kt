package com.anddd.nevera

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anddd.nevera.core.designsystem.component.navigationbar.NeveraNavigationBar
import com.anddd.nevera.core.navigation.Navigator
import com.anddd.nevera.domain.model.deeplink.DeeplinkAction
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.main.api.HomeRoute
import com.anddd.nevera.navigation.NeveraNavHost
import com.anddd.nevera.navigation.PreSessionHost
import com.anddd.nevera.navigation.TopLevelDestination
import com.anddd.nevera.navigation.toNavigationBarItem
import kotlin.reflect.KClass


@Composable
fun NeveraApp(
    googleAuthClient: GoogleAuthClient,
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val sessionState by mainViewModel.sessionState.collectAsState()

    // 인증 이전에는 메인 그래프가 아예 존재하지 않는다.
    // 로그인하지 않은 상태로 홈·냉장고에 도달하는 경로가 코드에 없다.
    if (sessionState !is SessionState.Authenticated) {
        PreSessionHost(
            googleAuthClient = googleAuthClient,
            isChecking = sessionState is SessionState.Checking,
            onAuthChecked = mainViewModel::onAuthChecked,
            onAuthenticated = mainViewModel::onAuthenticated,
        )
        return
    }

    AuthenticatedApp(mainViewModel = mainViewModel)
}

@Composable
private fun AuthenticatedApp(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val navigator = remember(navController) { Navigator(navController) }
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val topLevelDestinations = TopLevelDestination.entries

    // 인증 이후에만 이 컴포저블이 존재하므로, 딥링크는 항상 메인 그래프 위에서 소비된다.
    // 스플래시 종료를 기다리거나 백스택을 손질할 필요가 없어졌다.
    LaunchedEffect(Unit) {
        mainViewModel.sideEffect.collect { action ->
            when (action) {
                is DeeplinkAction.NavigateToIngredientDetail ->
                    navController.navigate(TopLevelDestination.Fridge.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
            }
        }
    }

    val isTopLevel = topLevelDestinations.any { destination ->
        currentDestination.matchesRoute(destination.screenRouteClass)
    }

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            if (isTopLevel) {
                NeveraNavigationBar(
                    items = topLevelDestinations.map { destination ->
                        destination.toNavigationBarItem(
                            selected = currentDestination?.hierarchy?.any {
                                it.hasRoute(destination.routeClass)
                            } == true,
                        )
                    },
                    onItemClick = { destination ->
                        navController.navigate(destination.route) {
                            // 이제 그래프의 시작 목적지가 실제 백스택 루트와 일치하므로
                            // HomeRoute를 하드코딩하지 않아도 된다.
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NeveraNavHost(
            navController = navController,
            navigator = navigator,
            onDeeplink = mainViewModel::dispatchDeeplink,
            onSignedOut = mainViewModel::onSignedOut,
            modifier = Modifier.padding(innerPadding),
        )
    }
}


private fun NavDestination?.matchesRoute(routeClass: KClass<*>): Boolean {
    return this?.hasRoute(routeClass) == true
}

