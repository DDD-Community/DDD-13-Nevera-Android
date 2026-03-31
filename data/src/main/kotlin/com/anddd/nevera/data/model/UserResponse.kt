package com.anddd.nevera.data.model

internal data class UserResponse(
    val id: String,
    val name: String,
    val email: String
)

internal data class LoginRequest(
    val email: String,
    val password: String
)

internal data class SnsLoginRequest(
    val provider: String,
    val token: String
)

internal data class LoginResponse(
    val user: UserResponse,
    val accessToken: String,
    val refreshToken: String
)
