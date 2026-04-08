package com.anddd.nevera.data.datasource

import javax.inject.Inject

internal class FakeSessionDataSourceImpl @Inject constructor() : SessionDataSource {

    private var accessToken: String? = null
    private var refreshToken: String? = null

    override suspend fun getAccessToken(): String? = accessToken

    override suspend fun setAccessToken(accessToken: String) {
        this.accessToken = accessToken
    }

    override suspend fun setRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }

    override suspend fun getRefreshToken(): String? = refreshToken

    override suspend fun setTokens(accessToken: String, refreshToken: String) {
        setAccessToken(accessToken)
        setRefreshToken(refreshToken)
    }

    override suspend fun clearLoginData() {
        accessToken = null
        refreshToken = null
    }
}
