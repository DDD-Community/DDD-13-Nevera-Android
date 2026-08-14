package com.anddd.nevera.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack

/**
 * 루트가 여럿인 흐름의 백스택 상태.
 *
 * 스택을 두 층으로 나눈다.
 * - [stacksByRoot] 는 동시에 살아 있는 화면 스택들이다. 각 스택은 자기 루트 키로 식별되고,
 *   그 키가 곧 그 스택의 첫 원소다.
 * - [rootHistory] 는 루트를 오간 순서다. 마지막 원소가 지금 보고 있는 스택의 루트다.
 *
 * 층을 나누면 스택별 상태가 저절로 보존된다. 다른 루트로 옮겨도 떠나온 스택은 손대지 않기
 * 때문에, 돌아왔을 때 떠날 때의 화면이 그대로 있다.
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

    /**
     * 지금 보고 있는 화면 스택.
     *
     * [rootHistory]에는 있는데 [stacksByRoot]에 없는 루트를 만나면 던진다. 두 값이 어긋난
     * 상태로는 어느 화면을 그려야 할지 정해지지 않으므로 조용히 넘기지 않는다.
     */
    val currentStack: NavBackStack<NavKey>
        get() = stacksByRoot[currentRootKey]
            ?: error("$currentRootKey 를 루트로 하는 스택이 없습니다")

    /** 현재 보이는 화면. */
    val currentKey: NavKey get() = currentStack.last()
}

/**
 * 루트마다 화면 스택을 하나씩 가진 [NavigationState]를 만든다.
 *
 * 각 스택은 rememberNavBackStack으로 만들어 프로세스 사망 후에도 복원된다.
 */
@Composable
fun rememberNavigationState(
    startRootKey: NavKey,
    rootKeys: Set<NavKey>,
): NavigationState {
    val rootHistory = rememberNavBackStack(startRootKey)
    val stacksByRoot = rootKeys.associateWith { key -> rememberNavBackStack(key) }
    return remember(startRootKey, rootKeys) {
        NavigationState(
            startRootKey = startRootKey,
            rootHistory = rootHistory,
            stacksByRoot = stacksByRoot,
        )
    }
}
