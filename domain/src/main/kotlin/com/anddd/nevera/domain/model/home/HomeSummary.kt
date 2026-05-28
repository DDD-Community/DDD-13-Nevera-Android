package com.anddd.nevera.domain.model.home

data class HomeSummary(
    val nickname: String,
    val wish: HomeWish?,
    val rescuedAmount: Int,
    val disposalAmount: Int,
)

data class HomeWish(
    val id: Long,
    val name: String,
    val goalAmount: Int,
    val accumulatedAmount: Int,
    val remainingAmount: Int,
    val isAchieved: Boolean,
)
