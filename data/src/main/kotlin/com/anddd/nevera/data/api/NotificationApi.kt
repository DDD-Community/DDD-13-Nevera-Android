package com.anddd.nevera.data.api

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.notification.RegisterFcmTokenRequest
import com.anddd.nevera.data.model.notification.RegisterFcmTokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

internal interface NotificationApi {

    @POST("api/v1/notification/token")
    suspend fun registerFcmToken(
        @Body body: RegisterFcmTokenRequest
    ): ApiResponse<RegisterFcmTokenResponse>
}
