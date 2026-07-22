package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.common.CommonError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * 모든 기능별 에러 매퍼가 마지막에 위임하는 공통 변환을 고정한다.
 *
 * 여기가 바뀌면 앱 전체의 에러 표시가 한꺼번에 바뀌므로, 네 가지 [NetworkError]가
 * 각각 어떤 [CommonError]가 되는지를 명시적으로 못 박아 둔다.
 */
class CommonErrorMapperTest {

    @Test
    fun `HttpError는 서버 메시지를 담은 ServerError가 된다`() {
        val error: NetworkError = NetworkError.HttpError(code = 500, message = "서버 내부 오류")

        assertEquals(CommonError.ServerError("서버 내부 오류"), error.toCommonError())
    }

    @Test
    fun `메시지가 없는 HttpError는 메시지가 null인 ServerError가 된다`() {
        val error: NetworkError = NetworkError.HttpError(code = 500, message = null)

        assertEquals(CommonError.ServerError(null), error.toCommonError())
    }

    @Test
    fun `NetworkConnectionError는 NetworkUnavailable이 된다`() {
        val error: NetworkError = NetworkError.NetworkConnectionError()

        assertEquals(CommonError.NetworkUnavailable, error.toCommonError())
    }

    @Test
    fun `TimeoutError는 Timeout이 된다`() {
        val error: NetworkError = NetworkError.TimeoutError()

        assertEquals(CommonError.Timeout, error.toCommonError())
    }

    @Test
    fun `UnknownError는 Unknown이 된다`() {
        val error: NetworkError = NetworkError.UnknownError()

        assertEquals(CommonError.Unknown, error.toCommonError())
    }
}
