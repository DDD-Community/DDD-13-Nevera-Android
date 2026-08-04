package com.anddd.nevera.feature.mypage.api

import kotlinx.serialization.Serializable

/** 마이페이지 흐름의 진입점. :app이 바텀 탭 정의에 쓴다. */
@Serializable
data object MyPageGraphRoute

/** 마이페이지 첫 화면. :app이 바텀바 노출 여부 판단에 쓴다. */
@Serializable
data object MyPageRoute
