package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.user.ProfileResponse

internal interface UserRemoteDataSource {
    suspend fun getProfile(): ApiResponse<ProfileResponse>
}
