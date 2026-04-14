package com.anddd.nevera.data.model.auth

internal data class EmailVerifyRequest(
    val email: String,
    val authCode: String
)
