package com.anddd.nevera.quality.screencontent.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ViewModelAccessOnlyInScreenRuleTest {

    private val rule = ViewModelAccessOnlyInScreenRule(Config.empty)

    @Test
    fun `Screen 함수 안의 hiltViewModel과 collect 계열 호출은 허용`() {
        val code = """
            package com.anddd.nevera.feature.home

            @Composable
            fun HomeScreen(
                onNavigateBack: () -> Unit,
                viewModel: HomeViewModel = hiltViewModel(),
            ) {
                val uiState = viewModel.collectAsState().value
                viewModel.collectSideEffect { effect -> }
                HomeContent(uiState = uiState, onIntent = viewModel::handleIntent)
            }
        """.trimIndent()
        assertThat(rule.lint(code)).isEmpty()
    }

    @Test
    fun `Content 함수 안의 hiltViewModel 호출은 위반`() {
        val code = """
            package com.anddd.nevera.feature.home.component

            @Composable
            internal fun HomeContent(
                uiState: HomeUiState,
                viewModel: HomeViewModel = hiltViewModel(),
            ) {}
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `Content 함수 안의 collectAsState 호출은 위반`() {
        val code = """
            package com.anddd.nevera.feature.home.component

            @Composable
            internal fun HomeContent(viewModel: HomeViewModel) {
                val uiState = viewModel.collectAsState().value
            }
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `Component 함수 안의 collectSideEffect 호출은 위반`() {
        val code = """
            package com.anddd.nevera.feature.home.component

            @Composable
            internal fun WishBanner(viewModel: HomeViewModel) {
                viewModel.collectSideEffect { effect -> }
            }
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `Preview 함수 안의 collectAsLazyPagingItems 호출은 허용`() {
        val code = """
            package com.anddd.nevera.feature.notification

            @Preview(name = "빈 상태")
            @Composable
            private fun NotificationContentPreview() {
                val pagingItems = flowOf(PagingData.empty<NotificationItemUiModel>())
                    .collectAsLazyPagingItems()
                NotificationContent(pagingItems = pagingItems)
            }
        """.trimIndent()
        assertThat(rule.lint(code)).isEmpty()
    }

    @Test
    fun `Screen 밖 일반 컴포저블의 collectAsLazyPagingItems 호출은 위반`() {
        val code = """
            package com.anddd.nevera.feature.notification.component

            @Composable
            internal fun NotificationContent(viewModel: NotificationViewModel) {
                val pagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()
            }
        """.trimIndent()
        assertThat(rule.lint(code)).hasSize(1)
    }

    @Test
    fun `feature 패키지가 아니면 검사하지 않음`() {
        val code = """
            package com.anddd.nevera.core.ui

            @Composable
            fun CoreComponent(viewModel: SomeViewModel = hiltViewModel()) {}
        """.trimIndent()
        assertThat(rule.lint(code)).isEmpty()
    }
}
