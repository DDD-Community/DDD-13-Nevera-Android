package com.anddd.nevera.data.mapper

import com.anddd.nevera.data.model.auth.MessageResponse
import com.anddd.nevera.data.model.auth.TokenResponse
import com.anddd.nevera.domain.model.auth.LoginResult
import com.anddd.nevera.domain.model.common.MessageResult

internal fun TokenResponse.toDomain(): LoginResult = LoginResult(
    accessToken = accessToken,
    refreshToken = refreshToken
)

internal fun MessageResponse.toDomain(): MessageResult = MessageResult(
    message = message
)