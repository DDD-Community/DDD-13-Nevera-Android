package com.anddd.nevera.feature.auth.main.model

import com.anddd.nevera.domain.model.auth.GoogleLoginError
import com.anddd.nevera.domain.model.common.CommonError

sealed interface GoogleLoginErrorUiModel {
    val message: String

    data object NetworkUnavailable : GoogleLoginErrorUiModel {
        override val message: String = "네트워크 연결을 확인해주세요."
    }

    data object Timeout : GoogleLoginErrorUiModel {
        override val message: String = "요청 시간이 초과됐습니다."
    }

    data class ServerError(
        override val message: String = "SNS 로그인에 실패했습니다.",
    ) : GoogleLoginErrorUiModel

    data object Unknown : GoogleLoginErrorUiModel {
        override val message: String = "SNS 로그인에 실패했습니다."
    }
}

internal fun GoogleLoginError.toUiModel(): GoogleLoginErrorUiModel = when (this) {
    is GoogleLoginError.InvalidToken ->
        GoogleLoginErrorUiModel.ServerError(serverMessage ?: "유효하지 않은 Google 토큰입니다.")
    is GoogleLoginError.Common -> when (val e = error) {
        CommonError.NetworkUnavailable -> GoogleLoginErrorUiModel.NetworkUnavailable
        CommonError.Timeout -> GoogleLoginErrorUiModel.Timeout
        CommonError.Unauthorized -> GoogleLoginErrorUiModel.ServerError("다시 로그인해주세요.")
        is CommonError.ServerError -> GoogleLoginErrorUiModel.ServerError(e.message ?: "SNS 로그인에 실패했습니다.")
        CommonError.Unknown -> GoogleLoginErrorUiModel.Unknown
    }
}
