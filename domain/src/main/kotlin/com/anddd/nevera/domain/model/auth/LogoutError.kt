package com.anddd.nevera.domain.model.auth

import com.anddd.nevera.domain.model.common.CommonError

sealed interface LogoutError {
    data class TokenNotFound(val serverMessage: String?) : LogoutError
    data class Common(val error: CommonError) : LogoutError
}
