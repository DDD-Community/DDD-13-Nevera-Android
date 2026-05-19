package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.auth.LogoutError

private object LogoutErrorCode {
    const val TOKEN_NOT_FOUND = 2023
}

internal fun NetworkError.toLogoutError(): LogoutError = when (this) {
    is NetworkError.HttpError -> when (code) {
        LogoutErrorCode.TOKEN_NOT_FOUND -> LogoutError.TokenNotFound(serverMessage = message)
        else -> LogoutError.Common(toCommonError())
    }
    else -> LogoutError.Common(toCommonError())
}
