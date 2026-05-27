package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.di.RefreshApi
import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.AuthApi
import com.anddd.nevera.data.model.auth.RefreshRequest
import com.anddd.nevera.data.model.auth.TokenResponse
import javax.inject.Inject

internal class RefreshDataSourceImpl @Inject constructor(
    // authInterceptor 미포함
    @param:RefreshApi private val authApi: AuthApi
) : RefreshDataSource {

    override suspend fun refresh(refreshToken: String): ApiResponse<TokenResponse> {
        return authApi.refresh(RefreshRequest(refreshToken))
    }
}
