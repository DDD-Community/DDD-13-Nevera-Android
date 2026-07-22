package com.anddd.nevera.domain.usecase.deeplink

import com.anddd.nevera.domain.model.deeplink.DeeplinkAction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ResolveDeeplinkUseCaseTest {

    private val useCase = ResolveDeeplinkUseCase()

    @Test
    fun `상세 딥링크는 접두사 뒤 문자열을 재료 식별자로 사용한다`() {
        val result = useCase("nevera://detail/123")

        assertEquals(DeeplinkAction.NavigateToIngredientDetail("123"), result)
    }

    @Test
    fun `식별자에 숫자가 아닌 문자가 있어도 그대로 전달한다`() {
        val result = useCase("nevera://detail/abc-987")

        assertEquals(DeeplinkAction.NavigateToIngredientDetail("abc-987"), result)
    }

    @Test
    fun `접두사만 있고 식별자가 없으면 null을 반환한다`() {
        val result = useCase("nevera://detail/")

        assertNull(result)
    }

    @Test
    fun `식별자가 공백뿐이면 null을 반환한다`() {
        val result = useCase("nevera://detail/   ")

        assertNull(result)
    }

    @Test
    fun `알 수 없는 접두사는 null을 반환한다`() {
        val result = useCase("nevera://unknown/123")

        assertNull(result)
    }

    @Test
    fun `빈 문자열은 null을 반환한다`() {
        val result = useCase("")

        assertNull(result)
    }

    @Test
    fun `접두사가 문자열 중간에 있으면 null을 반환한다`() {
        val result = useCase("https://example.com/nevera://detail/123")

        assertNull(result)
    }
}
