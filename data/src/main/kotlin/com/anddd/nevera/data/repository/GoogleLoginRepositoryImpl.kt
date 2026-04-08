package com.anddd.nevera.data.repository

import com.anddd.nevera.data.datasource.GoogleAuthDataSource
import com.anddd.nevera.domain.repository.GoogleLoginRepository
import javax.inject.Inject

class GoogleLoginRepositoryImpl @Inject constructor(
    private val googleAuthDataSource: GoogleAuthDataSource,
) : GoogleLoginRepository {

    override suspend fun googleLogin(): String {
        return googleAuthDataSource.getIdToken()
    }
}
