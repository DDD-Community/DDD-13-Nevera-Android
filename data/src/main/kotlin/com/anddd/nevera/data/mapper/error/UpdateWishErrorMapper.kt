package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.wish.UpdateWishError

private object UpdateWishErrorCode {
    const val INVALID_INPUT = 3001
    const val WISH_NOT_FOUND = 4051
    const val WISH_FORBIDDEN = 4052
    const val WISH_ALREADY_ACHIEVED = 4053
}

internal fun NetworkError.toUpdateWishError(): UpdateWishError = when (this) {
    is NetworkError.HttpError -> when (code) {
        UpdateWishErrorCode.INVALID_INPUT -> UpdateWishError.InvalidInput
        UpdateWishErrorCode.WISH_NOT_FOUND -> UpdateWishError.WishNotFound
        UpdateWishErrorCode.WISH_FORBIDDEN -> UpdateWishError.WishForbidden
        UpdateWishErrorCode.WISH_ALREADY_ACHIEVED -> UpdateWishError.WishAlreadyAchieved
        else -> UpdateWishError.Common(toCommonError())
    }
    else -> UpdateWishError.Common(toCommonError())
}
