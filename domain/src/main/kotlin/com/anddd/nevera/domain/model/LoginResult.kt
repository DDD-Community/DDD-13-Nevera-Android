package com.anddd.nevera.domain.model

data class LoginResult(
    val user: User,
    val token: String,
    val loginType: LoginType
)