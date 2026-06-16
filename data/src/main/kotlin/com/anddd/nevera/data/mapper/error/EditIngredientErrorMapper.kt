package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.ingredient.EditIngredientError
import timber.log.Timber

private object EditIngredientErrorCode {
    const val INVENTORY_NOT_FOUND = 4001
    const val INVENTORY_FORBIDDEN = 4002
}

internal fun NetworkError.toEditIngredientError(): EditIngredientError = when (this) {
    is NetworkError.HttpError -> when (code) {
        EditIngredientErrorCode.INVENTORY_NOT_FOUND -> EditIngredientError.InventoryNotFound
        EditIngredientErrorCode.INVENTORY_FORBIDDEN -> EditIngredientError.InventoryForbidden
        else -> EditIngredientError.Common(toCommonError())
    }
    else -> EditIngredientError.Common(toCommonError())
}.also {
    Timber.d("EditIngredientError[code=$code]: $message")
}
