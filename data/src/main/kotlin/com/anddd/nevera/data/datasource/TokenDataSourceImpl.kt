package com.anddd.nevera.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.anddd.nevera.domain.model.auth.LoginProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "secure_token")

internal class TokenDataSourceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val cryptoHelper: CryptoHelper
) : TokenDataSource {

    private val dataStore = context.tokenDataStore

    override suspend fun getAccessToken(): String? =
        decryptOrNull(KEY_ACCESS_TOKEN)

    override suspend fun setAccessToken(accessToken: String) {
        dataStore.edit {
            it[KEY_ACCESS_TOKEN] = cryptoHelper.encrypt(accessToken)
        }
    }

    override suspend fun getRefreshToken(): String? =
        decryptOrNull(KEY_REFRESH_TOKEN)

    override suspend fun setRefreshToken(refreshToken: String) {
        dataStore.edit {
            it[KEY_REFRESH_TOKEN] = cryptoHelper.encrypt(refreshToken)
        }
    }

    override suspend fun setTokens(accessToken: String, refreshToken: String) {
        dataStore.edit {
            it[KEY_ACCESS_TOKEN] = cryptoHelper.encrypt(accessToken)
            it[KEY_REFRESH_TOKEN] = cryptoHelper.encrypt(refreshToken)
        }
    }

    override suspend fun getProvider(): LoginProvider? =
        LoginProvider.toProvider(decryptOrNull(KEY_LOGIN_PROVIDER))

    override suspend fun setProvider(provider: LoginProvider) {
        dataStore.edit { it[KEY_LOGIN_PROVIDER] = cryptoHelper.encrypt(provider.providerName) }
    }

    override suspend fun setLoginInfo(
        accessToken: String,
        refreshToken: String,
        provider: LoginProvider
    ) {
        dataStore.edit {
            it[KEY_ACCESS_TOKEN] = cryptoHelper.encrypt(accessToken)
            it[KEY_REFRESH_TOKEN] = cryptoHelper.encrypt(refreshToken)
            it[KEY_LOGIN_PROVIDER] = cryptoHelper.encrypt(provider.providerName)
        }
    }

    override suspend fun clearLoginInfo() {
        dataStore.edit { it.clear() }
    }

    private suspend fun decryptOrNull(key: Preferences.Key<String>): String? =
        dataStore.data.first()[key]?.let { encrypted ->
            runCatching { cryptoHelper.decrypt(encrypted) }
                .onFailure { dataStore.edit { it.remove(key) } }
                .getOrNull()
        }

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_LOGIN_PROVIDER = stringPreferencesKey("login_provider")
    }
}
