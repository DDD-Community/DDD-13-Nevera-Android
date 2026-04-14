package com.anddd.nevera.domain.model.common

/**
 * 여러 기능에서 공통으로 발생할 수 있는 에러.
 *
 * 판단 기준: "HTTP/소켓이 없어도 이 개념이 의미 있는가?"
 * - NetworkUnavailable, Timeout, Unauthorized 는 통신 수단과 무관한 개념이므로 domain에 둔다.
 * - HttpError(code), SocketTimeoutException 처럼 특정 기술에 종속된 표현은 data 레이어에서 변환한다.
 */
sealed interface CommonError {
    data object NetworkUnavailable : CommonError
    data object Timeout : CommonError
    data object Unauthorized : CommonError
    data class ServerError(val message: String?) : CommonError
    data object Unknown : CommonError
}