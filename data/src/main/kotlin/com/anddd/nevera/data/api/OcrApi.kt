package com.anddd.nevera.data.api

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.ingredient.OcrIngredientDto
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * OCR 이미지 분석 API
 *
 * ⚠️ 실제 서버 엔드포인트 및 HTTP 메서드(GET/POST)는 명세 확인 후 조정하세요.
 *    Multipart body 전송은 POST가 HTTP 표준이므로 현재 POST로 구현합니다.
 */
internal interface OcrApi {

    /**
     * 영수증 / 이커머스 이미지 OCR 분석
     *
     * Request  : form-data, key = "image", value = 이미지 파일
     * Response : ApiResponse<List<OcrIngredientDto>>
     */
    @Multipart
    @POST("api/v1/ocr/extract")
    suspend fun extractIngredients(
        @Part image: MultipartBody.Part,
    ): ApiResponse<List<OcrIngredientDto>>
}
