package com.anddd.nevera.feature.fridge.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.fridge.edit.EditFridgeIngredientScreen
import com.anddd.nevera.feature.fridge.main.FridgeScreen
import com.anddd.nevera.feature.notification.api.NotificationRoute
import kotlinx.serialization.Serializable

@Serializable
data object FridgeRoute

@Serializable
data class EditFridgeIngredientRoute(val ingredientId: Long)

fun NavGraphBuilder.fridgeScreen(
    navController: NavController,
    onNavigateToCamera: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToEditIngredient: (Long) -> Unit,
) {
    composable<FridgeRoute> {
        FridgeScreen(
            onNavigateToCamera = onNavigateToCamera,
            onNavigateToGallery = onNavigateToGallery,
            onNavigateToNotification = {
                navController.navigate(NotificationRoute) { launchSingleTop = true }
            },
            onNavigateToEditIngredient = onNavigateToEditIngredient,
        )
    }
}

fun NavGraphBuilder.editFridgeIngredientScreen(
    onNavigateBack: () -> Unit,
) {
    composable<EditFridgeIngredientRoute> {
        EditFridgeIngredientScreen(onNavigateBack = onNavigateBack)
    }
}
