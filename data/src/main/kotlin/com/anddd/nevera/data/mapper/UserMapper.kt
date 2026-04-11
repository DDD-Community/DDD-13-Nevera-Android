package com.anddd.nevera.data.mapper

import com.anddd.nevera.data.model.auth.TokenResponse
import com.anddd.nevera.domain.model.LoginResult

internal fun TokenResponse.toDomain(): LoginResult = LoginResult(
    accessToken = accessToken,
    refreshToken = refreshToken
)