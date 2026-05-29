package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.ingredient.RegisterIngredientError
import timber.log.Timber

private object RegisterErrorCode {
    const val MEMBER_NOT_FOUND = 2041
    const val MAX_ITEMS_EXCEEDED = 3003
}

internal fun NetworkError.toRegisterIngredientError(): RegisterIngredientError = when (this) {
    is NetworkError.HttpError -> when (code) {
        RegisterErrorCode.MEMBER_NOT_FOUND -> RegisterIngredientError.MemberNotFound
        RegisterErrorCode.MAX_ITEMS_EXCEEDED -> RegisterIngredientError.MaxItemsExceeded
        else -> RegisterIngredientError.Common(toCommonError())
    }
    else -> RegisterIngredientError.Common(toCommonError())
}.also {
    Timber.d("RegisterIngredientError[code=$code]: $message")
}
