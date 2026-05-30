package com.anddd.nevera.feature.main.home.model

import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface HomeIntent : NeveraIntent {

    data class RecentIngredientTabClick(val tab: IngredientFilterTab) : HomeIntent

    data object AddIngredientClick : HomeIntent

    data class LoadMoreIngredients(val tab: IngredientFilterTab) : HomeIntent

    data class UpdateNicknameClick(val nickname: String) : HomeIntent

    data object CreateWishClick : HomeIntent

    data class CreateWishConfirmed(val name: String, val goalAmount: Long) : HomeIntent

    data object WishEditClick : HomeIntent

    data class UpdateWishConfirmed(val id: Long, val name: String, val goalAmount: Long) : HomeIntent

    data object NotificationIconClicked : HomeIntent
}
