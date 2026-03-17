package com.anddd.nevera.core.common

sealed interface NetworkError {
    val code: Int?
    val message: String?

    /**
     * Server Status Code 참조
     */
    data class HttpError(
        override val code: Int,
        override val message: String? = null
    ) : NetworkError

    data class NetworkConnectionError(
        override val code: Int? = null,
        override val message: String? = "네트워크 연결 오류"
    ) : NetworkError

    data class TimeoutError(
        override val code: Int? = null,
        override val message: String? = "요청 시간이 초과되었습니다."
    ) : NetworkError

    data class SerializationError(
        override val code: Int? = null,
        override val message: String? = "응답 파싱에 실패했습니다."
    ) : NetworkError

    data class UnknownError(
        override val code: Int? = null,
        override val message: String? = "알 수 없는 오류가 발생했습니다.",
        val throwable: Throwable? = null
    ) : NetworkError
}