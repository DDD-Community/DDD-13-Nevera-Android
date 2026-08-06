package com.anddd.nevera.quality.mvi.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NeveraViewModelInheritanceRuleTest {

    private val rule = NeveraViewModelInheritanceRule(Config.empty)

    @Test
    fun `NeveraViewModel을 상속하면 위반 없음`() {
        val code = """
            package com.anddd.nevera.feature.main
            class HomeViewModel : NeveraViewModel<HomeUiState, HomeSideEffect, HomeIntent, HomeMutation>(HomeUiState())
        """.trimIndent()
        assertThat(rule.lint(code)).isEmpty()
    }

    @Test
    fun `ViewModel을 직접 상속하면 위반`() {
        val code = """
            package com.anddd.nevera.feature.main
            class HomeViewModel : ViewModel()
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `feature 패키지 외부의 ViewModel은 검사하지 않음`() {
        val code = """
            package com.anddd.nevera.core.mvi
            abstract class NeveraViewModel<STATE, SIDE_EFFECT, INTENT, MUTATION> : ViewModel()
        """.trimIndent()
        assertThat(rule.lint(code)).isEmpty()
    }
}
