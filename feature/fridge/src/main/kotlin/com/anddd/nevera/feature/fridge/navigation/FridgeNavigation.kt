package com.anddd.nevera.feature.fridge.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.fridge.edit.EditFridgeIngredientScreen
import com.anddd.nevera.feature.fridge.main.FridgeScreen
import kotlinx.serialization.Serializable

@Serializable
data object FridgeRoute

@Serializable
data class EditFridgeIngredientRoute(val ingredientId: Long)

fun NavGraphBuilder.fridgeScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToEditIngredient: (Long) -> Unit,
) {
    composable<FridgeRoute> {
        FridgeScreen(
            onNavigateToCamera = onNavigateToCamera,
            onNavigateToGallery = onNavigateToGallery,
            onNavigateToNotification = onNavigateToNotification,
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
