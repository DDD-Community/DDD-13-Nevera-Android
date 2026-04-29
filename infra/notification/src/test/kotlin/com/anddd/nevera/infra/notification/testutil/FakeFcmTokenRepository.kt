package com.anddd.nevera.infra.notification.testutil

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.notification.FcmTokenError
import com.anddd.nevera.domain.repository.FcmTokenRepository

class FakeFcmTokenRepository(
    var storedToken: String?,
    var syncNeeded: Boolean,
    var registerResult: NeveraResult<Unit, FcmTokenError> = NeveraResult.Success(Unit),
) : FcmTokenRepository {

    val markedTokens = mutableListOf<String>()
    val registeredTokens = mutableListOf<String>()

    override suspend fun getFcmToken(): String? = storedToken

    override suspend fun markTokenForSync(token: String) {
        markedTokens += token
        storedToken = token
        syncNeeded = true
    }

    override suspend fun clearSyncNeeded() {
        syncNeeded = false
    }

    override suspend fun clearFcmData() {
        storedToken = null
        syncNeeded = false
    }

    override suspend fun isSyncNeeded(): Boolean = syncNeeded

    override suspend fun registerFcmToken(token: String): NeveraResult<Unit, FcmTokenError> {
        registeredTokens += token
        return registerResult
    }
}
