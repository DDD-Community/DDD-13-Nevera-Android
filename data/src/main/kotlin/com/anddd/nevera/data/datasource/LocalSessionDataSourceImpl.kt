package com.anddd.nevera.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

internal class LocalSessionDataSourceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SessionDataSource {

    private val dataStore = context.sessionDataStore

    override suspend fun getAccessToken(): String? =
        dataStore.data.first()[KEY_TOKEN]

    override suspend fun setAccessToken(accessToken: String) {
        dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = accessToken
        }
    }

    override suspend fun getRefreshToken(): String? =
        dataStore.data.first()[KEY_REFRESH_TOKEN]

    override suspend fun setRefreshToken(refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    override suspend fun setTokens(accessToken: String, refreshToken: String) {
        setAccessToken(accessToken)
        setRefreshToken(refreshToken)
    }

    override suspend fun clearLoginData() {
        dataStore.edit { it.clear() }
    }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }
}
