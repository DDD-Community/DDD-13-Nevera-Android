package com.anddd.nevera.feature.fridge.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.fridge.main.FridgeScreen
import kotlinx.serialization.Serializable

@Serializable
data object FridgeRoute

fun NavGraphBuilder.fridgeScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToNotification: () -> Unit,
) {
    composable<FridgeRoute> {
        FridgeScreen(
            onNavigateToCamera = onNavigateToCamera,
            onNavigateToGallery = onNavigateToGallery,
            onNavigateToNotification = onNavigateToNotification,
        )
    }
}
