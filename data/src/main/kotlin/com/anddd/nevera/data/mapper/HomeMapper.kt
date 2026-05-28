package com.anddd.nevera.data.mapper

import com.anddd.nevera.data.model.home.HomeSummaryResponse
import com.anddd.nevera.domain.model.home.HomeSummary
import com.anddd.nevera.domain.model.home.HomeWish

internal fun HomeSummaryResponse.toDomain(): HomeSummary = HomeSummary(
    nickname = nickname,
    wish = if (wishId != null && wishName != null && wishAmount != null &&
        accumulated != null && remaining != null && achieved != null
    ) {
        HomeWish(
            id = wishId,
            name = wishName,
            goalAmount = wishAmount,
            accumulatedAmount = accumulated,
            remainingAmount = remaining,
            isAchieved = achieved,
        )
    } else null,
    rescuedAmount = totalConsumed,
    disposalAmount = totalWasted,
)
