package com.anddd.nevera.feature.fridge.main.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface FridgeSideEffect : NeveraSideEffect {
    data class ShowToast(val message: String) : FridgeSideEffect

    data object ShowCaptureModeBottomSheet : FridgeSideEffect

    data object NavigateToNotification : FridgeSideEffect

    data class ShowRescueBottomSheet(val item: FridgeIngredientUiModel) : FridgeSideEffect

    data class ShowDisposeBottomSheet(val item: FridgeIngredientUiModel) : FridgeSideEffect

    data class NavigateToEditIngredient(val ingredientId: Long) : FridgeSideEffect

    data class ScrollToIngredient(val index: Int) : FridgeSideEffect
}
