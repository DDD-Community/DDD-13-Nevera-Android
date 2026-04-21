package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.notification.FcmTokenError

interface FcmTokenRepository {
    suspend fun getFcmToken(): String?
    suspend fun markTokenForSync(token: String)
    suspend fun clearSyncNeeded()
    suspend fun clearFcmData()
    suspend fun isSyncNeeded(): Boolean
    suspend fun registerFcmToken(token: String): NeveraResult<Unit, FcmTokenError>
}
