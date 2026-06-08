package com.anddd.nevera.data.api

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.notification.NotificationListResponse
import com.anddd.nevera.data.model.notification.NotificationSettingsResponse
import com.anddd.nevera.data.model.notification.NotificationTimeResponse
import com.anddd.nevera.data.model.notification.RegisterFcmTokenRequest
import com.anddd.nevera.data.model.notification.RegisterFcmTokenResponse
import com.anddd.nevera.data.model.notification.UpdateNotificationEnabledRequest
import com.anddd.nevera.data.model.notification.UpdateNotificationTimeRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

internal interface NotificationApi {

    @POST("api/v1/notification/token")
    suspend fun registerFcmToken(
        @Body body: RegisterFcmTokenRequest,
    ): ApiResponse<RegisterFcmTokenResponse>

    @GET("api/v1/notification/list")
    suspend fun getNotifications(
        @Query("offset") offset: Int,
    ): ApiResponse<List<NotificationListResponse>>

    @GET("api/v1/mypage/notification")
    suspend fun getNotificationTime(): ApiResponse<NotificationSettingsResponse>

    @PUT("api/v1/mypage/notification/enabled")
    suspend fun updateNotificationEnabled(
        @Body body: UpdateNotificationEnabledRequest,
    ): ApiResponse<NotificationSettingsResponse>

    @PUT("api/v1/mypage/notification/time")
    suspend fun updateNotificationTime(
        @Body body: UpdateNotificationTimeRequest,
    ): ApiResponse<NotificationTimeResponse>
}
