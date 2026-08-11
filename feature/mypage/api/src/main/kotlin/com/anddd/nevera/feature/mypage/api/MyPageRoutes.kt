package com.anddd.nevera.feature.mypage.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// ── 진입점 ────────────────────────────────────────────────────────────────────

/** 마이페이지 첫 화면. 자기 스택의 루트다. */
@Serializable
data object MyPageRoute : NavKey

/** 앱 정보 화면. 마이페이지에서 진입하지만 독립적으로 열려도 무방하다. */
@Serializable
data object AppInfoRoute : NavKey

/** 계정 설정 화면. */
@Serializable
data object SettingAccountRoute : NavKey

/** 알림 설정 화면. */
@Serializable
data object SettingNotificationRoute : NavKey
