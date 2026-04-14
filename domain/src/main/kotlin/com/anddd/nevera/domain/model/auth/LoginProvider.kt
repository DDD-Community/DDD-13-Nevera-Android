package com.anddd.nevera.domain.model.auth

enum class LoginProvider(val providerName: String) {
    EMAIL("email"),
    GOOGLE("google");

    companion object {
        fun toProvider(providerName: String?): LoginProvider? {
            return when (providerName) {
                EMAIL.providerName -> EMAIL
                GOOGLE.providerName -> GOOGLE
                else -> null
            }
        }
    }
}
