package com.anddd.nevera.domain.usecase.auth

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.domain.model.common.MessageResult
import com.anddd.nevera.domain.repository.FcmTokenRepository
import com.anddd.nevera.domain.repository.TokenRepository
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val fcmTokenRepository: FcmTokenRepository,
) {

    suspend operator fun invoke(isDebug: Boolean): NeveraResult<MessageResult, NetworkError> {
        return userRepository.logout()
            .onSuccess {
                clearLoginInfo(isDebug)
                clearFcmData(isDebug)
            }
    }

    private suspend fun clearLoginInfo(isDebug: Boolean) {
        try {
            tokenRepository.clearLoginInfo()
        } catch (ce: CancellationException) {
            throw ce
        } catch (t: Throwable) {
            if (isDebug) {
                t.printStackTrace()
            }
        }
    }

    private suspend fun clearFcmData(isDebug: Boolean) {
        try {
            fcmTokenRepository.clearFcmData()
        } catch (ce: CancellationException) {
            throw ce
        } catch (t: Throwable) {
            if (isDebug) {
                t.printStackTrace()
            }
        }
    }
}
