package com.anddd.nevera.feature.main.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// ── 진입점 ────────────────────────────────────────────────────────────────────

/** 홈 화면. 앱이 시작하는 스택의 루트다. */
@Serializable
data object HomeRoute : NavKey
