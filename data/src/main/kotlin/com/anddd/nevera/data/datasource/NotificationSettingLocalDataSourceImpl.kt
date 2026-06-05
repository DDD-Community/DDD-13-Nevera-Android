package com.anddd.nevera.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val Context.notificationSettingDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "notification_setting"
)

internal class NotificationSettingLocalDataSourceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : NotificationSettingLocalDataSource {

    private val dataStore = context.notificationSettingDataStore

    override suspend fun getExpiryAlarmEnabled(): Boolean =
        dataStore.data.first()[KEY_EXPIRY_ALARM_ENABLED] ?: false

    override suspend fun setExpiryAlarmEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_EXPIRY_ALARM_ENABLED] = enabled }
    }

    companion object {
        private val KEY_EXPIRY_ALARM_ENABLED = booleanPreferencesKey("expiry_alarm_enabled")
    }
}
