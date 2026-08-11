package com.anddd.nevera.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * 앱 본문의 백스택 상태. 관찰 가능한 리스트라 원하는 모양을 직접 쓴다.
 *
 * 스택을 두 층으로 나눈다.
 * - [stacksByRoot] 는 동시에 살아 있는 화면 스택들이다. 각 스택은 자기 루트 키로 식별되고,
 *   그 키가 곧 그 스택의 첫 원소다.
 * - [rootHistory] 는 루트를 오간 순서다. 마지막 원소가 지금 보고 있는 스택의 루트다.
 *
 * 층을 나누면 스택별 상태가 저절로 보존된다. 다른 루트로 옮겨도 떠나온 스택은 손대지 않기
 * 때문에, 돌아왔을 때 떠날 때의 화면이 그대로 있다.
 *
 * 여기서 '루트'는 UI 형태와 무관하다. 무엇을 루트로 삼을지는 조립 지점이 정한다.
 *
 * 인증 이전 화면(스플래시·로그인)은 이 상태에 포함하지 않는다. 그 화면들은 루트도 아니고
 * 어떤 루트에 속하지도 않으므로, NavDisplay 밖에서 별도로 다룬다.
 */
class NavigationState(
    val startRootKey: NavKey,
    val rootHistory: NavBackStack<NavKey>,
    val stacksByRoot: Map<NavKey, NavBackStack<NavKey>>,
) {
    /** 지금 보고 있는 스택의 루트. */
    val currentRootKey: NavKey get() = rootHistory.last()

    /** 존재하는 루트 전부. 방문 여부와 무관하다는 점에서 [rootHistory]와 다르다. */
    val rootKeys: Set<NavKey> get() = stacksByRoot.keys

    /** 지금 보고 있는 화면 스택. */
    val currentStack: NavBackStack<NavKey>
        get() = stacksByRoot[currentRootKey]
            ?: error("$currentRootKey 를 루트로 하는 스택이 없습니다")

    /** 현재 보이는 화면. */
    val currentKey: NavKey get() = currentStack.last()
}
