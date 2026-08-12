package com.anddd.nevera.navigation

import com.anddd.nevera.feature.fridge.api.EditFridgeIngredientRoute
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * 딥링크는 외부 입력이다. 유효하지 않은 형태가 들어와도 앱이 엉뚱한 화면을
 * 열지 않는 것을 고정한다.
 */
class DeeplinkResolverTest {

    private val resolver = DeeplinkResolver()

    @Test
    @DisplayName("식재료 상세 딥링크는 냉장고 탭 위에 상세 화면을 얹는다")
    fun resolvesIngredientDetail() {
        val target = resolver.resolve("nevera://detail/101")

        assertEquals(TopLevelDestination.Fridge, target?.root)
        assertEquals(listOf(EditFridgeIngredientRoute(101L)), target?.stack)
    }

    @Test
    @DisplayName("쿼리 파라미터가 붙어도 식별자를 읽는다")
    fun resolvesWithQueryParameter() {
        val target = resolver.resolve("nevera://detail/101?from=push")

        assertEquals(listOf(EditFridgeIngredientRoute(101L)), target?.stack)
    }

    @ParameterizedTest(name = "무효 입력: {0}")
    @ValueSource(
        strings = [
            "nevera://",
            "nevera://detail",
            "nevera://detail/",
            "nevera://detail/abc",
            "nevera://unknown/101",
            "https://nevera.app/detail/101",
            "not a uri at all",
            "",
        ],
    )
    fun returnsNullForInvalidInput(deeplink: String) {
        assertNull(resolver.resolve(deeplink))
    }
}
