package com.anddd.nevera.feature.fridge.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.core.navigation.Navigator
import com.anddd.nevera.feature.fridge.api.FridgeRoute
import com.anddd.nevera.feature.fridge.edit.EditFridgeIngredientScreen
import com.anddd.nevera.feature.fridge.main.FridgeScreen
import com.anddd.nevera.feature.notification.api.NotificationRoute
import kotlinx.serialization.Serializable

@Serializable
data class EditFridgeIngredientRoute(val ingredientId: Long)

fun NavGraphBuilder.fridgeScreen(
    navigator: Navigator,
    onNavigateToCamera: () -> Unit,
    onNavigateToGallery: () -> Unit,
) {
    composable<FridgeRoute> {
        FridgeScreen(
            onNavigateToCamera = onNavigateToCamera,
            onNavigateToGallery = onNavigateToGallery,
            onNavigateToNotification = { navigator.navigate(NotificationRoute) },
            onNavigateToEditIngredient = { id -> navigator.navigate(EditFridgeIngredientRoute(id)) },
        )
    }
}

fun NavGraphBuilder.editFridgeIngredientScreen(
    navigator: Navigator,
) {
    composable<EditFridgeIngredientRoute> {
        EditFridgeIngredientScreen(onNavigateBack = navigator::goBack)
    }
}
