package com.anddd.nevera.domain.usecase.notification

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.domain.model.notification.FcmTokenError
import com.anddd.nevera.domain.repository.FcmTokenRepository
import javax.inject.Inject

class SyncFcmTokenUseCase @Inject constructor(
    private val fcmTokenRepository: FcmTokenRepository,
    private val fcmTokenProvider: FcmTokenProvider,
) {

    suspend operator fun invoke(): NeveraResult<Unit, FcmTokenError> {
        var fcmToken = fcmTokenRepository.getFcmToken()
        var needsSync = fcmTokenRepository.isSyncNeeded()

        if (fcmToken.isNullOrEmpty()) {
            val fetched: String? = runCatching {
                fcmTokenProvider.getToken()
            }.getOrNull()

            if (!fetched.isNullOrEmpty()) {
                fcmToken = fetched
                fcmTokenRepository.markTokenForSync(fetched)
                needsSync = true
            }
        }
        if (!needsSync || fcmToken.isNullOrEmpty()) return NeveraResult.Success(Unit)

        return fcmTokenRepository.registerFcmToken(fcmToken)
            .onSuccess { fcmTokenRepository.setNeedsSync(false) }
    }
}
