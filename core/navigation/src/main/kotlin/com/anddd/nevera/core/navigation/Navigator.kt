package com.anddd.nevera.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * 화면 이동을 이름 붙은 연산으로만 노출하는 통로.
 *
 * 백스택은 가변 리스트라 그대로 넘기면 호출부가 어떤 화면이든 지우고 끼워 넣을 수 있다.
 * 이 타입은 스택 조작을 여기 선언된 연산으로 제한한다.
 *
 * 구현체는 다루는 스택 개수로 갈린다. [SingleStackNavigator]는 하나,
 * [MultiStackNavigator]는 루트마다 하나씩이다. 차이는 [navigate]와 [goBack]에만 있고,
 * 나머지 연산은 [currentStack]만 알면 되므로 여기서 공유한다.
 */
abstract class Navigator {

    /**
     * 화면을 쌓고 걷어내는 스택. 항상 하나 이상을 담는다.
     *
     * [replaceStack]과 [goBack]이 이 불변을 전제로 첫 원소를 남긴다.
     */
    protected abstract val currentStack: NavBackStack<NavKey>

    /**
     * [key]로 이동한다. 같은 키가 스택에 두 번 쌓이지 않는다.
     *
     * 스택이 여럿인 구현에서는 [key]가 어느 스택의 루트인지에 따라 이동 방식이 갈린다.
     */
    abstract fun navigate(key: NavKey)

    /** 이전 화면으로 돌아간다. 더 돌아갈 곳이 없으면 아무 일도 하지 않는다. */
    abstract fun goBack()

    /**
     * 현재 스택의 첫 원소는 남기고 그 위를 [stack]으로 통째로 바꾼다.
     *
     * 호출 전에 무엇이 쌓여 있었든 결과가 같아지므로, 여러 화면을 한 번에 얹어야 하는
     * 진입에서 도착 상태를 하나로 고정할 수 있다.
     */
    fun replaceStack(stack: List<NavKey>) {
        currentStack.apply {
            if (size > 1) subList(1, size).clear()
            addAll(stack)
        }
    }

    /**
     * [replace]의 구현. 공개 인라인 함수의 본문이 호출해야 해 바이너리에는 열려 있지만
     * 소스에서 직접 부르지 않는다.
     */
    @PublishedApi
    internal fun replace(replaced: Class<out NavKey>, key: NavKey) {
        currentStack.apply {
            removeAll { replaced.isInstance(it) }
            add(key)
        }
    }
}

/**
 * 현재 스택에서 타입 [T]인 항목을 모두 지우고 [key]를 얹는다. 뒤로가기로 [T]에 돌아갈 수 없다.
 *
 * 지울 대상을 인스턴스가 아니라 타입으로 받는다. 같은 화면이 인자만 달리해 스택에 있을 때
 * 인스턴스로 지우면 나머지가 남기 때문이다.
 */
inline fun <reified T : NavKey> Navigator.replace(key: NavKey) {
    replace(T::class.java, key)
}
