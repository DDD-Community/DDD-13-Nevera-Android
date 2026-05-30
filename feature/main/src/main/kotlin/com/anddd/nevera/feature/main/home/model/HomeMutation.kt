package com.anddd.nevera.feature.main.home.model

import com.anddd.nevera.core.mvi.NeveraMutation

sealed interface HomeMutation : NeveraMutation {
    data object Loading : HomeMutation

    data object LoadComplete : HomeMutation

    data class SetRecentIngredientFilterTab(val tab: IngredientFilterTab) : HomeMutation

    data class ShowProfile(val profile: HomeProfileUiModel) : HomeMutation

    data class ShowWish(val wish: HomeWishUiModel) : HomeMutation

    data object ShowEmptyWish : HomeMutation

    data class ShowSavings(val savings: HomeSavingsUiModel) : HomeMutation

    data class ShowRescuedIngredients(
        val ingredients: List<IngredientUiModel>,
        val hasMore: Boolean,
    ) : HomeMutation

    data object LoadingMoreRescuedIngredients : HomeMutation

    data class AppendRescuedIngredients(
        val ingredients: List<IngredientUiModel>,
        val hasMore: Boolean,
    ) : HomeMutation

    data class ShowDisposalIngredients(
        val ingredients: List<IngredientUiModel>,
        val hasMore: Boolean,
    ) : HomeMutation

    data object LoadingMoreDisposalIngredients : HomeMutation

    data class AppendDisposalIngredients(
        val ingredients: List<IngredientUiModel>,
        val hasMore: Boolean,
    ) : HomeMutation

    data class UpdateNickname(val nickname: String) : HomeMutation

    data class BadgeUpdated(val hasUnread: Boolean) : HomeMutation
}
