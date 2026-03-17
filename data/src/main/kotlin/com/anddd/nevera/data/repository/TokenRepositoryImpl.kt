package com.anddd.nevera.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.anddd.nevera.domain.repository.TokenRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

internal class TokenRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenRepository {

    /**
     * TODO :: DataSource로 분리하자, mock 도 고려해야지
     */
    private val dataStore = context.sessionDataStore

    override suspend fun saveSession(token: String, userId: String) {
        dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER_ID] = userId
        }
    }

    override suspend fun getSession(): Pair<String?, String?> {
        val prefs = dataStore.data.first()
        return prefs[KEY_TOKEN] to prefs[KEY_USER_ID]
    }

    override suspend fun getToken(): String? =
        dataStore.data.first()[KEY_TOKEN]

    override suspend fun getUserId(): String? =
        dataStore.data.first()[KEY_USER_ID]

    override suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
    }
}
