package com.anddd.nevera.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.map
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.NotificationLocalDataSource
import com.anddd.nevera.data.datasource.NotificationRemoteDataSource
import com.anddd.nevera.data.datasource.NotificationRemoteMediator
import com.anddd.nevera.data.mapper.error.toGetNotificationTimeError
import com.anddd.nevera.data.mapper.error.toUpdateNotificationEnabledError
import com.anddd.nevera.data.mapper.error.toUpdateNotificationTimeError
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.data.mapper.toEntity
import com.anddd.nevera.data.model.notification.UpdateNotificationEnabledRequest
import com.anddd.nevera.data.model.notification.UpdateNotificationTimeRequest
import com.anddd.nevera.domain.model.notification.AppNotification
import com.anddd.nevera.domain.model.notification.GetNotificationTimeError
import com.anddd.nevera.domain.model.notification.NotificationSettings
import com.anddd.nevera.domain.model.notification.NotificationTime
import com.anddd.nevera.domain.model.notification.UpdateNotificationEnabledError
import com.anddd.nevera.domain.model.notification.UpdateNotificationTimeError
import com.anddd.nevera.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
internal class NotificationRepositoryImpl @Inject constructor(
    private val remoteMediator: NotificationRemoteMediator,
    private val localDataSource: NotificationLocalDataSource,
    private val remoteDataSource: NotificationRemoteDataSource,
    private val apiCall: ApiCallExecutor,
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

    override suspend fun getNotificationTime(): NeveraResult<NotificationSettings, GetNotificationTimeError> {
        return apiCall {
            remoteDataSource.getNotificationTime()
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toGetNotificationTimeError() },
        )
    }

    override suspend fun updateNotificationEnabled(
        enabled: Boolean
    ): NeveraResult<NotificationSettings, UpdateNotificationEnabledError> {
        return apiCall {
            val request = UpdateNotificationEnabledRequest(enabled)
            remoteDataSource.updateNotificationEnabled(request)
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toUpdateNotificationEnabledError() },
        )
    }

    override suspend fun updateNotificationTime(
        hour: Int,
        minute: Int
    ): NeveraResult<NotificationTime, UpdateNotificationTimeError> {
        return apiCall {
            val request = UpdateNotificationTimeRequest(hour, minute)
            remoteDataSource.updateNotificationTime(request)
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toUpdateNotificationTimeError() },
        )
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
