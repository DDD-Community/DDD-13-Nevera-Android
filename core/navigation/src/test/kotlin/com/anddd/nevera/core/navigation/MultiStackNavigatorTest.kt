package com.anddd.nevera.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

private data object HomeRoot : NavKey
private data object FridgeRoot : NavKey
private data object MyPageRoot : NavKey
private data object NotificationScreen : NavKey
private data class CaptureScreen(val openGallery: Boolean = false) : NavKey
private data class ResultScreen(val imageUri: String) : NavKey
private data class SuccessScreen(val totalCost: Int) : NavKey

class MultiStackNavigatorTest {

    private lateinit var state: NavigationState
    private lateinit var navigator: MultiStackNavigator

    @BeforeEach
    fun setUp() {
        val roots = listOf(HomeRoot, FridgeRoot, MyPageRoot)
        state = NavigationState(
            startRootKey = HomeRoot,
            rootHistory = NavBackStack(HomeRoot),
            stacksByRoot = roots.associateWith { NavBackStack(it) },
        )
        navigator = MultiStackNavigator(state)
    }

    @Test
    fun `루트가 아닌 목적지는 현재 스택에 쌓인다`() {
        navigator.navigate(NotificationScreen)

        assertEquals(HomeRoot, state.currentRootKey)
        assertEquals(listOf(HomeRoot, NotificationScreen), state.currentStack.toList())
    }

    @Test
    fun `다른 루트로 이동하면 스택이 바뀌고 떠나온 스택은 보존된다`() {
        navigator.navigate(NotificationScreen)
        navigator.navigate(FridgeRoot)

        assertEquals(FridgeRoot, state.currentRootKey)
        // 떠나온 스택은 손대지 않으므로 떠날 때의 화면이 그대로 남는다
        assertEquals(listOf(HomeRoot, NotificationScreen), state.stacksByRoot[HomeRoot]?.toList())
    }

    @Test
    fun `현재 루트를 다시 선택하면 그 스택이 루트만 남기고 비워진다`() {
        navigator.navigate(NotificationScreen)
        navigator.navigate(HomeRoot)

        assertEquals(listOf(HomeRoot), state.currentStack.toList())
    }

    @Test
    fun `같은 목적지로 연달아 이동해도 중복 생성되지 않는다`() {
        navigator.navigate(NotificationScreen)
        navigator.navigate(NotificationScreen)

        assertEquals(listOf(HomeRoot, NotificationScreen), state.currentStack.toList())
    }

    @Test
    fun `시작 루트로 돌아가면 루트 방문 이력이 비워진다`() {
        navigator.navigate(FridgeRoot)
        navigator.navigate(MyPageRoot)
        navigator.navigate(HomeRoot)

        assertEquals(listOf(HomeRoot), state.rootHistory.toList())
    }

    @Test
    fun `뒤로가기는 현재 스택의 화면을 먼저 걷어낸다`() {
        navigator.navigate(NotificationScreen)
        navigator.goBack()

        assertEquals(listOf(HomeRoot), state.currentStack.toList())
        assertEquals(HomeRoot, state.currentRootKey)
    }

    @Test
    fun `루트에서 뒤로가면 이전에 있던 루트로 돌아간다`() {
        navigator.navigate(FridgeRoot)
        navigator.goBack()

        assertEquals(HomeRoot, state.currentRootKey)
    }

    @Test
    fun `시작 화면에서 뒤로가면 아무 일도 일어나지 않는다`() {
        navigator.goBack()

        assertEquals(listOf(HomeRoot), state.rootHistory.toList())
        assertEquals(HomeRoot, state.currentKey)
    }

    @Test
    fun `replace는 인자가 달라도 같은 타입을 모두 제거한다`() {
        navigator.navigate(CaptureScreen(openGallery = true))
        navigator.replace<CaptureScreen>(ResultScreen("content://photo"))

        // 인자가 다른 CaptureScreen이 남지 않아 뒤로가기로 돌아갈 수 없다
        assertEquals(listOf(HomeRoot, ResultScreen("content://photo")), state.currentStack.toList())
    }

    @Test
    fun `replace 이후 뒤로가면 흐름 진입 이전으로 나간다`() {
        navigator.navigate(CaptureScreen())
        navigator.replace<CaptureScreen>(ResultScreen("content://photo"))
        navigator.goBack()

        assertEquals(listOf(HomeRoot), state.currentStack.toList())
    }

    @Test
    fun `시작 루트가 아닌 스택에서 연 흐름을 벗어나도 그 스택에 화면이 남지 않는다`() {
        // 냉장고 탭에서 촬영 → 인식 결과 → 등록 완료까지 진행한다
        navigator.navigate(FridgeRoot)
        navigator.navigate(CaptureScreen())
        navigator.replace<CaptureScreen>(ResultScreen("content://photo"))
        navigator.replace<ResultScreen>(SuccessScreen(totalCost = 12_000))

        // 닫기 → 흐름을 벗어난다
        exitIngredientFlow()

        // 흐름을 벗어난 뒤 냉장고 스택에는 루트만 남아야 한다
        assertEquals(listOf(FridgeRoot), state.stacksByRoot[FridgeRoot]?.toList())
    }

    @Test
    fun `흐름을 벗어난 뒤 그 탭을 다시 선택해도 완료 화면으로 돌아가지 않는다`() {
        navigator.navigate(FridgeRoot)
        navigator.navigate(CaptureScreen())
        navigator.replace<CaptureScreen>(ResultScreen("content://photo"))
        navigator.replace<ResultScreen>(SuccessScreen(totalCost = 12_000))
        exitIngredientFlow()

        // 나중에 냉장고 탭을 다시 누른다
        navigator.navigate(FridgeRoot)

        assertEquals(FridgeRoot, state.currentKey)
    }

    @Test
    fun `시작 루트에서 연 흐름을 벗어나면 그 스택이 루트만 남기고 비워진다`() {
        navigator.navigate(CaptureScreen())
        navigator.replace<CaptureScreen>(ResultScreen("content://photo"))
        navigator.replace<ResultScreen>(SuccessScreen(totalCost = 12_000))

        exitIngredientFlow()

        assertEquals(listOf(HomeRoot), state.stacksByRoot[HomeRoot]?.toList())
        assertEquals(HomeRoot, state.currentKey)
    }

    /** :app의 ingredientEntry(onExitFlow = ...) 배선을 그대로 흉내낸다. */
    private fun exitIngredientFlow() {
        navigator.replaceStack(emptyList())
    }
}
