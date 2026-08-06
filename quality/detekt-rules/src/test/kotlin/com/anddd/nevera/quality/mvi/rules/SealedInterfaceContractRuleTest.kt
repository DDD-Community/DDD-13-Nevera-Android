package com.anddd.nevera.quality.mvi.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SealedInterfaceContractRuleTest {

    private val rule = SealedInterfaceContractRule(Config.empty)

    @Test
    fun `sealed interface Intent는 위반 없음`() {
        assertThat(rule.lint("sealed interface HomeIntent : NeveraIntent")).isEmpty()
    }

    @Test
    fun `sealed class Intent는 위반`() {
        assertThat(rule.lint("sealed class HomeIntent : NeveraIntent()")).hasSize(1)
    }

    @Test
    fun `일반 interface Intent는 위반`() {
        assertThat(rule.lint("interface HomeIntent : NeveraIntent")).hasSize(1)
    }

    @Test
    fun `sealed interface Mutation은 위반 없음`() {
        assertThat(rule.lint("sealed interface HomeMutation : NeveraMutation")).isEmpty()
    }

    @Test
    fun `sealed interface SideEffect는 위반 없음`() {
        assertThat(rule.lint("sealed interface HomeSideEffect : NeveraSideEffect")).isEmpty()
    }
}
