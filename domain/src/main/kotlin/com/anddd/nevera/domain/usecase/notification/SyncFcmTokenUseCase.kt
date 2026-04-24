package com.anddd.nevera.domain.usecase.notification

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.domain.model.notification.FcmTokenError
import com.anddd.nevera.domain.repository.FcmTokenProvider
import com.anddd.nevera.domain.repository.FcmTokenRepository
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class SyncFcmTokenUseCase @Inject constructor(
    private val fcmTokenRepository: FcmTokenRepository,
    private val fcmTokenProvider: FcmTokenProvider,
) {

    suspend operator fun invoke(): NeveraResult<Unit, FcmTokenError> {
        var fcmToken = fcmTokenRepository.getFcmToken()
        var isSyncNeeded = fcmTokenRepository.isSyncNeeded()

        if (fcmToken.isNullOrEmpty()) {
            val fetched: String? = runCatching {
                fcmTokenProvider.getToken()
            }.onFailure { if (it is CancellationException) throw it }.getOrNull()

            if (!fetched.isNullOrEmpty()) {
                fcmToken = fetched
                fcmTokenRepository.markTokenForSync(fetched)
                isSyncNeeded = true
            }
        }
        if (!isSyncNeeded || fcmToken.isNullOrEmpty()) return NeveraResult.Success(Unit)

        return fcmTokenRepository.registerFcmToken(fcmToken)
            .onSuccess { fcmTokenRepository.clearSyncNeeded() }
    }
}
