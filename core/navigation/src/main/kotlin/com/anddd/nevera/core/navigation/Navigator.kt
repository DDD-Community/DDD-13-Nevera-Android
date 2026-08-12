package com.anddd.nevera.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * feature가 화면 이동에 쓰는 좁은 통로.
 *
 * 백스택을 그대로 넘기지 않는 이유는 권한 범위 때문이다. 백스택은 가변 리스트라, 넘기는
 * 순간 feature가 어떤 화면이든 지우고 끼워 넣을 수 있다. 그러면 "어떤 화면에서 나가면
 * 스택이 어떻게 되는가"라는 정책이 모듈마다 흩어진다.
 *
 * 그래서 이 통로는 백스택 조작을 **이름 붙은 정책**으로만 노출한다. 새 정책이 필요하면
 * 여기에 이름을 붙여 추가하고, 호출부에서 스택을 직접 조립하지 않는다.
 *
 * 구현체는 스택을 몇 개 다루는지로 갈린다. [SingleStackNavigator]는 하나,
 * [MultiStackNavigator]는 루트마다 하나씩이다. 그 차이는 [navigate]와 [goBack]에만 나타나고,
 * 나머지 연산은 [currentStack] 하나만 알면 되므로 여기서 공유한다.
 */
abstract class Navigator {

    /** 지금 화면을 쌓고 걷어내는 대상. 무엇을 현재 스택으로 볼지는 구현체가 정한다. */
    protected abstract val currentStack: NavBackStack<NavKey>

    /** 목적지로 이동한다. 이동의 의미는 구현체가 정한다. */
    abstract fun navigate(key: NavKey)

    /** 이전 화면으로 돌아간다. 더 돌아갈 곳이 없으면 아무 일도 하지 않는다. */
    abstract fun goBack()

    /**
     * 현재 스택의 루트는 남기고 그 위를 [stack]으로 통째로 바꾼다.
     *
     * 여러 화면을 한 번에 얹어야 하는 진입에 쓴다. 뒤로가기를 누르면 루트로 돌아가므로,
     * 어떤 상태에서 호출하든 도착점이 같아진다.
     */
    fun replaceStack(stack: List<NavKey>) {
        currentStack.apply {
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
        currentStack.apply {
            removeAll { replaced.isInstance(it) }
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
