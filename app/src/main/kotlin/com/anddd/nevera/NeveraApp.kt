package com.anddd.nevera

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anddd.nevera.core.designsystem.component.navigationbar.NeveraNavigationBar
import com.anddd.nevera.domain.model.deeplink.DeeplinkAction
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.main.home.navigation.HomeRoute
import com.anddd.nevera.feature.splash.main.navigation.SplashRoute
import com.anddd.nevera.navigation.NeveraNavHost
import com.anddd.nevera.navigation.TopLevelDestination
import com.anddd.nevera.navigation.toNavigationBarItem
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.first


@Composable
fun NeveraApp(
    googleAuthClient: GoogleAuthClient,
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val topLevelDestinations = TopLevelDestination.entries

    LaunchedEffect(Unit) {
        mainViewModel.sideEffect.collect { action ->
            when (action) {
                is DeeplinkAction.NavigateToIngredientDetail -> {
                    navController.awaitSplashExit()
                    navController.ensureHomeAsBackStackRoot()
                    navController.navigate(TopLevelDestination.Fridge.route) {
                        popUpTo<HomeRoute> { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
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
                            popUpTo<HomeRoute> {
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
            googleAuthClient = googleAuthClient,
            onDeeplink = mainViewModel::dispatchDeeplink,
            modifier = Modifier.padding(innerPadding),
        )
    }
}


private fun NavDestination?.matchesRoute(routeClass: KClass<*>): Boolean {
    return this?.hasRoute(routeClass) == true
}

// SplashViewModel의 자동 로그인 분기(최소 2초 대기)가 끝나기 전에 네비게이션하면,
// 이후 Splash의 onNavigateToHome/onNavigateToLogin이 화면을 덮어쓴다.
private suspend fun NavHostController.awaitSplashExit() {
    currentBackStackEntryFlow.first { entry -> !entry.destination.hasRoute(SplashRoute::class) }
}

// 콜드 스타트(앱 프로세스가 종료된 상태에서 알림 클릭)로 시작하면 SplashRoute부터 시작하므로
// HomeRoute가 백스택에 없을 수 있다.
private fun NavHostController.ensureHomeAsBackStackRoot() {
    val hasHomeInBackStack = runCatching {
        getBackStackEntry<HomeRoute>()
        true
    }.getOrDefault(false)

    if (hasHomeInBackStack) {
        // top-level 화면이 아닌 화면에서 알림을 받으면, top-level에 도달할 때까지 pop한다.
        while (currentDestination?.let { dest ->
                TopLevelDestination.entries.none { dest.matchesRoute(it.screenRouteClass) }
            } == true
        ) {
            if (!popBackStack()) break
        }
    } else {
        // HomeRoute를 백스택 루트로 세워야 Fridge 진입 후 백키로 Home에 도달할 수 있다.
        navigate(HomeRoute) {
            popUpTo(graph.findStartDestination().id) { inclusive = true }
        }
    }
}
