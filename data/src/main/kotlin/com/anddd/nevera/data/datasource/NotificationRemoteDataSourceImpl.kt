package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.NotificationApi
import com.anddd.nevera.data.model.notification.NotificationListResponse
import javax.inject.Inject

internal class NotificationRemoteDataSourceImpl @Inject constructor(
    private val notificationApi: NotificationApi,
) : NotificationRemoteDataSource {

    override suspend fun getNotifications(offset: Int): ApiResponse<List<NotificationListResponse>> =
        notificationApi.getNotifications(offset)
}
