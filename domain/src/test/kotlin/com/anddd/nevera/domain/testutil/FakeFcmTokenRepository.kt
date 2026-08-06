package com.anddd.nevera.domain.testutil

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.notification.FcmTokenError
import com.anddd.nevera.domain.repository.FcmTokenRepository

/**
 * [FcmTokenRepository]의 테스트용 구현.
 *
 * 저장된 토큰과 동기화 필요 여부를 실제로 들고 있으므로,
 * "등록에 성공하면 동기화 필요 표시가 해제된다" 같은 상태 변화까지 그대로 검증할 수 있다.
 */
class FakeFcmTokenRepository(
    var storedToken: String? = null,
    var syncNeeded: Boolean = false,
    var registerResult: NeveraResult<Unit, FcmTokenError> = NeveraResult.Success(Unit),
) : FcmTokenRepository {

    val savedTokens = mutableListOf<String>()
    val registeredTokens = mutableListOf<String>()
    var clearSyncNeededCount: Int = 0
        private set

    override suspend fun getFcmToken(): String? = storedToken

    override suspend fun saveTokenPendingSync(token: String) {
        savedTokens += token
        storedToken = token
        syncNeeded = true
    }

    override suspend fun clearSyncNeeded() {
        clearSyncNeededCount++
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
