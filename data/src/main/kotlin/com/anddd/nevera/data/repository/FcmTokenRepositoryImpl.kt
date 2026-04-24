package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.map
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.FcmTokenLocalDataSource
import com.anddd.nevera.data.datasource.FcmTokenRemoteDataSource
import com.anddd.nevera.data.mapper.error.toFcmTokenError
import com.anddd.nevera.domain.model.notification.FcmTokenError
import com.anddd.nevera.domain.repository.FcmTokenRepository
import javax.inject.Inject

internal class FcmTokenRepositoryImpl @Inject constructor(
    private val fcmTokenLocalDataSource: FcmTokenLocalDataSource,
    private val fcmTokenRemoteDataSource: FcmTokenRemoteDataSource,
    private val apiCall: ApiCallExecutor,
) : FcmTokenRepository {

    override suspend fun getFcmToken(): String? {
        return fcmTokenLocalDataSource.getFcmToken()
    }

    override suspend fun markTokenForSync(token: String) {
        fcmTokenLocalDataSource.markTokenForSync(token)
    }

    override suspend fun clearSyncNeeded() {
        fcmTokenLocalDataSource.setNeedsSync(false)
    }

    override suspend fun clearFcmData() {
        fcmTokenLocalDataSource.clearFcmData()
    }

    override suspend fun isSyncNeeded(): Boolean {
        return fcmTokenLocalDataSource.isSyncNeeded()
    }

    override suspend fun registerFcmToken(token: String): NeveraResult<Unit, FcmTokenError> {
        return apiCall { fcmTokenRemoteDataSource.registerFcmToken(token) }
            .map(
                onSuccess = { },
                onFailure = { it.toFcmTokenError() },
            )
    }
}
