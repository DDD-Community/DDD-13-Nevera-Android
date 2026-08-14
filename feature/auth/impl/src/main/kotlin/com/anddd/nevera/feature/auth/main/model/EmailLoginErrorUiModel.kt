package com.anddd.nevera.feature.auth.main.model

import com.anddd.nevera.domain.model.auth.LoginError
import com.anddd.nevera.domain.model.common.CommonError

sealed interface EmailLoginErrorUiModel {
    val message: String

    data object InvalidCredentials : EmailLoginErrorUiModel {
        override val message: String = "이메일 또는 비밀번호가 틀렸습니다."
    }

    data object NetworkUnavailable : EmailLoginErrorUiModel {
        override val message: String = "네트워크 연결을 확인해주세요."
    }

    data object Timeout : EmailLoginErrorUiModel {
        override val message: String = "요청 시간이 초과됐습니다."
    }

    data object Unauthorized : EmailLoginErrorUiModel {
        override val message: String = "다시 로그인해주세요."
    }

    data class ServerError(
        override val message: String = "로그인에 실패했습니다.",
    ) : EmailLoginErrorUiModel

    data object Unknown : EmailLoginErrorUiModel {
        override val message: String = "로그인에 실패했습니다."
    }
}

internal fun LoginError.toUiModel(): EmailLoginErrorUiModel = when (this) {
    LoginError.InvalidCredentials -> EmailLoginErrorUiModel.InvalidCredentials
    is LoginError.Common -> when (val error = this.error) {
        CommonError.NetworkUnavailable -> EmailLoginErrorUiModel.NetworkUnavailable
        CommonError.Timeout -> EmailLoginErrorUiModel.Timeout
        CommonError.Unauthorized -> EmailLoginErrorUiModel.Unauthorized
        is CommonError.ServerError -> EmailLoginErrorUiModel.ServerError(error.message ?: "로그인에 실패했습니다.")
        CommonError.Unknown -> EmailLoginErrorUiModel.Unknown
    }
}
