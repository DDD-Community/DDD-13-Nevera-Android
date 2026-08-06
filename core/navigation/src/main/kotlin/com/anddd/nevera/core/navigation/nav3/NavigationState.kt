package com.anddd.nevera.core.navigation.nav3

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Navigation 3의 백스택 상태.
 *
 * Navigation 2에서는 백스택이 NavController 안에 감춰져 있고 popUpTo·saveState 같은
 * 옵션으로 간접 조작했다. Navigation 3에서는 백스택이 관찰 가능한 리스트라 직접 다룬다.
 *
 * 스택을 두 층으로 나눈다.
 * - [topLevelStack] 은 탭 방문 순서다. 마지막 원소가 현재 탭이다.
 * - [subStacks] 는 탭마다 하나씩 있는 화면 스택이다.
 *
 * 이렇게 하면 Navigation 2의 saveState/restoreState 조합 없이도 탭별 상태가 보존된다.
 * 탭을 옮겨도 그 탭의 서브스택은 그대로 남아 있기 때문이다.
 *
 * 인증 이전 화면(스플래시·로그인)은 이 상태에 포함하지 않는다. 그 화면들은 탭도 아니고
 * 탭에 속하지도 않으므로, NavDisplay 밖에서 별도로 다룬다.
 */
class NavigationState(
    val startKey: NavKey,
    val topLevelStack: NavBackStack<NavKey>,
    val subStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    /** 현재 선택된 탭. */
    val currentTopLevelKey: NavKey get() = topLevelStack.last()

    /** 탭 목록. */
    val topLevelKeys: Set<NavKey> get() = subStacks.keys

    /** 현재 탭의 화면 스택. */
    val currentSubStack: NavBackStack<NavKey>
        get() = subStacks[currentTopLevelKey]
            ?: error("$currentTopLevelKey 에 해당하는 서브스택이 없습니다")

    /** 현재 보이는 화면. */
    val currentKey: NavKey get() = currentSubStack.last()
}
