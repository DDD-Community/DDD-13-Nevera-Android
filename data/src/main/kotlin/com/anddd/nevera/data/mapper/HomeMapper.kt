package com.anddd.nevera.data.mapper

import com.anddd.nevera.data.model.home.HomeSummaryResponse
import com.anddd.nevera.domain.model.home.HomeSummary
import com.anddd.nevera.domain.model.home.HomeWish

internal fun HomeSummaryResponse.toDomain(): HomeSummary = HomeSummary(
    nickname = nickname,
    wish = wishId?.let {
        HomeWish(
            id = it,
            name = wishName ?: "",
            goalAmount = wishAmount ?: 0,
            accumulatedAmount = accumulated ?: 0,
            remainingAmount = remaining ?: 0,
            isAchieved = achieved ?: false,
        )
    },
    rescuedAmount = totalConsumed,
    dispositionAmount = totalWasted,
)
