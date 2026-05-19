package com.anddd.nevera.domain.model.auth

import com.anddd.nevera.domain.model.common.CommonError

sealed interface SignupError {
    data class UnverifiedEmail(val serverMessage: String?) : SignupError
    data class NotFound(val serverMessage: String?) : SignupError
    data class Common(val error: CommonError) : SignupError
}
