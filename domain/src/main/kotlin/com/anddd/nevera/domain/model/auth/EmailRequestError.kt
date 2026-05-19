package com.anddd.nevera.domain.model.auth

import com.anddd.nevera.domain.model.common.CommonError

sealed interface EmailRequestError {
    data class DuplicateEmail(val serverMessage: String?) : EmailRequestError
    data class MailSendError(val serverMessage: String?) : EmailRequestError
    data class Common(val error: CommonError) : EmailRequestError
}
