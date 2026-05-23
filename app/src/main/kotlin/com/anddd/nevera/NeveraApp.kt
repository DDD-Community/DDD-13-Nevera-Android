package com.anddd.nevera

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anddd.nevera.core.designsystem.component.navigationbar.NeveraNavigationBar
import com.anddd.nevera.core.designsystem.component.navigationbar.NeveraNavigationBarItem
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.auth.navigation.AuthRoute
import com.anddd.nevera.feature.auth.navigation.authNavGraph
import com.anddd.nevera.feature.main.home.navigation.HomeRoute
import com.anddd.nevera.feature.mypage.appinfo.navigation.AppInfoRoute
import com.anddd.nevera.feature.mypage.appinfo.navigation.appInfoScreen
import com.anddd.nevera.feature.mypage.main.navigation.MyPageRoute
import com.anddd.nevera.feature.mypage.settingaccount.navigation.SettingAccountRoute
import com.anddd.nevera.feature.mypage.settingaccount.navigation.settingAccountScreen
import com.anddd.nevera.feature.splash.main.navigation.SplashRoute
import com.anddd.nevera.feature.splash.main.navigation.splashScreen
import com.anddd.nevera.navigation.MainRoute
import com.anddd.nevera.navigation.mainNavGraph

enum class MainTab { HOME, MY_PAGE }

@Composable
fun NeveraApp(googleAuthClient: GoogleAuthClient) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    val shouldShowBottomBar = currentDestination?.hierarchy
        ?.any { it.hasRoute(MainRoute::class) } == true

    val selectedTab = when {
        currentDestination?.hierarchy?.any { it.hasRoute(HomeRoute::class) } == true -> MainTab.HOME
        currentDestination?.hierarchy?.any { it.hasRoute(MyPageRoute::class) } == true -> MainTab.MY_PAGE
        else -> null
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (shouldShowBottomBar) {
                NeveraNavigationBar(
                    items = mainNavigationItems(selectedTab),
                    onItemClick = { tab ->
                        val route = when (tab) {
                            MainTab.HOME -> HomeRoute
                            MainTab.MY_PAGE -> MyPageRoute
                        }
                        navController.navigate(route) {
                            popUpTo<MainRoute> { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SplashRoute,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            splashScreen(
                onNavigateToLogin = {
                    navController.navigate(AuthRoute) {
                        popUpTo<SplashRoute> { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(MainRoute) {
                        popUpTo<SplashRoute> { inclusive = true }
                    }
                },
            )
            authNavGraph(
                googleAuthClient = googleAuthClient,
                navController = navController,
                onNavigateToHome = {
                    navController.navigate(MainRoute) {
                        popUpTo<AuthRoute> { inclusive = true }
                    }
                }
            )
            mainNavGraph(
                onNavigateToAppInfo = {
                    navController.navigate(AppInfoRoute)
                },
                onNavigateToAccountSetting = {
                    navController.navigate(SettingAccountRoute)
                },
            )
            appInfoScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
            settingAccountScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(AuthRoute) {
                        popUpTo<MainRoute> { inclusive = true }
                    }
                },
            )
        }
    }
}

@Composable
private fun mainNavigationItems(selectedTab: MainTab?): List<NeveraNavigationBarItem<MainTab>> = listOf(
    NeveraNavigationBarItem(
        tag = MainTab.HOME,
        selectedIcon = NeveraIcons.NavHomeFilled,
        unselectedIcon = NeveraIcons.NavHome,
        selected = selectedTab == MainTab.HOME,
    ),
    NeveraNavigationBarItem(
        tag = MainTab.MY_PAGE,
        selectedIcon = NeveraIcons.NavMyFilled,
        unselectedIcon = NeveraIcons.NavMy,
        selected = selectedTab == MainTab.MY_PAGE,
    ),
)
