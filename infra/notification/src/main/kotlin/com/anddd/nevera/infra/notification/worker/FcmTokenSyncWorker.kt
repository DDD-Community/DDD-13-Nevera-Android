package com.anddd.nevera.infra.notification.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.repository.FcmTokenProvider
import com.anddd.nevera.domain.repository.FcmTokenRepository
import com.anddd.nevera.domain.repository.TokenRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException

@HiltWorker
class FcmTokenSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val fcmTokenRepository: FcmTokenRepository,
    private val fcmTokenProvider: FcmTokenProvider,
    private val tokenRepository: TokenRepository,
) : CoroutineWorker(context, workerParams) {

    private val isMaxRetriesExceeded: Boolean
        get() = runAttemptCount >= MAX_RETRIES

    private suspend fun isLoggedIn(): Boolean {
        return !tokenRepository.getAccessToken().isNullOrEmpty()
    }

    override suspend fun doWork(): Result {
        val inputFcmToken = inputData.getString(KEY_FCM_TOKEN)
        val fcmToken = if (inputFcmToken.isNullOrEmpty()) {
            resolveStoredOrFetchedToken()
        } else {
            resolveInputToken(inputFcmToken)
        }

        return when {
            fcmToken.isNullOrEmpty() ||
            !fcmTokenRepository.isSyncNeeded() ||
            !isLoggedIn() -> Result.success()
            else -> register(fcmToken)
        }
    }

    // 로컬 저장 토큰 우선 반환, 없으면 Firebase에서 fetch 후 저장
    private suspend fun resolveStoredOrFetchedToken(): String? {
        val stored = fcmTokenRepository.getFcmToken()
        if (!stored.isNullOrEmpty()) return stored

        val fetched = runCatching {
            // Firebase FCM Token 요청
            fcmTokenProvider.getToken()
        }.onFailure {
            if (it is CancellationException) throw it
        }.getOrNull()

        // 푸시 토큰 저장 및 isSyncNeeded 상태 true 로 상태 변경
        if (!fetched.isNullOrEmpty()) fcmTokenRepository.markTokenForSync(fetched)
        return fetched
    }

    // Firebase 콜백으로 전달된 토큰 — 기존 토큰과 동일하고 sync 불필요하면 null 반환
    private suspend fun resolveInputToken(inputFcmToken: String): String? {
        val isSyncNeeded = fcmTokenRepository.isSyncNeeded()
        val stored = fcmTokenRepository.getFcmToken()
        if (!isSyncNeeded && stored == inputFcmToken) return null
        if (stored != inputFcmToken) {
            // 푸시 토큰 저장 및 isSyncNeeded 상태 true 로 상태 변경
            fcmTokenRepository.markTokenForSync(inputFcmToken)
        }
        return inputFcmToken
    }

    private suspend fun register(token: String): Result {
        return when (fcmTokenRepository.registerFcmToken(token)) {
            is NeveraResult.Success -> {
                fcmTokenRepository.clearSyncNeeded()
                Result.success()
            }

            is NeveraResult.Failure -> if (isMaxRetriesExceeded) Result.failure() else Result.retry()
        }
    }

    companion object {
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val MAX_RETRIES = 5
        const val WORK_NAME = "fcm_token_sync"

        private val networkConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        fun createUpdateRequest(newFcmToken: String): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<FcmTokenSyncWorker>()
                .setInputData(workDataOf(KEY_FCM_TOKEN to newFcmToken))
                .setConstraints(networkConstraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .build()

        fun createSyncRequest(): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<FcmTokenSyncWorker>()
                .setConstraints(networkConstraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .build()
    }
}
