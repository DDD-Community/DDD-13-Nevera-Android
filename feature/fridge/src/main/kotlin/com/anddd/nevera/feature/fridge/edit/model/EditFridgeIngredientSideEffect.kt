package com.anddd.nevera.feature.fridge.edit.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface EditFridgeIngredientSideEffect : NeveraSideEffect {
    data object NavigateBack : EditFridgeIngredientSideEffect
    data object ShowUpdateFailedToast : EditFridgeIngredientSideEffect
    data object ShowCategorySheet : EditFridgeIngredientSideEffect
    data object ShowStorageLocationSheet : EditFridgeIngredientSideEffect
    data object ShowDatePicker : EditFridgeIngredientSideEffect
}
