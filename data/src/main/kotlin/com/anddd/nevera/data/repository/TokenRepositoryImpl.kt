package com.anddd.nevera.data.repository

import com.anddd.nevera.data.datasource.LocalSessionDataSource
import com.anddd.nevera.data.datasource.SessionDataSource
import com.anddd.nevera.domain.repository.TokenRepository
import javax.inject.Inject

internal class TokenRepositoryImpl @Inject constructor(
    @param:LocalSessionDataSource private val sessionDataSource: SessionDataSource
) : TokenRepository {

    override suspend fun getAccessToken(): String? =
        sessionDataSource.getAccessToken()

    override suspend fun setAccessToken(accessToken: String) =
        sessionDataSource.setAccessToken(accessToken)

    override suspend fun getRefreshToken(): String? =
        sessionDataSource.getRefreshToken()

    override suspend fun setRefreshToken(refreshToken: String) =
        sessionDataSource.setRefreshToken(refreshToken)

    override suspend fun setTokens(accessToken: String, refreshToken: String) {
        setAccessToken(accessToken)
        setRefreshToken(refreshToken)
    }

    override suspend fun clearLoginData() = sessionDataSource.clearLoginData()
}
