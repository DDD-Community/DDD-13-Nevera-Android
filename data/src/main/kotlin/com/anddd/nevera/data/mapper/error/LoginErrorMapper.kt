package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.auth.LoginError
import com.anddd.nevera.domain.model.common.CommonError


// TODO: 서버 비즈니스 에러 코드 확정 후 InvalidCredentials 매핑 추가
//       e.g. is NetworkError.HttpError -> if (code == SERVER_CODE_INVALID_CREDENTIALS) LoginError.InvalidCredentials
internal fun NetworkError.toLoginError(): LoginError = when (this) {
    is NetworkError.HttpError -> when (code) {
        401 -> LoginError.Common(CommonError.Unauthorized)
        else -> LoginError.Common(toCommonError())
    }
    else -> LoginError.Common(toCommonError())
}
