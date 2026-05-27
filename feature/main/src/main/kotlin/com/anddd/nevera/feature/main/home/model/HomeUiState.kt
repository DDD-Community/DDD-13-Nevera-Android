package com.anddd.nevera.feature.main.home.model

import com.anddd.nevera.core.mvi.NeveraState

data class HomeUiState(
    val isLoading: Boolean = false,
    val wishUiModel: WishUiModel = WishUiModel(
        nickname = "",
        wish = "",
        savedMoney = 0,
        goalMoney = 0,
    ),
    val rescueDisposalCostUiModel: RescueDisposalCostUiModel = RescueDisposalCostUiModel(),
    val ingredientFilterTab: IngredientFilterTab = IngredientFilterTab.Rescue,
) : NeveraState
