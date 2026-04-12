package com.anddd.nevera.domain.model.auth

data class LoginResult(
    val accessToken: String,
    val refreshToken: String
)
