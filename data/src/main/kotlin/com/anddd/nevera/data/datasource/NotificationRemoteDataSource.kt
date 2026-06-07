package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.notification.NotificationListResponse
import com.anddd.nevera.data.model.notification.NotificationSettingsResponse
import com.anddd.nevera.data.model.notification.NotificationTimeResponse
import com.anddd.nevera.data.model.notification.UpdateNotificationEnabledRequest
import com.anddd.nevera.data.model.notification.UpdateNotificationTimeRequest

internal interface NotificationRemoteDataSource {
    suspend fun getNotifications(offset: Int): ApiResponse<List<NotificationListResponse>>
    suspend fun getNotificationTime(): ApiResponse<NotificationSettingsResponse>
    suspend fun updateNotificationEnabled(request: UpdateNotificationEnabledRequest): ApiResponse<NotificationSettingsResponse>
    suspend fun updateNotificationTime(request: UpdateNotificationTimeRequest): ApiResponse<NotificationTimeResponse>
}
