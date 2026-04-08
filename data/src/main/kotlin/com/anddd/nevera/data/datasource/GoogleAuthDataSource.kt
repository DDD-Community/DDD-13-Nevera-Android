package com.anddd.nevera.data.datasource

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.anddd.nevera.data.BuildConfig
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthDataSource @Inject constructor(
    private val credentialManager: CredentialManager,
    @ApplicationContext private val context: Context,
) {

    suspend fun getIdToken(): String {
        val googleLoginOption = GetSignInWithGoogleOption.Builder(
            serverClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
        )
            .setNonce(generateNonce())
            .build()

        val credentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(credentialOption = googleLoginOption)
            .build()

        val credentialResponse = credentialManager.getCredential(
            context = context,
            request = credentialRequest
        )

        return try {
            GoogleIdTokenCredential.createFrom(
                data = credentialResponse.credential.data
            ).idToken
        } catch (e: Exception) {
            Log.e("GoogleAuthDataSource", "getIdToken failed", e)
            throw e
        }
    }

    private fun generateNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val digest = messageDigest.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
