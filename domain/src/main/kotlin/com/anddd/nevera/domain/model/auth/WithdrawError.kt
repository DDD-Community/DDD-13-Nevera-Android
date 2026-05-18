package com.anddd.nevera.domain.model.auth

import com.anddd.nevera.domain.model.common.CommonError

sealed interface WithdrawError {
    /** 토큰이 유효하지 않거나 이미 탈퇴된 회원 (401, 404) → 로그인 화면으로 이동 */
    data object SessionInvalid : WithdrawError

    /** 인프라/공통 에러 래핑 */
    data class Common(val error: CommonError) : WithdrawError
}
