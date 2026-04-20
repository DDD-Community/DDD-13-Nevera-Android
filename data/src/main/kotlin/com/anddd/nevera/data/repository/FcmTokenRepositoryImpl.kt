package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.map
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.FcmTokenLocalDataSource
import com.anddd.nevera.data.datasource.NotificationRemoteDataSource
import com.anddd.nevera.data.mapper.error.toFcmTokenError
import com.anddd.nevera.domain.model.notification.FcmTokenError
import com.anddd.nevera.domain.repository.FcmTokenRepository
import javax.inject.Inject

internal class FcmTokenRepositoryImpl @Inject constructor(
    private val fcmTokenDataSource: FcmTokenLocalDataSource,
    private val notificationDataSource: NotificationRemoteDataSource,
    private val apiCall: ApiCallExecutor,
) : FcmTokenRepository {

    override suspend fun getFcmToken(): String? {
        return fcmTokenDataSource.getFcmToken()
    }

    override suspend fun saveFcmToken(token: String) {
        fcmTokenDataSource.saveFcmToken(token)
    }

    override suspend fun isSyncNeeded(): Boolean {
        return fcmTokenDataSource.isSyncNeeded()
    }

    override suspend fun setNeedsSync(value: Boolean) {
        fcmTokenDataSource.setNeedsSync(value)
    }

    override suspend fun markTokenForSync(token: String) {
        fcmTokenDataSource.markTokenForSync(token)
    }

    override suspend fun registerFcmToken(token: String): NeveraResult<Unit, FcmTokenError> {
        return apiCall { notificationDataSource.registerFcmToken(token) }
            .map(
                onSuccess = { Unit },
                onFailure = { it.toFcmTokenError() },
            )
    }
}
