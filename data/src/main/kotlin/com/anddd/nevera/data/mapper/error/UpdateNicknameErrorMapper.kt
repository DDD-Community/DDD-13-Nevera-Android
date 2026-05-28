package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.user.UpdateNicknameError

private object UpdateNicknameErrorCode {
    const val INVALID_NICKNAME = 3001
}

internal fun NetworkError.toUpdateNicknameError(): UpdateNicknameError = when (this) {
    is NetworkError.HttpError -> when (code) {
        UpdateNicknameErrorCode.INVALID_NICKNAME -> UpdateNicknameError.InvalidNickname
        else -> UpdateNicknameError.Common(toCommonError())
    }
    else -> UpdateNicknameError.Common(toCommonError())
}
