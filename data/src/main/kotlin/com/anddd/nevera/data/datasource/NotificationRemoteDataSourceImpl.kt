package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.NotificationApi
import com.anddd.nevera.data.model.notification.NotificationListResponse
import com.anddd.nevera.data.model.notification.NotificationSettingsResponse
import com.anddd.nevera.data.model.notification.NotificationTimeResponse
import com.anddd.nevera.data.model.notification.UpdateNotificationEnabledRequest
import com.anddd.nevera.data.model.notification.UpdateNotificationTimeRequest
import javax.inject.Inject

internal class NotificationRemoteDataSourceImpl @Inject constructor(
    private val notificationApi: NotificationApi,
) : NotificationRemoteDataSource {

    override suspend fun getNotifications(offset: Int): ApiResponse<List<NotificationListResponse>> =
        notificationApi.getNotifications(offset)

    override suspend fun getNotificationTime(): ApiResponse<NotificationSettingsResponse> =
        notificationApi.getNotificationTime()

    override suspend fun updateNotificationEnabled(request: UpdateNotificationEnabledRequest): ApiResponse<NotificationSettingsResponse> =
        notificationApi.updateNotificationEnabled(request)

    override suspend fun updateNotificationTime(request: UpdateNotificationTimeRequest): ApiResponse<NotificationTimeResponse> =
        notificationApi.updateNotificationTime(request)
}
