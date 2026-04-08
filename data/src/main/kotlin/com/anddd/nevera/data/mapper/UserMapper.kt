package com.anddd.nevera.data.mapper

import com.anddd.nevera.data.model.LoginResponse
import com.anddd.nevera.data.model.UserResponse
import com.anddd.nevera.domain.model.LoginResult
import com.anddd.nevera.domain.model.LoginType
import com.anddd.nevera.domain.model.User

internal fun UserResponse.toDomain(): User = User(
    id = id,
    name = name,
    email = email
)

// TODO :: 임시
internal fun LoginResponse.toDomain(loginType: LoginType): LoginResult = LoginResult(
    user = user.toDomain(),
    token = accessToken,
    refreshToken = refreshToken,
    loginType = loginType
)
