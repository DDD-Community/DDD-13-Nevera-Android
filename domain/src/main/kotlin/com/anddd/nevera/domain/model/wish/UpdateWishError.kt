package com.anddd.nevera.domain.model.wish

import com.anddd.nevera.domain.model.common.CommonError

sealed interface UpdateWishError {
    data object InvalidInput : UpdateWishError
    data object WishNotFound : UpdateWishError
    data object WishForbidden : UpdateWishError
    data object WishAlreadyAchieved : UpdateWishError
    data class Common(val error: CommonError) : UpdateWishError
}
