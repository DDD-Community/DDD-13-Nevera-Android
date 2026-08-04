package com.anddd.nevera.feature.main.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.core.navigation.Navigator
import com.anddd.nevera.feature.main.home.HomeScreen
import com.anddd.nevera.feature.notification.api.NotificationRoute
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    navigator: Navigator,
    onNavigateToCamera: () -> Unit,
    onNavigateToGallery: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onNavigateToCamera = onNavigateToCamera,
            onNavigateToGallery = onNavigateToGallery,
            onNavigateToNotification = { navigator.navigate(NotificationRoute) },
        )
    }
}
