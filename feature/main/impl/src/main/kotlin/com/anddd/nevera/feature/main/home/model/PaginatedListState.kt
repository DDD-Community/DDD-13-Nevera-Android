package com.anddd.nevera.feature.main.home.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class PaginatedListState<T>(
    val items: PersistentList<T> = persistentListOf(),
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val currentOffset: Int = 0,
)

internal fun <T> PaginatedListState<T>.appendPage(
    newItems: List<T>,
    hasMore: Boolean,
): PaginatedListState<T> = copy(
    items = items.addingAll(newItems),
    isLoadingMore = false,
    hasMore = hasMore,
    currentOffset = currentOffset + newItems.size,
)
