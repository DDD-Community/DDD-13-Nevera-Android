package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.auth.WithdrawError

internal fun NetworkError.toWithdrawError(): WithdrawError = when (this) {
    is NetworkError.HttpError -> when (code) {
        401, 404 -> WithdrawError.SessionInvalid
        else -> WithdrawError.Common(toCommonError())
    }
    else -> WithdrawError.Common(toCommonError())
}
