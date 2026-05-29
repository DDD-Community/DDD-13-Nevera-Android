package com.anddd.nevera.data.datasource

import androidx.paging.PagingSource
import com.anddd.nevera.core.database.dao.NotificationDao
import com.anddd.nevera.core.database.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class NotificationLocalDataSourceImpl @Inject constructor(
    private val notificationDao: NotificationDao,
) : NotificationLocalDataSource {

    override fun getPagingSource(): PagingSource<Int, NotificationEntity> =
        notificationDao.getPagingSource()

    override fun hasUnread(): Flow<Boolean> =
        notificationDao.hasUnread()

    override suspend fun insertAllIgnoring(entities: List<NotificationEntity>) =
        notificationDao.insertAllIgnoring(entities)

    override suspend fun insert(entity: NotificationEntity) =
        notificationDao.insert(entity)

    override suspend fun markAsRead(id: String) =
        notificationDao.markAsRead(id)

    override suspend fun markAllAsRead() =
        notificationDao.markAllAsRead()
}
