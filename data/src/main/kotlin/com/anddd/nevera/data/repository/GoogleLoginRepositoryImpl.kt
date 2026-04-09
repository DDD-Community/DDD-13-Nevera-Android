package com.anddd.nevera.data.repository

import android.util.Log
import com.anddd.nevera.data.datasource.GoogleAuthDataSource
import com.anddd.nevera.data.datasource.RemoteGoogleLoginDataSource
import com.anddd.nevera.domain.repository.GoogleLoginRepository
import javax.inject.Inject

internal class GoogleLoginRepositoryImpl @Inject constructor(
    private val googleAuthDataSource: GoogleAuthDataSource,
    private val remoteGoogleLoginDataSource: RemoteGoogleLoginDataSource,
) : GoogleLoginRepository {

    override suspend fun googleLogin() {
        val idToken = googleAuthDataSource.getIdToken()
        val response = remoteGoogleLoginDataSource.login(idToken = idToken)
        Log.d("GoogleLoginRepository", "googleLogin: $response")
    }
}
