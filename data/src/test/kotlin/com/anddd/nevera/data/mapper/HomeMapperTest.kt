package com.anddd.nevera.data.mapper

import com.anddd.nevera.data.model.home.HomeSummaryResponse
import com.anddd.nevera.domain.model.home.HomeWish
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * 홈 요약 응답의 위시 조립 규칙을 고정한다.
 *
 * 서버는 위시 정보를 중첩 객체가 아니라 여섯 개의 개별 nullable 필드로 내려준다.
 * 매퍼는 여섯 개가 모두 있을 때만 위시를 만들고 하나라도 없으면 위시 없음으로 처리하는데,
 * 이 "모두 있을 때만" 조건은 필드가 늘어날 때 빠뜨리기 쉬운 지점이다.
 */
class HomeMapperTest {

    private fun response(
        wishId: Long? = 1L,
        wishName: String? = "에어팟",
        wishAmount: Int? = 300_000,
        accumulated: Int? = 120_000,
        remaining: Int? = 180_000,
        achieved: Boolean? = false,
    ) = HomeSummaryResponse(
        nickname = "네베라",
        wishId = wishId,
        wishName = wishName,
        wishAmount = wishAmount,
        accumulated = accumulated,
        remaining = remaining,
        achieved = achieved,
        totalConsumed = 50_000,
        totalWasted = 12_000,
    )

    @Test
    fun `위시 필드가 모두 있으면 위시를 조립한다`() {
        val summary = response().toDomain()

        assertEquals(
            HomeWish(
                id = 1L,
                name = "에어팟",
                goalAmount = 300_000,
                accumulatedAmount = 120_000,
                remainingAmount = 180_000,
                isAchieved = false,
            ),
            summary.wish,
        )
    }

    @Test
    fun `위시 식별자가 없으면 위시는 null이 된다`() {
        assertNull(response(wishId = null).toDomain().wish)
    }

    @Test
    fun `위시 이름이 없으면 위시는 null이 된다`() {
        assertNull(response(wishName = null).toDomain().wish)
    }

    @Test
    fun `위시 목표 금액이 없으면 위시는 null이 된다`() {
        assertNull(response(wishAmount = null).toDomain().wish)
    }

    @Test
    fun `누적 금액이 없으면 위시는 null이 된다`() {
        assertNull(response(accumulated = null).toDomain().wish)
    }

    @Test
    fun `남은 금액이 없으면 위시는 null이 된다`() {
        assertNull(response(remaining = null).toDomain().wish)
    }

    @Test
    fun `달성 여부가 없으면 위시는 null이 된다`() {
        assertNull(response(achieved = null).toDomain().wish)
    }

    @Test
    fun `위시가 없어도 닉네임과 금액 요약은 그대로 옮겨진다`() {
        val summary = response(wishId = null).toDomain()

        assertEquals("네베라", summary.nickname)
        assertEquals(50_000, summary.rescuedAmount)
        assertEquals(12_000, summary.disposalAmount)
    }

    @Test
    fun `서버의 누적 구조액과 폐기액이 각각 rescuedAmount와 disposalAmount로 옮겨진다`() {
        // 두 필드가 서로 뒤바뀌면 홈 화면의 절약 금액이 폐기 금액으로 표시된다.
        val summary = response().toDomain()

        assertEquals(50_000, summary.rescuedAmount)
        assertEquals(12_000, summary.disposalAmount)
    }
}
