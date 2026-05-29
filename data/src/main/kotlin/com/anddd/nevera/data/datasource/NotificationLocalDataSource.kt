package com.anddd.nevera.data.datasource

import androidx.paging.PagingSource
import com.anddd.nevera.core.database.entity.NotificationEntity

internal interface NotificationLocalDataSource {
    fun getPagingSource(): PagingSource<Int, NotificationEntity>
    suspend fun insertAllIgnoring(entities: List<NotificationEntity>)
    suspend fun markAsRead(id: String)
}
