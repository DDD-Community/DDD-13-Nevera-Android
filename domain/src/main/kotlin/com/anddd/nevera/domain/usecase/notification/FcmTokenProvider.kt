package com.anddd.nevera.domain.usecase.notification

interface FcmTokenProvider {
    suspend fun getToken(): String?
}