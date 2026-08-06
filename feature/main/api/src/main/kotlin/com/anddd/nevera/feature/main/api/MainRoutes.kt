package com.anddd.nevera.feature.main.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// ── 진입점 ────────────────────────────────────────────────────────────────────

/** 홈 화면. 바텀 탭이자 세션의 백스택 루트다. */
@Serializable
data object HomeRoute : NavKey
