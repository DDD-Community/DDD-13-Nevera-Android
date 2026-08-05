package com.anddd.nevera.feature.fridge.api

import kotlinx.serialization.Serializable

// ── 진입점 ────────────────────────────────────────────────────────────────────

/** 냉장고 화면. 바텀 탭이다. */
@Serializable
data object FridgeRoute

/** 식재료 수정 화면. 냉장고 목록의 항목에서 진입한다. */
@Serializable
data class EditFridgeIngredientRoute(val ingredientId: Long)
