package com.anddd.nevera.feature.auth.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// ── 진입점 ────────────────────────────────────────────────────────────────────

/** 로그인 흐름의 진입점. 스플래시와 마이페이지(로그아웃)가 목적지로 삼는다. */
@Serializable
data object AuthGraphRoute : NavKey

// ── 로그인 흐름 내부 단계 (AuthGraphRoute로 진입한다) ─────────────────────────

@Serializable
data object LoginRoute : NavKey

@Serializable
data object SignupRoute : NavKey
