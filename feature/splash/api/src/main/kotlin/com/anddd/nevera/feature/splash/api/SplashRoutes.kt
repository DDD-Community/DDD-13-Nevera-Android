package com.anddd.nevera.feature.splash.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// ── 진입점 ────────────────────────────────────────────────────────────────────

/** 자동 로그인 확인 화면. 인증 전 흐름의 루트다. */
@Serializable
data object SplashRoute : NavKey
