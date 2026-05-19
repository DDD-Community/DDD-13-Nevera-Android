package com.anddd.nevera.domain.model.auth

import com.anddd.nevera.domain.model.common.CommonError

sealed interface GoogleLoginError {
    data class InvalidToken(val serverMessage: String?) : GoogleLoginError
    data class Common(val error: CommonError) : GoogleLoginError
}
