package com.anddd.nevera.domain.repository

import androidx.paging.PagingData
import com.anddd.nevera.domain.model.notification.AppNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(): Flow<PagingData<AppNotification>>
    suspend fun markAsRead(id: String)
}
