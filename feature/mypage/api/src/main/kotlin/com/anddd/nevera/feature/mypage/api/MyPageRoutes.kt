package com.anddd.nevera.feature.mypage.api

import kotlinx.serialization.Serializable

// ── 진입점 ────────────────────────────────────────────────────────────────────

/** 마이페이지 흐름의 진입점. :app이 바텀 탭 정의에 쓴다. */
@Serializable
data object MyPageGraphRoute

/** 마이페이지 첫 화면. :app이 바텀바 노출 여부 판단에 쓴다. */
@Serializable
data object MyPageRoute

/** 앱 정보 화면. 마이페이지에서 진입하지만 독립적으로 열려도 무방하다. */
@Serializable
data object AppInfoRoute

/** 계정 설정 화면. */
@Serializable
data object SettingAccountRoute

/** 알림 설정 화면. */
@Serializable
data object SettingNotificationRoute
