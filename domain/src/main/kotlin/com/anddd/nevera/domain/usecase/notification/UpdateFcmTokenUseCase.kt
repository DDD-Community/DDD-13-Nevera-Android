package com.anddd.nevera.domain.usecase.notification

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.domain.model.notification.FcmTokenError
import com.anddd.nevera.domain.repository.FcmTokenRepository
import com.anddd.nevera.domain.repository.TokenRepository
import javax.inject.Inject

class UpdateFcmTokenUseCase @Inject constructor(
    private val fcmTokenRepository: FcmTokenRepository,
    private val tokenRepository: TokenRepository,
) {

    suspend operator fun invoke(newFcmToken: String): NeveraResult<Unit, FcmTokenError> {
        val storedFcmToken = fcmTokenRepository.getFcmToken()
        val isSyncNeeded = fcmTokenRepository.isSyncNeeded()
        if (!isSyncNeeded && newFcmToken == storedFcmToken) return NeveraResult.Success(Unit)

        fcmTokenRepository.markTokenForSync(newFcmToken)

        tokenRepository.getAccessToken() ?: return NeveraResult.Success(Unit)  // 미로그인이면 여기서 종료

        return fcmTokenRepository.registerFcmToken(newFcmToken)
            .onSuccess { fcmTokenRepository.clearSyncNeeded() }
    }
}
