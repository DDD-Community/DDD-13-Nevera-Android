package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.di.RefreshApi
import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.UserApi
import com.anddd.nevera.data.model.auth.RefreshRequest
import com.anddd.nevera.data.model.auth.TokenResponse
import javax.inject.Inject

internal class RefreshDataSourceImpl @Inject constructor(
    // authInterceptor 미포함
    @param:RefreshApi private val userApi: UserApi
) : RefreshDataSource {

    override suspend fun refresh(refreshToken: String): ApiResponse<TokenResponse> {
        return userApi.refresh(RefreshRequest(refreshToken))
    }
}
