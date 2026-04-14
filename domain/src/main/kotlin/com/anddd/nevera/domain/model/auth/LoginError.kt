package com.anddd.nevera.domain.model.auth

import com.anddd.nevera.domain.model.common.CommonError

sealed interface LoginError {
    /** 이메일 또는 비밀번호가 일치하지 않음 */
    data object InvalidCredentials : LoginError

    /** 인프라/공통 에러 래핑 */
    data class Common(val error: CommonError) : LoginError
}