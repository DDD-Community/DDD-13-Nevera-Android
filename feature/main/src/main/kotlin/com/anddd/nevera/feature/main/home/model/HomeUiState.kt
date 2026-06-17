package com.anddd.nevera.feature.main.home.model

import androidx.compose.runtime.Immutable
import com.anddd.nevera.core.mvi.NeveraState

@Immutable
data class HomeUiState(
    val isLoading: Boolean = false,
    val hasUnreadNotification: Boolean = false,
    val profile: HomeProfileUiModel = HomeProfileUiModel(nickname = ""),
    val wish: HomeWishUiModel? = null,
    val savings: HomeSavingsUiModel = HomeSavingsUiModel(rescuedAmount = 0, disposalAmount = 0),
    val ingredientFilterTab: IngredientFilterTab = IngredientFilterTab.Rescue,
    val rescuedIngredients: PaginatedListState<IngredientUiModel> = PaginatedListState(),
    val disposalIngredients: PaginatedListState<IngredientUiModel> = PaginatedListState(),
) : NeveraState
