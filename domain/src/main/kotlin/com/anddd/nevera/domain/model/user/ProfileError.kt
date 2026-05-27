package com.anddd.nevera.domain.model.user

import com.anddd.nevera.domain.model.common.CommonError

sealed interface ProfileError {
    data object MemberNotFound : ProfileError
    data class Common(val error: CommonError) : ProfileError
}
