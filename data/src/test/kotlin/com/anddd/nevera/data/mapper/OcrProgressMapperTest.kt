package com.anddd.nevera.data.mapper

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.network.model.ApiError
import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.ingredient.OcrProgressDto
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.ingredient.OcrExtractError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * OCR 진행률 스트림 응답을 성공·실패로 나누는 규칙을 고정한다.
 *
 * 이 응답은 실시간 스트림으로 들어오므로 화면에서 재현하기 어렵다.
 */
class OcrProgressMapperTest {

    @Test
    fun `진행률이 있으면 성공으로 변환된다`() {
        val response = ApiResponse(result = OcrProgressDto(result = "PROCESSING", progress = 42), error = null)

        assertEquals(NeveraResult.Success(42), response.toProgressResult())
    }

    @Test
    fun `진행률 0도 정상 값으로 취급한다`() {
        val response = ApiResponse(result = OcrProgressDto(result = "PROCESSING", progress = 0), error = null)

        assertEquals(NeveraResult.Success(0), response.toProgressResult())
    }

    @Test
    fun `결과가 없고 에러 코드가 있으면 해당 OCR 에러로 변환된다`() {
        val response = ApiResponse<OcrProgressDto>(
            result = null,
            error = ApiError(code = 6003, message = "Vision API 오류"),
        )

        assertEquals(NeveraResult.Failure(OcrExtractError.GoogleVisionApiFailed), response.toProgressResult())
    }

    @Test
    fun `결과가 없고 알 수 없는 에러 코드면 공통 에러로 감싼다`() {
        val response = ApiResponse<OcrProgressDto>(
            result = null,
            error = ApiError(code = 9999, message = "알 수 없는 오류"),
        )

        assertEquals(
            NeveraResult.Failure(OcrExtractError.Common(CommonError.ServerError("알 수 없는 오류"))),
            response.toProgressResult(),
        )
    }

    @Test
    fun `에러에 코드가 없으면 코드를 -1로 채워 공통 에러로 감싼다`() {
        val response = ApiResponse<OcrProgressDto>(
            result = null,
            error = ApiError(code = null, message = "코드 없는 오류"),
        )

        assertEquals(
            NeveraResult.Failure(OcrExtractError.Common(CommonError.ServerError("코드 없는 오류"))),
            response.toProgressResult(),
        )
    }

    @Test
    fun `결과도 에러도 없으면 Unknown 공통 에러로 변환된다`() {
        val response = ApiResponse<OcrProgressDto>(result = null, error = null)

        assertEquals(
            NeveraResult.Failure(OcrExtractError.Common(CommonError.Unknown)),
            response.toProgressResult(),
        )
    }
}
