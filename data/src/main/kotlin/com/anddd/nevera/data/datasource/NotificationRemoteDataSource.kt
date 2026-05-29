package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.notification.NotificationListResponse

internal interface NotificationRemoteDataSource {
    suspend fun getNotifications(offset: Int): ApiResponse<List<NotificationListResponse>>
}
