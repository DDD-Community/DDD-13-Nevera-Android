package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.notification.RegisterFcmTokenResponse

internal interface FcmTokenRemoteDataSource {
    suspend fun registerFcmToken(pushToken: String): ApiResponse<RegisterFcmTokenResponse>
}
