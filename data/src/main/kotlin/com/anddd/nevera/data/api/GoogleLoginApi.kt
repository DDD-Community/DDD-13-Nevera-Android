package com.anddd.nevera.data.api

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.GoogleLoginRequestDto
import com.anddd.nevera.data.model.GoogleLoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

internal interface GoogleLoginApi {

    @POST("api/v1/auth/google")
    suspend fun login(
        @Body request: GoogleLoginRequestDto
    ): ApiResponse<GoogleLoginResponseDto>
}
