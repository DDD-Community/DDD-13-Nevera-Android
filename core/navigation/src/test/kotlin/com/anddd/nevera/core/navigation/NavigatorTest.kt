package com.anddd.nevera.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

private data object HomeTab : NavKey
private data object FridgeTab : NavKey
private data object MyPageTab : NavKey
private data object NotificationScreen : NavKey
private data class CaptureScreen(val openGallery: Boolean = false) : NavKey
private data class ResultScreen(val imageUri: String) : NavKey

class NavigatorTest {

    private lateinit var state: NavigationState
    private lateinit var navigator: Navigator

    @BeforeEach
    fun setUp() {
        val tabs = listOf(HomeTab, FridgeTab, MyPageTab)
        state = NavigationState(
            startKey = HomeTab,
            topLevelStack = NavBackStack(HomeTab),
            subStacks = tabs.associateWith { NavBackStack(it) },
        )
        navigator = Navigator(state)
    }

    @Test
    @DisplayName("탭이 아닌 목적지는 현재 탭의 스택에 쌓인다")
    fun navigateToNonTopLevel() {
        navigator.navigate(NotificationScreen)

        assertEquals(HomeTab, state.currentTopLevelKey)
        assertEquals(listOf(HomeTab, NotificationScreen), state.currentSubStack.toList())
    }

    @Test
    @DisplayName("다른 탭으로 이동하면 탭이 전환되고 이전 탭의 스택은 보존된다")
    fun navigateToOtherTabPreservesStack() {
        navigator.navigate(NotificationScreen)
        navigator.navigate(FridgeTab)

        assertEquals(FridgeTab, state.currentTopLevelKey)
        // 탭을 옮겨도 떠날 때의 화면이 그대로 남는다
        assertEquals(listOf(HomeTab, NotificationScreen), state.subStacks[HomeTab]?.toList())
    }

    @Test
    @DisplayName("현재 탭을 다시 선택하면 그 탭의 루트로 돌아간다")
    fun reselectingCurrentTabClearsSubStack() {
        navigator.navigate(NotificationScreen)
        navigator.navigate(HomeTab)

        assertEquals(listOf(HomeTab), state.currentSubStack.toList())
    }

    @Test
    @DisplayName("같은 목적지로 연달아 이동해도 중복 생성되지 않는다")
    fun navigateSameKeyTwiceDoesNotDuplicate() {
        navigator.navigate(NotificationScreen)
        navigator.navigate(NotificationScreen)

        assertEquals(listOf(HomeTab, NotificationScreen), state.currentSubStack.toList())
    }

    @Test
    @DisplayName("시작 탭으로 돌아가면 탭 방문 이력이 비워진다")
    fun returningToStartTabClearsTopLevelHistory() {
        navigator.navigate(FridgeTab)
        navigator.navigate(MyPageTab)
        navigator.navigate(HomeTab)

        assertEquals(listOf(HomeTab), state.topLevelStack.toList())
    }

    @Test
    @DisplayName("뒤로가기는 현재 탭의 화면을 먼저 걷어낸다")
    fun goBackPopsSubStackFirst() {
        navigator.navigate(NotificationScreen)
        navigator.goBack()

        assertEquals(listOf(HomeTab), state.currentSubStack.toList())
        assertEquals(HomeTab, state.currentTopLevelKey)
    }

    @Test
    @DisplayName("탭 루트에서 뒤로가면 이전 탭으로 돌아간다")
    fun goBackFromTabRootReturnsToPreviousTab() {
        navigator.navigate(FridgeTab)
        navigator.goBack()

        assertEquals(HomeTab, state.currentTopLevelKey)
    }

    @Test
    @DisplayName("시작 화면에서 뒤로가면 아무 일도 일어나지 않는다")
    fun goBackAtStartIsNoOp() {
        navigator.goBack()

        assertEquals(listOf(HomeTab), state.topLevelStack.toList())
        assertEquals(HomeTab, state.currentKey)
    }

    @Test
    @DisplayName("replaceStep은 인자가 달라도 같은 타입의 단계를 제거한다")
    fun replaceStepRemovesSameTypeRegardlessOfArguments() {
        navigator.navigate(CaptureScreen(openGallery = true))
        navigator.replaceStep<CaptureScreen>(ResultScreen("content://photo"))

        // 촬영 단계가 사라져 뒤로가기로 돌아갈 수 없다
        assertEquals(listOf(HomeTab, ResultScreen("content://photo")), state.currentSubStack.toList())
    }

    @Test
    @DisplayName("replaceStep 이후 뒤로가면 흐름 진입 이전으로 나간다")
    fun goBackAfterReplaceStepExitsFlow() {
        navigator.navigate(CaptureScreen())
        navigator.replaceStep<CaptureScreen>(ResultScreen("content://photo"))
        navigator.goBack()

        assertEquals(listOf(HomeTab), state.currentSubStack.toList())
    }
}
