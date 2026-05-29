package com.anddd.nevera.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.anddd.nevera.data.datasource.NotificationLocalDataSource
import com.anddd.nevera.data.datasource.NotificationRemoteMediator
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.data.mapper.toEntity
import com.anddd.nevera.domain.model.notification.AppNotification
import com.anddd.nevera.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
internal class NotificationRepositoryImpl @Inject constructor(
    private val remoteMediator: NotificationRemoteMediator,
    private val localDataSource: NotificationLocalDataSource,
) : NotificationRepository {

    override fun getNotifications(): Flow<PagingData<AppNotification>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = { localDataSource.getPagingSource() },
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }

    override fun hasUnread(): Flow<Boolean> = localDataSource.hasUnread()

    override suspend fun insert(notification: AppNotification) {
        localDataSource.insert(notification.toEntity())
    }

    override suspend fun markAsRead(id: String) {
        localDataSource.markAsRead(id)
    }

    override suspend fun markAllAsRead() {
        localDataSource.markAllAsRead()
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
