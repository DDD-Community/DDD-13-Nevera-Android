package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.auth.SignupError

private object SignupErrorCode {
    const val UNVERIFIED_EMAIL = 2003
    const val AUTH_NOT_FOUND = 2005
}

internal fun NetworkError.toSignupError(): SignupError = when (this) {
    is NetworkError.HttpError -> when (code) {
        SignupErrorCode.UNVERIFIED_EMAIL -> SignupError.UnverifiedEmail(serverMessage = message)
        SignupErrorCode.AUTH_NOT_FOUND -> SignupError.NotFound(serverMessage = message)
        else -> SignupError.Common(toCommonError())
    }
    else -> SignupError.Common(toCommonError())
}
