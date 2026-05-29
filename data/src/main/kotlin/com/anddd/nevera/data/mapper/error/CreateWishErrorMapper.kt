package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.wish.CreateWishError

private object CreateWishErrorCode {
    const val INVALID_INPUT = 3001
    const val MEMBER_NOT_FOUND = 2041
}

internal fun NetworkError.toCreateWishError(): CreateWishError = when (this) {
    is NetworkError.HttpError -> when (code) {
        CreateWishErrorCode.INVALID_INPUT -> CreateWishError.InvalidInput
        CreateWishErrorCode.MEMBER_NOT_FOUND -> CreateWishError.MemberNotFound
        else -> CreateWishError.Common(toCommonError())
    }
    else -> CreateWishError.Common(toCommonError())
}
