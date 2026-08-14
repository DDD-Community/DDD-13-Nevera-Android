package com.anddd.nevera.feature.auth.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// ── 진입점 ────────────────────────────────────────────────────────────────────
//
// 인증 전 흐름은 세션 상태(SessionState)로 진입한다. 앱 본문과 백스택을
// 공유하지 않으므로 다른 모듈이 이 목적지로 이동하지 않는다.

@Serializable
data object LoginRoute : NavKey

@Serializable
data object SignupRoute : NavKey
