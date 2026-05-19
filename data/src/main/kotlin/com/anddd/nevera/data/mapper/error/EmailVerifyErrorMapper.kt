package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.auth.EmailVerifyError

internal fun NetworkError.toEmailVerifyError(): EmailVerifyError = when (this) {
    is NetworkError.HttpError -> when (code) {
        2001 -> EmailVerifyError.InvalidCode(serverMessage = message)
        2002 -> EmailVerifyError.ExpiredCode(serverMessage = message)
        2005 -> EmailVerifyError.NotFound(serverMessage = message)
        else -> EmailVerifyError.Common(toCommonError())
    }
    else -> EmailVerifyError.Common(toCommonError())
}
