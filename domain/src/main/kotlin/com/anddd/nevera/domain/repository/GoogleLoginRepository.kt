package com.anddd.nevera.domain.repository

interface GoogleLoginRepository {

    suspend fun googleLogin(): String
}
