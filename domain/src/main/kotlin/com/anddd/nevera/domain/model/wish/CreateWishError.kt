package com.anddd.nevera.domain.model.wish

import com.anddd.nevera.domain.model.common.CommonError

sealed interface CreateWishError {
    data object InvalidInput : CreateWishError
    data object MemberNotFound : CreateWishError
    data class Common(val error: CommonError) : CreateWishError
}
