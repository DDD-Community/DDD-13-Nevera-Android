package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.common.CommonError

internal fun NetworkError.toCommonError(): CommonError = when (this) {
    is NetworkError.HttpError -> CommonError.ServerError(message)
    is NetworkError.NetworkConnectionError -> CommonError.NetworkUnavailable
    is NetworkError.TimeoutError -> CommonError.Timeout
    is NetworkError.UnknownError -> CommonError.Unknown
}
