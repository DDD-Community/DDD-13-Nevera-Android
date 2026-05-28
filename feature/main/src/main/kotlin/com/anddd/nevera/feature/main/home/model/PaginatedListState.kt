package com.anddd.nevera.feature.main.home.model

data class PaginatedListState<T>(
    val items: List<T> = emptyList(),
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val currentOffset: Int = 0,
)

internal fun <T> PaginatedListState<T>.appendPage(
    newItems: List<T>,
    hasMore: Boolean,
): PaginatedListState<T> = copy(
    items = items + newItems,
    isLoadingMore = false,
    hasMore = hasMore,
    currentOffset = currentOffset + newItems.size,
)
