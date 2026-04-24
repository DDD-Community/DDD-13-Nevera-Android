package com.anddd.nevera.domain.scheduler

interface FcmSyncScheduler {
    /** 새 FCM 토큰 발급 시 호출. WorkManager를 통해 즉시 서버 등록을 예약한다. */
    fun scheduleUpdateFcmToken(newFcmToken: String)
    /** 앱 시작 또는 로그인 시 호출. 토큰이 미등록 상태면 백그라운드에서 동기화를 시도한다. */
    fun scheduleSyncFcmToken()
    /** 로그아웃 시 호출. 대기 중인 FCM 동기화 작업을 취소한다. */
    fun cancelSyncFcmToken()
}
