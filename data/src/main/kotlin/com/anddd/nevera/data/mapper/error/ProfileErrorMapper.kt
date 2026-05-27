package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.user.ProfileError

private object ProfileErrorCode {
    const val MEMBER_NOT_FOUND = 2041
}

internal fun NetworkError.toProfileError(): ProfileError = when (this) {
    is NetworkError.HttpError -> when (code) {
        ProfileErrorCode.MEMBER_NOT_FOUND -> ProfileError.MemberNotFound
        else -> ProfileError.Common(toCommonError())
    }
    else -> ProfileError.Common(toCommonError())
}
