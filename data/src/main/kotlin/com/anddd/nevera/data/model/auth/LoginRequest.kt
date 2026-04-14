package com.anddd.nevera.data.model.auth

internal data class LoginRequest(
    val email: String,
    val password: String
) {
    override fun toString(): String = "LoginRequest(email=***, password=***)"
}