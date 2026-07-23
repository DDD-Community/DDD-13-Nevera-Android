package com.anddd.nevera.data.testutil

import com.anddd.nevera.data.model.home.HomeSummaryResponse

/**
 * 홈 요약 응답([HomeSummaryResponse]) 픽스처.
 *
 * 위시 관련 필드는 기본적으로 null(위시 없음)이며, 필요한 케이스에서 인자로 채운다.
 */
internal fun homeSummaryResponse(
    nickname: String = "네베라",
    totalConsumed: Int = 50_000,
    totalWasted: Int = 12_000,
) = HomeSummaryResponse(
    nickname = nickname,
    wishId = null,
    wishName = null,
    wishAmount = null,
    accumulated = null,
    remaining = null,
    achieved = null,
    totalConsumed = totalConsumed,
    totalWasted = totalWasted,
)
