package com.anddd.nevera.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * 스택 하나짜리 흐름의 이동 통로. 갈래가 없는 선형 흐름에 쓴다.
 *
 * 루트를 여러 개 두지 않으므로 [MultiStackNavigator]가 가진 루트 전환·루트 재선택 개념이
 * 없다. 스택의 첫 원소가 곧 루트이고, 뒤로가기는 거기서 멈춘다.
 */
class SingleStackNavigator(private val backStack: NavBackStack<NavKey>) : Navigator() {

    override val currentStack: NavBackStack<NavKey> get() = backStack

    /** 화면을 쌓는다. 이미 스택에 있으면 중복 없이 맨 뒤로 옮긴다. */
    override fun navigate(key: NavKey) {
        backStack.apply {
            remove(key)
            add(key)
        }
    }

    /**
     * 이전 화면으로 돌아간다. 루트에서는 아무 일도 하지 않는다.
     *
     * 스택을 비우면 NavDisplay가 `require(backStack.isNotEmpty())`에서 죽으므로,
     * 마지막 하나는 남긴다. 루트에서의 뒤로가기는 시스템이 앱 종료로 처리한다.
     */
    override fun goBack() {
        if (backStack.size > 1) backStack.removeLastOrNull()
    }
}
