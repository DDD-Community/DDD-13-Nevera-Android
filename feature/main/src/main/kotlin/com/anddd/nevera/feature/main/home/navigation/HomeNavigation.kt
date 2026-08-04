package com.anddd.nevera.feature.main.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.main.home.HomeScreen
import com.anddd.nevera.feature.notification.api.NotificationRoute
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    navController: NavController,
    onNavigateToCamera: () -> Unit,
    onNavigateToGallery: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onNavigateToCamera = onNavigateToCamera,
            onNavigateToGallery = onNavigateToGallery,
            onNavigateToNotification = {
                navController.navigate(NotificationRoute) { launchSingleTop = true }
            },
        )
    }
}
