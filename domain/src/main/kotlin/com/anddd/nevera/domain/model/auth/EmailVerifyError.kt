package com.anddd.nevera.domain.model.auth

import com.anddd.nevera.domain.model.common.CommonError

sealed interface EmailVerifyError {
    data class InvalidCode(val serverMessage: String?) : EmailVerifyError
    data class ExpiredCode(val serverMessage: String?) : EmailVerifyError
    data class NotFound(val serverMessage: String?) : EmailVerifyError
    data class Common(val error: CommonError) : EmailVerifyError
}
