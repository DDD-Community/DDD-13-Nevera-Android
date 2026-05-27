package com.anddd.nevera.domain.usecase.auth

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.domain.model.auth.WithdrawError
import com.anddd.nevera.domain.model.common.MessageResult
import com.anddd.nevera.domain.scheduler.FcmSyncScheduler
import com.anddd.nevera.domain.repository.FcmTokenRepository
import com.anddd.nevera.domain.repository.TokenRepository
import com.anddd.nevera.domain.repository.AuthRepository
import javax.inject.Inject

class WithdrawUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository,
    private val fcmTokenRepository: FcmTokenRepository,
    private val fcmSyncScheduler: FcmSyncScheduler,
) {

    suspend operator fun invoke(): NeveraResult<MessageResult, WithdrawError> {
        return authRepository.withdraw()
            .onSuccess {
                clearSession()
            }
            .onFailure { error ->
                if (error is WithdrawError.SessionInvalid) clearSession()
            }
    }

    private suspend fun clearSession() {
        fcmSyncScheduler.cancelSyncFcmToken()
        tokenRepository.clearLoginInfo()
        fcmTokenRepository.clearFcmData()
    }
}
