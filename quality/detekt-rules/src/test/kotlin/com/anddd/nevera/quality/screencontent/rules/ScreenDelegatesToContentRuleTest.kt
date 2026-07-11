package com.anddd.nevera.quality.screencontent.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ScreenDelegatesToContentRuleTest {

    private val rule = ScreenDelegatesToContentRule(Config.empty)

    @Test
    fun `Screen이 같은 접두사의 Content를 호출하면 위반 없음`() {
        val code = """
            package com.anddd.nevera.feature.home

            @Composable
            fun HomeScreen(
                onNavigateBack: () -> Unit,
                viewModel: HomeViewModel = hiltViewModel(),
            ) {
                val uiState = viewModel.collectAsState().value
                HomeContent(
                    uiState = uiState,
                    onIntent = viewModel::handleIntent,
                )
            }
        """.trimIndent()
        assertThat(rule.lint(code)).isEmpty()
    }

    @Test
    fun `Screen이 조건 분기 안에서 Content를 호출해도 위반 없음`() {
        val code = """
            package com.anddd.nevera.feature.ingredient

            @Composable
            fun IngredientScreen(viewModel: IngredientViewModel = hiltViewModel()) {
                val uiState = viewModel.collectAsState().value
                if (uiState.isReady) {
                    IngredientContent(uiState = uiState, onIntent = viewModel::handleIntent)
                }
            }
        """.trimIndent()
        assertThat(rule.lint(code)).isEmpty()
    }

    @Test
    fun `Screen이 Content를 호출하지 않으면 위반`() {
        val code = """
            package com.anddd.nevera.feature.photodetail

            @Composable
            fun PhotoDetailScreen(imageUri: String, onBack: () -> Unit) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(model = imageUri)
                }
            }
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `다른 접두사의 Content 호출은 위반 - LoadingContent 같은 공용 컴포넌트로 우회 불가`() {
        val code = """
            package com.anddd.nevera.feature.notification

            @Composable
            fun NotificationScreen(viewModel: NotificationViewModel = hiltViewModel()) {
                NotificationList(uiState = viewModel.collectAsState().value)
                LoadingContent()
            }
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `Composable 어노테이션 없는 Screen 함수는 검사하지 않음`() {
        val code = """
            package com.anddd.nevera.feature.home

            fun buildHomeScreen(): String = "home"

            fun HomeScreen(config: Config) {}
        """.trimIndent()
        assertThat(rule.lint(code)).isEmpty()
    }

    @Test
    fun `feature 패키지가 아니면 검사하지 않음`() {
        val code = """
            package com.anddd.nevera.core.ui.sample

            @Composable
            fun SampleScreen() {
                Box {}
            }
        """.trimIndent()
        assertThat(rule.lint(code)).isEmpty()
    }
}
