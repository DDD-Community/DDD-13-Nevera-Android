package com.anddd.nevera.feature.fridge.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.anddd.nevera.core.navigation.nav3.Nav3Navigator
import com.anddd.nevera.feature.fridge.api.EditFridgeIngredientRoute
import com.anddd.nevera.feature.fridge.api.FridgeRoute
import com.anddd.nevera.feature.fridge.edit.EditFridgeIngredientScreen
import com.anddd.nevera.feature.fridge.main.FridgeScreen
import com.anddd.nevera.feature.ingredient.api.OcrCaptureRoute
import com.anddd.nevera.feature.notification.api.NotificationRoute

fun EntryProviderScope<NavKey>.fridgeEntry(navigator: Nav3Navigator) {
    entry<FridgeRoute> {
        FridgeScreen(
            onNavigateToCamera = { navigator.navigate(OcrCaptureRoute()) },
            onNavigateToGallery = { navigator.navigate(OcrCaptureRoute(openGallery = true)) },
            onNavigateToNotification = { navigator.navigate(NotificationRoute) },
            onNavigateToEditIngredient = { id -> navigator.navigate(EditFridgeIngredientRoute(id)) },
        )
    }
    entry<EditFridgeIngredientRoute> { key ->
        EditFridgeIngredientScreen(
            ingredientId = key.ingredientId,
            onNavigateBack = navigator::goBack,
        )
    }
}
