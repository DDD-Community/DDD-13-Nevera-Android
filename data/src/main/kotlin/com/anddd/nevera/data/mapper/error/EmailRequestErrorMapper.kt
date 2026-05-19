package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.auth.EmailRequestError

internal fun NetworkError.toEmailRequestError(): EmailRequestError = when (this) {
    is NetworkError.HttpError -> when (code) {
        2006 -> EmailRequestError.DuplicateEmail(serverMessage = message)
        2007 -> EmailRequestError.MailSendError(serverMessage = message)
        else -> EmailRequestError.Common(toCommonError())
    }
    else -> EmailRequestError.Common(toCommonError())
}
