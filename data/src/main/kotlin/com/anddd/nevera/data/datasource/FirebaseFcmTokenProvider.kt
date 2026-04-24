package com.anddd.nevera.data.datasource

import com.anddd.nevera.domain.repository.FcmTokenProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

internal class FirebaseFcmTokenProvider @Inject constructor() : FcmTokenProvider {

    // 앱 초기 실행 직후에는 Firebase Installations ID 생성, Google Play Services 연결,
    // network RPC queue 초기화 등이 아직 완료되지 않은 상태일 수 있다.
    // 이 상태에서 token을 요청하면 GMS RPC가 SERVICE_NOT_AVAILABLE을 반환하며
    // Firebase SDK가 내부 retry를 반복해 Task가 완료되지 않아 await()이 무한 suspend 되므로
    // timeout 설정이 필요합니다.
    override suspend fun getToken(): String? =
        withTimeoutOrNull(FCM_TOKEN_TIMEOUT_MS) {
            Firebase.messaging.token.await()
        }

    companion object {
        private const val FCM_TOKEN_TIMEOUT_MS = 5_000L
    }
}