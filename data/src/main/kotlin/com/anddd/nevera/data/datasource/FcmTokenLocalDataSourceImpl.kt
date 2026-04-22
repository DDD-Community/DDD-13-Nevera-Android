package com.anddd.nevera.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val Context.pushTokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "push_token")

internal class FcmTokenLocalDataSourceImpl @Inject constructor(
    @ApplicationContext context: Context,
) : FcmTokenLocalDataSource {

    private val dataStore = context.pushTokenDataStore

    override suspend fun getFcmToken(): String? =
        dataStore.data.first()[KEY_FCM_TOKEN]

    override suspend fun isSyncNeeded(): Boolean =
        dataStore.data.first()[KEY_NEEDS_SYNC] ?: false

    override suspend fun setNeedsSync(value: Boolean) {
        dataStore.edit { it[KEY_NEEDS_SYNC] = value }
    }

    override suspend fun clearFcmData() {
        dataStore.edit { it.clear() }
    }

    override suspend fun markTokenForSync(token: String) {
        // DataStore.edit는 atomic transaction으로 동작한다.
        // FCM 토큰 저장과 서버 동기화 필요 상태를 동시에 업데이트하여
        // 토큰과 sync 상태 간의 불일치가 발생하지 않도록 한다.
        dataStore.edit {
            it[KEY_FCM_TOKEN] = token
            it[KEY_NEEDS_SYNC] = true
        }
    }

    companion object {
        private val KEY_FCM_TOKEN = stringPreferencesKey("fcm_token")
        private val KEY_NEEDS_SYNC = booleanPreferencesKey("fcm_token_needs_sync")
    }
}
