package com.anddd.nevera.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * 루트마다 스택 하나를 두는 흐름의 이동 통로. 앱 본문에 쓴다.
 *
 * 루트 전환 정책이 [navigate] 안에 있어서 조립 지점이 규칙을 따로 들고 있을 필요가 없다.
 */
class MultiStackNavigator(private val state: NavigationState) : Navigator() {

    override val currentStack: NavBackStack<NavKey> get() = state.currentStack

    /**
     * 목적지로 이동한다.
     *
     * 목적지가 현재 스택의 루트면 그 스택을 루트만 남기고 비우고(루트 재선택),
     * 다른 루트면 그 루트의 스택으로 옮기고,
     * 그 외에는 현재 스택 위에 화면을 쌓는다.
     */
    override fun navigate(key: NavKey) {
        when (key) {
            state.currentRootKey -> clearStack()
            in state.rootKeys -> goToRoot(key)
            else -> goToKey(key)
        }
    }

    /**
     * 이전 화면으로 돌아간다. 시작 화면에서는 아무 일도 하지 않는다.
     *
     * 현재 스택의 화면을 먼저 걷어내고, 루트에 도달하면 이전에 있던 루트로 돌아간다.
     */
    override fun goBack() {
        when (state.currentKey) {
            state.startRootKey -> Unit
            state.currentRootKey -> state.rootHistory.removeLastOrNull()
            else -> state.currentStack.removeLastOrNull()
        }
    }

    /** 현재 스택을 루트만 남기고 비운다. */
    private fun clearStack() {
        state.currentStack.run { if (size > 1) subList(1, size).clear() }
    }

    /** 현재 스택 안에서 이동한다. 이미 스택에 있으면 중복 없이 맨 뒤로 옮긴다. */
    private fun goToKey(key: NavKey) {
        state.currentStack.apply {
            remove(key)
            add(key)
        }
    }

    /** 다른 루트의 스택으로 옮긴다. 시작 루트로 가면 방문 이력을 비운다. */
    private fun goToRoot(key: NavKey) {
        state.rootHistory.apply {
            if (key == state.startRootKey) clear() else remove(key)
            add(key)
        }
    }
}
