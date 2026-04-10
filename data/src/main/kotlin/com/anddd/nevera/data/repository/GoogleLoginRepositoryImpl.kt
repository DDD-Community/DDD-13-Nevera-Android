package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.core.common.mapSuccess
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.GoogleAuthDataSource
import com.anddd.nevera.data.datasource.RemoteGoogleLoginDataSource
import com.anddd.nevera.domain.repository.GoogleLoginRepository
import javax.inject.Inject

internal class GoogleLoginRepositoryImpl @Inject constructor(
    private val googleAuthDataSource: GoogleAuthDataSource,
    private val remoteGoogleLoginDataSource: RemoteGoogleLoginDataSource,
    private val apiCall: ApiCallExecutor,
) : GoogleLoginRepository {

    override suspend fun googleLogin(): ApiResult<Unit> {
        return apiCall {
            val idToken = googleAuthDataSource.getIdToken()
            remoteGoogleLoginDataSource.login(idToken = idToken)
        }.mapSuccess { Unit }
    }
}
