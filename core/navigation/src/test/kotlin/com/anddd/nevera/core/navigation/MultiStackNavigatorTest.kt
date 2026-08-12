package com.anddd.nevera.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

private data object HomeRoot : NavKey
private data object FridgeRoot : NavKey
private data object MyPageRoot : NavKey
private data object NotificationScreen : NavKey
private data class CaptureScreen(val openGallery: Boolean = false) : NavKey
private data class ResultScreen(val imageUri: String) : NavKey

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
    @DisplayName("루트가 아닌 목적지는 현재 스택에 쌓인다")
    fun navigateToNonRoot() {
        navigator.navigate(NotificationScreen)

        assertEquals(HomeRoot, state.currentRootKey)
        assertEquals(listOf(HomeRoot, NotificationScreen), state.currentStack.toList())
    }

    @Test
    @DisplayName("다른 루트로 이동하면 스택이 바뀌고 떠나온 스택은 보존된다")
    fun navigateToOtherRootPreservesStack() {
        navigator.navigate(NotificationScreen)
        navigator.navigate(FridgeRoot)

        assertEquals(FridgeRoot, state.currentRootKey)
        // 떠나온 스택은 손대지 않으므로 떠날 때의 화면이 그대로 남는다
        assertEquals(listOf(HomeRoot, NotificationScreen), state.stacksByRoot[HomeRoot]?.toList())
    }

    @Test
    @DisplayName("현재 루트를 다시 선택하면 그 스택이 루트만 남기고 비워진다")
    fun reselectingCurrentRootClearsStack() {
        navigator.navigate(NotificationScreen)
        navigator.navigate(HomeRoot)

        assertEquals(listOf(HomeRoot), state.currentStack.toList())
    }

    @Test
    @DisplayName("같은 목적지로 연달아 이동해도 중복 생성되지 않는다")
    fun navigateSameKeyTwiceDoesNotDuplicate() {
        navigator.navigate(NotificationScreen)
        navigator.navigate(NotificationScreen)

        assertEquals(listOf(HomeRoot, NotificationScreen), state.currentStack.toList())
    }

    @Test
    @DisplayName("시작 루트로 돌아가면 루트 방문 이력이 비워진다")
    fun returningToStartRootClearsHistory() {
        navigator.navigate(FridgeRoot)
        navigator.navigate(MyPageRoot)
        navigator.navigate(HomeRoot)

        assertEquals(listOf(HomeRoot), state.rootHistory.toList())
    }

    @Test
    @DisplayName("뒤로가기는 현재 스택의 화면을 먼저 걷어낸다")
    fun goBackPopsCurrentStackFirst() {
        navigator.navigate(NotificationScreen)
        navigator.goBack()

        assertEquals(listOf(HomeRoot), state.currentStack.toList())
        assertEquals(HomeRoot, state.currentRootKey)
    }

    @Test
    @DisplayName("루트에서 뒤로가면 이전에 있던 루트로 돌아간다")
    fun goBackFromRootReturnsToPreviousRoot() {
        navigator.navigate(FridgeRoot)
        navigator.goBack()

        assertEquals(HomeRoot, state.currentRootKey)
    }

    @Test
    @DisplayName("시작 화면에서 뒤로가면 아무 일도 일어나지 않는다")
    fun goBackAtStartIsNoOp() {
        navigator.goBack()

        assertEquals(listOf(HomeRoot), state.rootHistory.toList())
        assertEquals(HomeRoot, state.currentKey)
    }

    @Test
    @DisplayName("replaceStep은 인자가 달라도 같은 타입의 단계를 제거한다")
    fun replaceStepRemovesSameTypeRegardlessOfArguments() {
        navigator.navigate(CaptureScreen(openGallery = true))
        navigator.replaceStep<CaptureScreen>(ResultScreen("content://photo"))

        // 촬영 단계가 사라져 뒤로가기로 돌아갈 수 없다
        assertEquals(listOf(HomeRoot, ResultScreen("content://photo")), state.currentStack.toList())
    }

    @Test
    @DisplayName("replaceStep 이후 뒤로가면 흐름 진입 이전으로 나간다")
    fun goBackAfterReplaceStepExitsFlow() {
        navigator.navigate(CaptureScreen())
        navigator.replaceStep<CaptureScreen>(ResultScreen("content://photo"))
        navigator.goBack()

        assertEquals(listOf(HomeRoot), state.currentStack.toList())
    }
}
