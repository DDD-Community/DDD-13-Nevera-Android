package com.anddd.nevera.data.model

internal data class GoogleLoginRequestDto(
    val idToken: String,
)

internal data class GoogleLoginResponseDto(
    val accessToken: String,
    val refreshToken: String,
)
