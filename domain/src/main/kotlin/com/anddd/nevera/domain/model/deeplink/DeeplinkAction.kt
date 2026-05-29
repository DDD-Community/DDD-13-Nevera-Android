package com.anddd.nevera.domain.model.deeplink

sealed interface DeeplinkAction {
    data class NavigateToIngredientDetail(val ingredientId: String) : DeeplinkAction
}
