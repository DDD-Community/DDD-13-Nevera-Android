package com.anddd.nevera.feature.notification.api

import kotlinx.serialization.Serializable

/**
 * 알림 목록 화면의 목적지.
 *
 * 이 모듈에 의존을 선언한 모듈만 이 이름을 볼 수 있다.
 * 알림 화면 구현(:feature:notification:impl)은 딸려오지 않는다.
 */
@Serializable
data object NotificationRoute
