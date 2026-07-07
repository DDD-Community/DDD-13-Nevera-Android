package com.anddd.nevera.quality.mvi.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ReduceOutsideApplyMutationRuleTest {

    private val rule = ReduceOutsideApplyMutationRule(Config.empty)

    @Test
    fun `applyMutation 안에서 reduce 호출은 위반 없음`() {
        val code = """
            suspend fun applyMutation(mutation: HomeMutation) {
                when (mutation) {
                    HomeMutation.Loading -> reduce { state.copy(isLoading = true) }
                }
            }
        """.trimIndent()
        assertThat(rule.lint(code)).isEmpty()
    }

    @Test
    fun `applyMutation 밖에서 reduce 호출은 위반`() {
        val code = """
            private fun onRefreshClicked() = intent {
                reduce { state.copy(isLoading = true) }
            }
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }
}
