package com.anddd.nevera.data.datasource

internal interface NotificationSettingLocalDataSource {
    suspend fun getExpiryAlarmEnabled(): Boolean
    suspend fun setExpiryAlarmEnabled(enabled: Boolean)
}
