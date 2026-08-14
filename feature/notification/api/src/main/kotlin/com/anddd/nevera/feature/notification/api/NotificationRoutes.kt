package com.anddd.nevera.feature.notification.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// ── 진입점 ────────────────────────────────────────────────────────────────────

/** 알림 목록 화면. 홈·냉장고·마이페이지 세 곳에서 진입한다. */
@Serializable
data object NotificationRoute : NavKey
