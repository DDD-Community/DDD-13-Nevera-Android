package com.anddd.nevera.data.datasource

internal interface FcmTokenLocalDataSource {
    suspend fun getFcmToken(): String?
    suspend fun saveFcmToken(token: String)
    suspend fun isSyncNeeded(): Boolean
    suspend fun setNeedsSync(value: Boolean)
    suspend fun markTokenForSync(token: String)
}
