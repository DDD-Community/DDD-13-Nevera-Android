package com.anddd.nevera.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator

/**
 * 두 층의 스택을 NavDisplay가 그릴 수 있는 하나의 목록으로 펼친다.
 *
 * 스택마다 별도의 데코레이터를 붙여, 화면 상태와 ViewModel이 스택 단위로 유지되게 한다.
 *
 * @param rootMetadata 각 스택의 루트 화면에만 더할 metadata. 루트는 그 위의 화면들과 달리
 *   스택째로 얹히고 걷히므로, 그 이동만 다르게 다루고 싶을 때 쓰는 자리다. 무엇을 담을지는
 *   호출부가 정한다.
 */
@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>,
    rootMetadata: Map<String, Any> = emptyMap(),
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = stacksByRoot.mapValues { (rootKey, stack) ->
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = { key ->
                entryProvider(key).plusMetadataIf(key == rootKey, key, rootMetadata)
            },
        )
    }
    return rootHistory
        .flatMap { decoratedEntries[it].orEmpty() }
        .toMutableStateList()
}

/**
 * [condition]이 참이면 [extra]를 더한 새 항목을, 아니면 자기 자신을 돌려준다.
 *
 * NavEntry는 자기 키를 공개하지 않아 복사하려면 [key]를 밖에서 받아야 한다.
 * contentKey는 그대로 넘긴다. 이 값이 바뀌면 화면 상태와 ViewModel을 담아 둔 자리를 잃는다.
 */
private fun NavEntry<NavKey>.plusMetadataIf(
    condition: Boolean,
    key: NavKey,
    extra: Map<String, Any>,
): NavEntry<NavKey> {
    if (!condition || extra.isEmpty()) return this
    val source = this
    return NavEntry(
        key = key,
        contentKey = source.contentKey,
        metadata = source.metadata + extra,
        content = { source.Content() },
    )
}
