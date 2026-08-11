package com.anddd.nevera.core.navigation

import androidx.navigation3.runtime.NavKey

/**
 * feature가 화면 이동에 쓰는 좁은 통로.
 *
 * [NavigationState]를 그대로 넘기지 않는 이유는 권한 범위 때문이다. 백스택은 가변
 * 리스트라, 넘기는 순간 feature가 어떤 화면이든 지우고 끼워 넣을 수 있다. 그러면
 * "어떤 화면에서 나가면 스택이 어떻게 되는가"라는 정책이 모듈마다 흩어진다.
 *
 * 그래서 이 통로는 백스택 조작을 **이름 붙은 정책**으로만 노출한다. 새 정책이 필요하면
 * 여기에 이름을 붙여 추가하고, 호출부에서 스택을 직접 조립하지 않는다.
 *
 * 루트 전환 정책도 [navigate] 안에 있다. 조립 지점인 :app이 전환 규칙을 따로 들고
 * 있을 필요가 없다.
 */
class Navigator(private val state: NavigationState) {

    /**
     * 목적지로 이동한다.
     *
     * 목적지가 현재 스택의 루트면 그 스택을 루트만 남기고 비우고(루트 재선택),
     * 다른 루트면 그 루트의 스택으로 옮기고,
     * 그 외에는 현재 스택 위에 화면을 쌓는다.
     */
    fun navigate(key: NavKey) {
        when (key) {
            state.currentRootKey -> clearStack()
            in state.rootKeys -> goToRoot(key)
            else -> goToKey(key)
        }
    }

    /** 이전 화면으로 돌아간다. 시작 화면에서는 아무 일도 하지 않는다. */
    fun goBack() {
        when (state.currentKey) {
            state.startRootKey -> Unit
            state.currentRootKey -> state.rootHistory.removeLastOrNull()
            else -> state.currentStack.removeLastOrNull()
        }
    }

    /**
     * 딥링크로 진입할 화면 스택을 통째로 조립한다.
     *
     * [root]의 스택으로 옮긴 뒤 그 스택을 [stack]으로 교체한다. 딥링크가 앱을 어느
     * 상태에서 열든 뒤로가기의 도착점이 루트로 같아진다.
     */
    fun openDeeplink(root: NavKey, stack: List<NavKey>) {
        if (root != state.currentRootKey) goToRoot(root)
        state.currentStack.apply {
            if (size > 1) subList(1, size).clear()
            addAll(stack)
        }
    }

    /**
     * [replaceStep]의 구현. 타입 파라미터를 지울 수 없어 밖으로 열려 있을 뿐이니
     * 직접 호출하지 않는다.
     */
    @PublishedApi
    internal fun replaceStep(replaced: Class<out NavKey>, key: NavKey) {
        state.currentStack.apply {
            removeAll { replaced.isInstance(it) }
            add(key)
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

/**
 * 흐름의 현재 단계 [T]를 [key]로 교체한다. 뒤로가기로 [T]에 돌아갈 수 없다.
 *
 * 촬영 → 인식결과처럼 이전 단계로 되돌아가는 것이 의미 없는 전이에 쓴다.
 *
 * 단계를 인스턴스가 아니라 타입으로 지정하는 이유는, `OcrCaptureRoute(openGallery = true)`
 * 처럼 인자가 달라도 같은 단계로 취급해야 하기 때문이다. 인스턴스로 지우면 인자가 다른
 * 항목이 스택에 남는다.
 */
inline fun <reified T : NavKey> Navigator.replaceStep(key: NavKey) {
    replaceStep(T::class.java, key)
}
