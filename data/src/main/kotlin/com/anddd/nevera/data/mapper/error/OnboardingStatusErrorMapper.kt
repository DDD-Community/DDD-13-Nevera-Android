package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.user.OnboardingStatusError

private object OnboardingStatusErrorCode {
    const val MEMBER_NOT_FOUND = 2041
}

internal fun NetworkError.toOnboardingStatusError(): OnboardingStatusError = when (this) {
    is NetworkError.HttpError -> when (code) {
        OnboardingStatusErrorCode.MEMBER_NOT_FOUND -> OnboardingStatusError.MemberNotFound
        else -> OnboardingStatusError.Common(toCommonError())
    }
    else -> OnboardingStatusError.Common(toCommonError())
}
