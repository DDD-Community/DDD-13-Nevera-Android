package com.anddd.nevera.feature.main.home.model

import com.anddd.nevera.domain.model.home.HomeWish

data class HomeWishUiModel(
    val id: Long,
    val name: String,
    val goalAmount: Int,
    val accumulatedAmount: Int,
    val remainingAmount: Int,
    val isAchieved: Boolean,
)

internal fun HomeWish.toUiModel(): HomeWishUiModel = HomeWishUiModel(
    id = id,
    name = name,
    goalAmount = goalAmount,
    accumulatedAmount = accumulatedAmount,
    remainingAmount = remainingAmount,
    isAchieved = isAchieved,
)
