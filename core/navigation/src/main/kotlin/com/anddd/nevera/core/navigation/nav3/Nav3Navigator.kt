package com.anddd.nevera.core.navigation.nav3

import androidx.navigation3.runtime.NavKey

/**
 * Navigation 3용 이동 통로. 마이그레이션이 끝나면 기존 Navigator를 대체한다.
 *
 * Navigation 2용 Navigator와 달리 [navigate] 하나가 세 가지 경우를 스스로 구분한다.
 * 그래서 :app이 바텀 탭 전환 정책을 따로 들고 있을 필요가 없다.
 */
class Nav3Navigator(val state: NavigationState) {

    /**
     * 목적지로 이동한다.
     *
     * 목적지가 현재 탭이면 그 탭의 루트로 돌아가고(탭 재선택),
     * 다른 탭이면 탭을 전환하고,
     * 그 외에는 현재 탭 안에서 화면을 쌓는다.
     */
    fun navigate(key: NavKey) {
        when {
            key == state.currentTopLevelKey -> clearSubStack()
            key in state.topLevelKeys -> goToTopLevel(key)
            else -> goToKey(key)
        }
    }

    /** 이전 화면으로 돌아간다. 시작 화면에서는 아무 일도 하지 않는다. */
    fun goBack() {
        when (state.currentKey) {
            state.startKey -> Unit
            state.currentTopLevelKey -> state.topLevelStack.removeLastOrNull()
            else -> state.currentSubStack.removeLastOrNull()
        }
    }

    /**
     * 흐름의 현재 단계를 [key]로 교체한다. 뒤로가기로 [replaced]에 돌아갈 수 없다.
     *
     * 촬영 → 인식결과처럼 이전 단계로 되돌아가는 것이 의미 없는 전이에 쓴다.
     * 타입으로 지우는 이유는 OcrCaptureRoute(openGallery = true)처럼 인자가 달라도
     * 같은 단계로 취급해야 하기 때문이다.
     */
    fun replaceStep(replaced: Class<out NavKey>, key: NavKey) {
        state.currentSubStack.apply {
            removeAll { replaced.isInstance(it) }
            add(key)
        }
    }

    /** 현재 탭의 화면 스택을 탭 루트만 남기고 비운다. */
    private fun clearSubStack() {
        state.currentSubStack.run { if (size > 1) subList(1, size).clear() }
    }

    /** 현재 탭 안에서 이동한다. 이미 스택에 있으면 중복 없이 맨 뒤로 옮긴다. */
    private fun goToKey(key: NavKey) {
        state.currentSubStack.apply {
            remove(key)
            add(key)
        }
    }

    /** 탭을 전환한다. 시작 탭으로 가면 탭 방문 이력을 비운다. */
    private fun goToTopLevel(key: NavKey) {
        state.topLevelStack.apply {
            if (key == state.startKey) clear() else remove(key)
            add(key)
        }
    }
}

/** [replaceStep]의 타입 파라미터 버전. */
inline fun <reified T : NavKey> Nav3Navigator.replaceStep(key: NavKey) {
    replaceStep(T::class.java, key)
}
