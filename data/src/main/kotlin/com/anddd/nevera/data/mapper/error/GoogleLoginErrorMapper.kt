package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.auth.GoogleLoginError

private object GoogleLoginErrorCode {
    const val INVALID_GOOGLE_TOKEN = 2011
}

internal fun NetworkError.toGoogleLoginError(): GoogleLoginError = when (this) {
    is NetworkError.HttpError -> when (code) {
        GoogleLoginErrorCode.INVALID_GOOGLE_TOKEN -> GoogleLoginError.InvalidToken(serverMessage = message)
        else -> GoogleLoginError.Common(toCommonError())
    }
    else -> GoogleLoginError.Common(toCommonError())
}
