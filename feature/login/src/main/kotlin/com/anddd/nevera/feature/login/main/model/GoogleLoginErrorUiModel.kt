package com.anddd.nevera.feature.login.main.model

import com.anddd.nevera.core.common.NetworkError

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

internal fun NetworkError.toUiModel(): GoogleLoginErrorUiModel = when (this) {
    is NetworkError.HttpError -> GoogleLoginErrorUiModel.ServerError(message ?: "SNS 로그인에 실패했습니다.")
    is NetworkError.NetworkConnectionError -> GoogleLoginErrorUiModel.NetworkUnavailable
    is NetworkError.TimeoutError -> GoogleLoginErrorUiModel.Timeout
    is NetworkError.UnknownError -> GoogleLoginErrorUiModel.Unknown
}
