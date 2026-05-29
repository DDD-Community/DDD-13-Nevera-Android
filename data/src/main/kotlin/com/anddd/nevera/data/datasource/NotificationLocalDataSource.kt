package com.anddd.nevera.data.datasource

import androidx.paging.PagingSource
import com.anddd.nevera.core.database.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

internal interface NotificationLocalDataSource {
    fun getPagingSource(): PagingSource<Int, NotificationEntity>
    fun hasUnread(): Flow<Boolean>
    suspend fun insertAllIgnoring(entities: List<NotificationEntity>)
    suspend fun insert(entity: NotificationEntity)
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
}
