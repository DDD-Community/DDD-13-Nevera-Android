package com.anddd.nevera.feature.login.main.google

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.anddd.nevera.feature.login.BuildConfig
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import timber.log.Timber
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

class GoogleAuthClient @Inject constructor() {

    suspend fun getIdToken(activity: Activity): String {
        val credentialManager = CredentialManager.create(activity)
        val request = buildSignInRequest()
        val response = credentialManager.getCredential(context = activity, request = request)
        return extractIdToken(response)
    }

    private fun buildSignInRequest(): GetCredentialRequest {
        val builder = GetSignInWithGoogleOption.Builder(
            serverClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
        )

        val option = builder.setNonce(generateNonce())
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()
    }

    private fun extractIdToken(response: GetCredentialResponse): String {
        return try {
            GoogleIdTokenCredential.createFrom(
                data = response.credential.data
            ).idToken
        } catch (e: GoogleIdTokenParsingException) {
            Timber.e(e, "getIdToken failed")
            throw e
        }
    }

    private fun generateNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(rawNonce.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}