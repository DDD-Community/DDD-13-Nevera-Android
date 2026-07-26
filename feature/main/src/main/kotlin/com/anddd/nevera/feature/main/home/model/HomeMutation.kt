package com.anddd.nevera.feature.main.home.model

import com.anddd.nevera.core.mvi.NeveraMutation
import kotlinx.collections.immutable.PersistentList

sealed interface HomeMutation : NeveraMutation {
    data object Loading : HomeMutation

    data object LoadComplete : HomeMutation

    data class SetRecentIngredientFilterTab(val tab: IngredientFilterTab) : HomeMutation

    data class ShowProfile(val profile: HomeProfileUiModel) : HomeMutation

    data class ShowWish(val wish: HomeWishUiModel) : HomeMutation

    data object ShowEmptyWish : HomeMutation

    data class ShowSavings(val savings: HomeSavingsUiModel) : HomeMutation

    data class ShowRescuedIngredients(
        val ingredients: PersistentList<IngredientUiModel>,
        val hasMore: Boolean,
    ) : HomeMutation

    data object LoadingMoreRescuedIngredients : HomeMutation

    /** 구조 목록 추가 로드 실패 시 로딩 표시를 해제해 다음 요청이 막히지 않게 한다. */
    data object LoadMoreRescuedFailed : HomeMutation

    data class AppendRescuedIngredients(
        val ingredients: List<IngredientUiModel>,
        val hasMore: Boolean,
    ) : HomeMutation

    data class ShowDisposalIngredients(
        val ingredients: PersistentList<IngredientUiModel>,
        val hasMore: Boolean,
    ) : HomeMutation

    data object LoadingMoreDisposalIngredients : HomeMutation

    /** 폐기 목록 추가 로드 실패 시 로딩 표시를 해제해 다음 요청이 막히지 않게 한다. */
    data object LoadMoreDisposalFailed : HomeMutation

    data class AppendDisposalIngredients(
        val ingredients: List<IngredientUiModel>,
        val hasMore: Boolean,
    ) : HomeMutation

    data class UpdateNickname(val nickname: String) : HomeMutation

    data class BadgeUpdated(val hasUnread: Boolean) : HomeMutation
}
