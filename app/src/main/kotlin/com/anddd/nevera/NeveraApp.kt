package com.anddd.nevera

import android.widget.Toast
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anddd.nevera.core.designsystem.component.navigationbar.NeveraNavigationBar
import com.anddd.nevera.domain.model.deeplink.DeeplinkAction
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.main.home.navigation.HomeRoute
import com.anddd.nevera.navigation.NeveraNavHost
import com.anddd.nevera.navigation.TopLevelDestination
import com.anddd.nevera.navigation.toNavigationBarItem
import kotlin.reflect.KClass


@Composable
fun NeveraApp(
    googleAuthClient: GoogleAuthClient,
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination

    LaunchedEffect(Unit) {
        mainViewModel.sideEffect.collect { action ->
            when (action) {
                is DeeplinkAction.NavigateToIngredientDetail -> {
                    // TODO: 냉장고 탭 구현 후 식재료(id) 포커스 네비게이션 추가
                    Toast.makeText(navController.context, "냉장고 탭으로 이동", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val topLevelDestinations = TopLevelDestination.entries
    val isTopLevel = topLevelDestinations.any { destination ->
        currentDestination.matchesRoute(destination.screenRouteClass)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
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
