package com.anddd.nevera.quality.screencontent.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ToastOutsideScreenRuleTest {

    private val rule = ToastOutsideScreenRule(Config.empty)

    @Test
    fun `Screen 함수의 collectSideEffect 안 Toast는 허용`() {
        val code = """
            package com.anddd.nevera.feature.home

            @Composable
            fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
                val context = LocalContext.current
                viewModel.collectSideEffect { effect ->
                    when (effect) {
                        is HomeSideEffect.ShowError ->
                            Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        """.trimIndent()
        assertThat(rule.lint(code)).isEmpty()
    }

    @Test
    fun `Content 함수 본문의 Toast는 위반`() {
        val code = """
            package com.anddd.nevera.feature.home.component

            @Composable
            internal fun HomeContent(uiState: HomeUiState) {
                val context = LocalContext.current
                if (uiState.errorMessage != null) {
                    Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `Content의 클릭 핸들러 안 Toast도 위반`() {
        val code = """
            package com.anddd.nevera.feature.home.component

            @Composable
            internal fun WishBanner(onCreateWish: () -> Unit) {
                val context = LocalContext.current
                Button(
                    onClick = {
                        Toast.makeText(context, "생성", Toast.LENGTH_SHORT).show()
                    },
                )
            }
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `Screen 함수 밖 private 헬퍼의 Toast는 위반`() {
        val code = """
            package com.anddd.nevera.feature.auth.signup

            private fun showToast(context: Context, message: String) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `FQCN 호출로도 우회 불가 - android_widget_Toast_makeText도 위반`() {
        val code = """
            package com.anddd.nevera.feature.home.component

            @Composable
            internal fun HomeContent(uiState: HomeUiState) {
                val context = LocalContext.current
                if (uiState.errorMessage != null) {
                    android.widget.Toast.makeText(context, uiState.errorMessage, android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `ViewModel 안의 Toast도 위반 - SideEffect로 처리해야 함`() {
        val code = """
            package com.anddd.nevera.feature.home

            class HomeViewModel(private val context: Context) {
                fun onError(message: String) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `feature 패키지가 아니면 검사하지 않음`() {
        val code = """
            package com.anddd.nevera.core.ui

            fun showDebugToast(context: Context, message: String) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        """.trimIndent()
        assertThat(rule.lint(code)).isEmpty()
    }
}
