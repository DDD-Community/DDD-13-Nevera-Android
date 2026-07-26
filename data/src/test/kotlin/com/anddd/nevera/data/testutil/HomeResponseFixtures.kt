package com.anddd.nevera.data.testutil

import com.anddd.nevera.data.model.home.HomeSummaryResponse

/**
 * 홈 요약 응답([HomeSummaryResponse]) 픽스처.
 *
 * 위시 관련 필드는 항상 null(위시 없음)로 만든다. 위시가 있는 응답이 필요하면
 * [HomeSummaryResponse]를 직접 생성한다(위시 조립 규칙은 `HomeMapperTest`가 이미 검증).
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
