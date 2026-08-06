package com.anddd.nevera.feature.splash.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// ── 진입점 ────────────────────────────────────────────────────────────────────

/** 앱 시작 화면. :app이 그래프의 시작 목적지로 쓴다. */
@Serializable
data object SplashRoute : NavKey
