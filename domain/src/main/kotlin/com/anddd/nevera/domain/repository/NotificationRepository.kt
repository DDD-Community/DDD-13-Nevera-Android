package com.anddd.nevera.domain.repository

import androidx.paging.PagingData
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.notification.AppNotification
import com.anddd.nevera.domain.model.notification.GetNotificationTimeError
import com.anddd.nevera.domain.model.notification.NotificationSettings
import com.anddd.nevera.domain.model.notification.NotificationTime
import com.anddd.nevera.domain.model.notification.UpdateNotificationEnabledError
import com.anddd.nevera.domain.model.notification.UpdateNotificationTimeError
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(): Flow<PagingData<AppNotification>>
    fun hasUnread(): Flow<Boolean>
    suspend fun insert(notification: AppNotification)
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
    suspend fun getNotificationTime(): NeveraResult<NotificationSettings, GetNotificationTimeError>
    suspend fun updateNotificationEnabled(enabled: Boolean): NeveraResult<NotificationSettings, UpdateNotificationEnabledError>
    suspend fun updateNotificationTime(hour: Int, minute: Int): NeveraResult<NotificationTime, UpdateNotificationTimeError>
}
