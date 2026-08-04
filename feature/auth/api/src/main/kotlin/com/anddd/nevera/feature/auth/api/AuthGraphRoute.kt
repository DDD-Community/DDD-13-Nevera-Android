package com.anddd.nevera.feature.auth.api

import kotlinx.serialization.Serializable

/** 로그인 흐름의 진입점. 스플래시와 마이페이지(로그아웃)가 목적지로 삼는다. */
@Serializable
data object AuthGraphRoute
