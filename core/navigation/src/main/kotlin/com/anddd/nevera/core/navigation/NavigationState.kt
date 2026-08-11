package com.anddd.nevera.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * 앱 본문의 백스택 상태. 관찰 가능한 리스트라 원하는 모양을 직접 쓴다.
 *
 * 스택을 두 층으로 나눈다.
 * - [topLevelStack] 은 탭 방문 순서다. 마지막 원소가 현재 탭이다.
 * - [subStacks] 는 탭마다 하나씩 있는 화면 스택이다.
 *
 * 층을 나누면 탭별 상태가 저절로 보존된다.
 * 탭을 옮겨도 그 탭의 서브스택은 손대지 않기 때문에, 돌아왔을 때 떠날 때의 화면이 그대로 있다.
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
