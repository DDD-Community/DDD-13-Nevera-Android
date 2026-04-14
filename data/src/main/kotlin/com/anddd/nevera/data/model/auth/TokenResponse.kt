package com.anddd.nevera.data.model.auth

internal data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)
