package com.anddd.nevera.quality.screencontent.rules

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ScreenNoScaffoldRuleTest {

    private val rule = ScreenNoScaffoldRule(Config.empty)

    @Test
    fun `Screen 파일에서 Scaffold를 직접 호출하면 위반`() {
        val code = """
            package com.anddd.nevera.feature.home

            @Composable
            fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
                Scaffold(
                    topBar = { NeveraAppBar(title = "홈") },
                ) { padding ->
                    HomeContent(uiState = viewModel.collectAsState().value)
                }
            }
        """.trimIndent()
        val ktFile = compileContentForTest(code, "HomeScreen.kt")
        assertThat(rule.lint(ktFile)).hasSize(1)
    }

    @Test
    fun `Screen 파일의 private 하위 컴포저블에 숨긴 Scaffold도 위반`() {
        val code = """
            package com.anddd.nevera.feature.notification

            @Composable
            fun NotificationScreen(viewModel: NotificationViewModel = hiltViewModel()) {
                NotificationList(uiState = viewModel.collectAsState().value)
            }

            @Composable
            private fun NotificationList(uiState: NotificationUiState) {
                Scaffold(topBar = { NeveraAppBar(title = "알림") }) { }
            }
        """.trimIndent()
        val ktFile = compileContentForTest(code, "NotificationScreen.kt")
        assertThat(rule.lint(ktFile)).hasSize(1)
    }

    @Test
    fun `Content 파일의 Scaffold는 위반 아님`() {
        val code = """
            package com.anddd.nevera.feature.home.component

            @Composable
            internal fun HomeContent(
                uiState: HomeUiState,
                onIntent: (HomeIntent) -> Unit,
            ) {
                Scaffold(topBar = { NeveraLogoAppBar() }) { }
            }
        """.trimIndent()
        val ktFile = compileContentForTest(code, "HomeContent.kt")
        assertThat(rule.lint(ktFile)).isEmpty()
    }

    @Test
    fun `feature 패키지가 아닌 Screen 파일은 검사하지 않음`() {
        val code = """
            package com.anddd.nevera.core.ui.sample

            @Composable
            fun SampleScreen() {
                Scaffold { }
            }
        """.trimIndent()
        val ktFile = compileContentForTest(code, "SampleScreen.kt")
        assertThat(rule.lint(ktFile)).isEmpty()
    }
}
