package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.auth.TokenResponse

internal interface RefreshDataSource {
    suspend fun refresh(refreshToken: String): ApiResponse<TokenResponse>
}
