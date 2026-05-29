package com.anddd.nevera.feature.main.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.main.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToNotification: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onNavigateToCamera = onNavigateToCamera,
            onNavigateToGallery = onNavigateToGallery,
            onNavigateToNotification = onNavigateToNotification,
        )
    }
}
