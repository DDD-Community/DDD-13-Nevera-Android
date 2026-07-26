package com.anddd.nevera.domain.testutil

import com.anddd.nevera.domain.repository.FcmTokenProvider

/**
 * [FcmTokenProvider]의 테스트용 구현.
 *
 * [error]를 설정하면 토큰 조회가 실패하는 상황을 재현한다.
 * 실제 구현은 Firebase SDK를 호출하므로 예외를 던질 수 있고,
 * `SyncDeviceTokenUseCase`가 그 예외를 삼키는지 검증할 때 쓴다.
 */
class FakeFcmTokenProvider(
    var token: String? = null,
    var error: Throwable? = null,
) : FcmTokenProvider {

    var getTokenCount: Int = 0
        private set

    override suspend fun getToken(): String? {
        getTokenCount++
        error?.let { throw it }
        return token
    }
}
