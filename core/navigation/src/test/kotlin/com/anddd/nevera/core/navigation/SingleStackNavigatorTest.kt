package com.anddd.nevera.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

private data object SplashScreen : NavKey
private data object LoginScreen : NavKey
private data object SignupScreen : NavKey
private data object ResetPasswordScreen : NavKey

class SingleStackNavigatorTest {

    private lateinit var backStack: NavBackStack<NavKey>
    private lateinit var navigator: SingleStackNavigator

    @BeforeEach
    fun setUp() {
        backStack = NavBackStack(SplashScreen)
        navigator = SingleStackNavigator(backStack)
    }

    @Test
    @DisplayName("목적지는 현재 스택에 쌓인다")
    fun navigatePushesOntoStack() {
        navigator.navigate(LoginScreen)

        assertEquals(listOf(SplashScreen, LoginScreen), backStack.toList())
    }

    @Test
    @DisplayName("같은 목적지로 연달아 이동해도 중복 생성되지 않는다")
    fun navigateSameKeyTwiceDoesNotDuplicate() {
        navigator.navigate(LoginScreen)
        navigator.navigate(LoginScreen)

        assertEquals(listOf(SplashScreen, LoginScreen), backStack.toList())
    }

    @Test
    @DisplayName("이미 스택에 있는 목적지로 이동하면 맨 뒤로 옮겨진다")
    fun navigateToExistingKeyMovesItToTop() {
        navigator.navigate(LoginScreen)
        navigator.navigate(SignupScreen)
        navigator.navigate(LoginScreen)

        assertEquals(listOf(SplashScreen, SignupScreen, LoginScreen), backStack.toList())
    }

    @Test
    @DisplayName("뒤로가기는 마지막 화면을 걷어낸다")
    fun goBackPopsLastScreen() {
        navigator.navigate(LoginScreen)
        navigator.goBack()

        assertEquals(listOf(SplashScreen), backStack.toList())
    }

    @Test
    @DisplayName("루트에서 뒤로가면 아무 일도 일어나지 않는다")
    fun goBackAtRootIsNoOp() {
        navigator.goBack()

        // 스택을 비우면 NavDisplay가 죽으므로 마지막 하나는 남는다
        assertEquals(listOf(SplashScreen), backStack.toList())
    }

    @Test
    @DisplayName("replaceStep은 그 단계를 지우고 새 화면을 얹는다")
    fun replaceStepRemovesReplacedStep() {
        navigator.navigate(LoginScreen)
        navigator.replaceStep<LoginScreen>(SignupScreen)

        assertEquals(listOf(SplashScreen, SignupScreen), backStack.toList())
    }

    @Test
    @DisplayName("스플래시를 로그인으로 교체하면 뒤로가기로 스플래시에 돌아갈 수 없다")
    fun replaceStepOnRootLeavesLoginAsRoot() {
        navigator.replaceStep<SplashScreen>(LoginScreen)
        navigator.goBack()

        assertEquals(listOf(LoginScreen), backStack.toList())
    }

    @Test
    @DisplayName("replaceStack은 루트만 남기고 그 위를 통째로 바꾼다")
    fun replaceStackKeepsRootOnly() {
        navigator.navigate(LoginScreen)
        navigator.navigate(SignupScreen)

        navigator.replaceStack(listOf(ResetPasswordScreen))

        assertEquals(listOf(SplashScreen, ResetPasswordScreen), backStack.toList())
    }

    @Test
    @DisplayName("replaceStack 이후 뒤로가면 루트로 돌아간다")
    fun goBackAfterReplaceStackReturnsToRoot() {
        navigator.replaceStack(listOf(LoginScreen, SignupScreen))
        navigator.goBack()
        navigator.goBack()

        assertEquals(listOf(SplashScreen), backStack.toList())
    }

    @Test
    @DisplayName("빈 목록으로 replaceStack하면 루트만 남는다")
    fun replaceStackWithEmptyListLeavesRoot() {
        navigator.navigate(LoginScreen)

        navigator.replaceStack(emptyList())

        assertEquals(listOf(SplashScreen), backStack.toList())
    }
}
