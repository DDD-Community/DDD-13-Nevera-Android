package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.data.testutil.httpError
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.ingredient.EditIngredientError
import com.anddd.nevera.domain.model.ingredient.OcrExtractError
import com.anddd.nevera.domain.model.ingredient.RegisterIngredientError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * 식재료 수정·등록·OCR 추출의 서버 에러 코드 매핑을 고정한다.
 *
 * OCR은 코드가 일곱 개이고 6xxx(OCR)와 5xxx(LLM) 두 계열이 섞여 있어,
 * 하나씩 옮겨 적는 과정에서 값이 어긋나기 쉬운 지점이다.
 */
class IngredientErrorMapperTest {

    @Test
    fun `수정 - 서버 코드 4001은 InventoryNotFound가 된다`() {
        assertEquals(EditIngredientError.InventoryNotFound, httpError(4001).toEditIngredientError())
    }

    @Test
    fun `수정 - 서버 코드 4002는 InventoryForbidden이 된다`() {
        assertEquals(EditIngredientError.InventoryForbidden, httpError(4002).toEditIngredientError())
    }

    @Test
    fun `수정 - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            EditIngredientError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toEditIngredientError(),
        )
    }

    @Test
    fun `수정 - 네트워크 연결 실패는 공통 에러로 감싼다`() {
        assertEquals(
            EditIngredientError.Common(CommonError.NetworkUnavailable),
            NetworkError.NetworkConnectionError().toEditIngredientError(),
        )
    }

    @Test
    fun `등록 - 서버 코드 2041은 MemberNotFound가 된다`() {
        assertEquals(RegisterIngredientError.MemberNotFound, httpError(2041).toRegisterIngredientError())
    }

    @Test
    fun `등록 - 서버 코드 3003은 MaxItemsExceeded가 된다`() {
        assertEquals(RegisterIngredientError.MaxItemsExceeded, httpError(3003).toRegisterIngredientError())
    }

    @Test
    fun `등록 - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            RegisterIngredientError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toRegisterIngredientError(),
        )
    }

    @Test
    fun `OCR - 서버 코드 6001은 OcrProcessFailed가 된다`() {
        assertEquals(OcrExtractError.OcrProcessFailed, httpError(6001).toOcrExtractError())
    }

    @Test
    fun `OCR - 서버 코드 6002는 InvalidImageFormat이 된다`() {
        assertEquals(OcrExtractError.InvalidImageFormat, httpError(6002).toOcrExtractError())
    }

    @Test
    fun `OCR - 서버 코드 6003은 GoogleVisionApiFailed가 된다`() {
        assertEquals(OcrExtractError.GoogleVisionApiFailed, httpError(6003).toOcrExtractError())
    }

    @Test
    fun `OCR - 서버 코드 6004는 EmptyImageFile이 된다`() {
        assertEquals(OcrExtractError.EmptyImageFile, httpError(6004).toOcrExtractError())
    }

    @Test
    fun `OCR - 서버 코드 6005는 FileSizeExceeded가 된다`() {
        assertEquals(OcrExtractError.FileSizeExceeded, httpError(6005).toOcrExtractError())
    }

    @Test
    fun `OCR - 서버 코드 5001은 LlmParseError가 된다`() {
        assertEquals(OcrExtractError.LlmParseError, httpError(5001).toOcrExtractError())
    }

    @Test
    fun `OCR - 서버 코드 5002는 LlmGenerateError가 된다`() {
        assertEquals(OcrExtractError.LlmGenerateError, httpError(5002).toOcrExtractError())
    }

    @Test
    fun `OCR - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            OcrExtractError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toOcrExtractError(),
        )
    }

    @Test
    fun `OCR - 타임아웃은 공통 에러로 감싼다`() {
        assertEquals(
            OcrExtractError.Common(CommonError.Timeout),
            NetworkError.TimeoutError().toOcrExtractError(),
        )
    }
}
