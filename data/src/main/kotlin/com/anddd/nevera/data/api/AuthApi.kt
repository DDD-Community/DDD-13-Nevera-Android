package com.anddd.nevera.data.api

import retrofit2.http.Body
import retrofit2.http.POST

internal interface AuthApi {
    // TODO: 백엔드 엔드포인트 확정 후 경로 업데이트 필요
    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): RefreshResponse
}

internal data class RefreshRequest(val refreshToken: String)

internal data class RefreshResponse(val accessToken: String, val refreshToken: String)
