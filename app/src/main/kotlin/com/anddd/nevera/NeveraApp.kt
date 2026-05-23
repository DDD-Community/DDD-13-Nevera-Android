package com.anddd.nevera

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anddd.nevera.core.designsystem.component.navigationbar.NeveraNavigationBar
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.navigation.NeveraNavHost
import com.anddd.nevera.navigation.TopLevelDestination
import com.anddd.nevera.navigation.toNavigationBarItem
import kotlin.reflect.KClass


@Composable
fun NeveraApp(googleAuthClient: GoogleAuthClient) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination

    val topLevelDestinations = TopLevelDestination.entries
    val isTopLevel = topLevelDestinations.any { destination ->
        currentDestination.matchesRoute(destination.routeClass)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (isTopLevel) {
                NeveraNavigationBar(
                    items = topLevelDestinations.map { destination ->
                        destination.toNavigationBarItem(
                            selected = currentDestination.matchesRoute(destination.routeClass),
                        )
                    },
                    onItemClick = { destination ->
                        navController.navigate(destination.route) {
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
            googleAuthClient = googleAuthClient,
            modifier = Modifier.padding(innerPadding),
        )
    }
}


private fun NavDestination?.matchesRoute(routeClass: KClass<*>): Boolean {
    return this?.hasRoute(routeClass) == true
}