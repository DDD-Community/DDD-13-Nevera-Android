package com.anddd.nevera.domain.repository

interface FcmTokenProvider {
    suspend fun getToken(): String?
}
