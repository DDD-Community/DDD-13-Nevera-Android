package com.anddd.nevera.core.network.auth

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 세션 만료 이벤트를 앱 전체에 전달하는 이벤트 버스.
 *
 * [AuthInterceptor]는 refresh API 요청이 401 Unauthorized를 반환하여
 * refresh token 만료 또는 무효를 감지한 경우 이 이벤트를 emit한다.
 *
 * NeveraApplication은 이를 구독하여 기존 Task를 모두 비우고
 * MainActivity를 새 태스크로 재시작한다.
 */
interface SessionEventBus {
    val sessionExpiredEvent: Flow<Unit>
    suspend fun emitSessionExpired()
}

@Singleton
class SessionEventBusImpl @Inject constructor() : SessionEventBus {
    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(
        replay = 0,           // 구독 시작 이전 이벤트는 재전달하지 않음 (앱 재구독 시 불필요한 중복 처리 방지)
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST  // 짧은 시간 내 중복 emit 시 최신 이벤트만 유지
    )
    override val sessionExpiredEvent: Flow<Unit> = _sessionExpiredEvent
    override suspend fun emitSessionExpired() = _sessionExpiredEvent.emit(Unit)
}