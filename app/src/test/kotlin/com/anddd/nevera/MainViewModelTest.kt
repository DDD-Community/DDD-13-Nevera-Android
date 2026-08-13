package com.anddd.nevera

import com.anddd.nevera.navigation.DeeplinkResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * MainViewModel은 딥링크를 기억하지 않는다. 넘어온 횟수만큼 목적지를 발행한다.
 *
 * 이 성질 때문에 "언제 dispatchDeeplink를 부를 것인가"는 전적으로 호출부(MainActivity)의
 * 책임이다. Activity가 다시 만들어질 때 같은 intent로 다시 부르면 화면도 다시 열린다.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = MainViewModel(DeeplinkResolver())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `같은 딥링크를 두 번 넘기면 목적지도 두 번 발행된다`() = runTest {
        viewModel.dispatchDeeplink(DEEPLINK)
        viewModel.dispatchDeeplink(DEEPLINK)

        val targets = viewModel.deeplinkTargets.take(2).toList()

        assertEquals(2, targets.size)
        assertEquals(targets[0], targets[1])
    }

    @Test
    fun `해석할 수 없는 딥링크는 목적지를 발행하지 않는다`() = runTest {
        viewModel.dispatchDeeplink("nevera://unknown/101")
        viewModel.dispatchDeeplink(DEEPLINK)

        val targets = viewModel.deeplinkTargets.take(1).toList()

        assertEquals(DeeplinkResolver().resolve(DEEPLINK), targets.single())
    }

    private companion object {
        const val DEEPLINK = "nevera://detail/101"
    }
}
