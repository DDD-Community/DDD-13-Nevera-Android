package com.anddd.nevera.domain.model.user

import com.anddd.nevera.domain.model.common.CommonError

sealed interface OnboardingStatusError {
    data object MemberNotFound : OnboardingStatusError
    data class Common(val error: CommonError) : OnboardingStatusError
}
