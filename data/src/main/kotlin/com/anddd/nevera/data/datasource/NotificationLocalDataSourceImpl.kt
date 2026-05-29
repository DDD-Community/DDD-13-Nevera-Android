package com.anddd.nevera.data.datasource

import androidx.paging.PagingSource
import com.anddd.nevera.core.database.dao.NotificationDao
import com.anddd.nevera.core.database.entity.NotificationEntity
import javax.inject.Inject

internal class NotificationLocalDataSourceImpl @Inject constructor(
    private val notificationDao: NotificationDao,
) : NotificationLocalDataSource {

    override fun getPagingSource(): PagingSource<Int, NotificationEntity> =
        notificationDao.getPagingSource()

    override suspend fun insertAllIgnoring(entities: List<NotificationEntity>) =
        notificationDao.insertAllIgnoring(entities)

    override suspend fun markAsRead(id: String) =
        notificationDao.markAsRead(id)
}
