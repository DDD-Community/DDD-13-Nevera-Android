package com.anddd.nevera.feature.main.home.model

data class HomeWishUiModel(
    val id: Long,
    val name: String,
    val goalAmount: Int,
    val accumulatedAmount: Int,
    val remainingAmount: Int,
    val isAchieved: Boolean,
)
