package com.anddd.nevera.quality.designsystem.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Material3AppBarRuleTest {

    private val rule = Material3AppBarRule(Config.empty)

    @Test
    fun `NeveraAppBar 사용은 위반 없음`() {
        val code = """
            Scaffold(
                topBar = { NeveraAppBar(title = "제목") }
            ) {}
        """.trimIndent()
        assertThat(rule.lint(code)).isEmpty()
    }

    @Test
    fun `TopAppBar 사용은 위반`() {
        val code = """
            Scaffold(
                topBar = { TopAppBar(title = { Text("제목") }) }
            ) {}
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `CenterAlignedTopAppBar 사용은 위반`() {
        val code = """
            Scaffold(
                topBar = { CenterAlignedTopAppBar(title = { Text("제목") }) }
            ) {}
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `SmallTopAppBar 사용은 위반`() {
        val code = """
            Scaffold(
                topBar = { SmallTopAppBar(title = { Text("제목") }) }
            ) {}
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `MediumTopAppBar 사용은 위반`() {
        val code = """
            Scaffold(
                topBar = { MediumTopAppBar(title = { Text("제목") }) }
            ) {}
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `LargeTopAppBar 사용은 위반`() {
        val code = """
            Scaffold(
                topBar = { LargeTopAppBar(title = { Text("제목") }) }
            ) {}
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }
}
