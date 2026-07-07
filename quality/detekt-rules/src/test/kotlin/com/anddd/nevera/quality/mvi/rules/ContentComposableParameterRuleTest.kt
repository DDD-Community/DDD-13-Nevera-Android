package com.anddd.nevera.quality.mvi.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ContentComposableParameterRuleTest {

    private val rule = ContentComposableParameterRule(Config.empty)

    @Test
    fun `UiState와 함수 타입 파라미터만 있으면 위반 없음`() {
        val code = """
            @Composable
            fun HomeContent(
                uiState: HomeUiState,
                onIntent: (HomeIntent) -> Unit,
            ) {}
        """.trimIndent()
        assertThat(rule.lint(code)).isEmpty()
    }

    @Test
    fun `Modifier 파라미터는 허용`() {
        val code = """
            @Composable
            fun HomeContent(
                uiState: HomeUiState,
                onIntent: (HomeIntent) -> Unit,
                modifier: Modifier = Modifier,
            ) {}
        """.trimIndent()
        assertThat(rule.lint(code)).isEmpty()
    }

    @Test
    fun `Boolean 파라미터는 위반`() {
        val code = """
            @Composable
            fun HomeContent(
                uiState: HomeUiState,
                showBottomSheet: Boolean,
                onIntent: (HomeIntent) -> Unit,
            ) {}
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `Composable 어노테이션 없는 Content 함수는 검사하지 않음`() {
        val code = """
            fun HomeContent(
                uiState: HomeUiState,
                someFlag: Boolean,
            ) {}
        """.trimIndent()
        assertThat(rule.lint(code)).isEmpty()
    }
}
