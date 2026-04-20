package com.anddd.nevera.domain.usecase.notification

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.domain.model.notification.FcmTokenError
import com.anddd.nevera.domain.repository.FcmTokenRepository
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class SyncFcmTokenUseCase @Inject constructor(
    private val fcmTokenRepository: FcmTokenRepository,
) {

    suspend operator fun invoke(
        defaultFcmToken: suspend () -> String? = { null }
    ): NeveraResult<Unit, FcmTokenError> {
        var fcmToken = fcmTokenRepository.getFcmToken()
        var needsSync = fcmTokenRepository.isSyncNeeded()

        if (fcmToken.isNullOrEmpty()) {
            // 앱 초기 실행 직후에는 Firebase Installations ID 생성, Google Play Services 연결,
            // network RPC queue 초기화 등이 아직 완료되지 않은 상태일 수 있다.
            // 이 상태에서 token을 요청하면 GMS RPC가 SERVICE_NOT_AVAILABLE을 반환하며
            // Firebase SDK가 내부 retry를 반복해 Task가 완료되지 않아 await()이 무한 suspend 되므로
            // timeout 설정이 필요합니다.
            val fetched: String? = withTimeoutOrNull(FCM_TOKEN_TIMEOUT_MS) {
                defaultFcmToken()
            }
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

    companion object {
        private const val FCM_TOKEN_TIMEOUT_MS = 5_000L
    }
}
