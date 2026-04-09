package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.GoogleLoginApi
import com.anddd.nevera.data.model.GoogleLoginRequestDto
import com.anddd.nevera.data.model.GoogleLoginResponseDto
import javax.inject.Inject

internal class RemoteGoogleLoginDataSource @Inject constructor(
    private val googleLoginApi: GoogleLoginApi
) {

    suspend fun login(idToken: String): ApiResponse<GoogleLoginResponseDto> {
        return googleLoginApi.login(GoogleLoginRequestDto(idToken = idToken))
    }
}
