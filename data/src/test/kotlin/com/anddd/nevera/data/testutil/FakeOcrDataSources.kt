package com.anddd.nevera.data.testutil

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.datasource.OcrDataSource
import com.anddd.nevera.data.datasource.OcrProgressDataSource
import com.anddd.nevera.data.datasource.OcrProgressResponse
import com.anddd.nevera.data.model.ingredient.OcrIngredientDto
import com.anddd.nevera.data.model.ingredient.OcrJobResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * OCR 관련 DataSource 대역.
 *
 * 두 대역은 크기가 작고 저장소 테스트에서 항상 함께 주입되므로 한 파일에 둔다.
 * 현재 저장소 테스트는 OCR 경로를 검증하지 않으므로, 호출되면 어떤 메서드가
 * 준비되지 않았는지 알려주는 예외를 던진다.
 */
internal class FakeOcrDataSource : OcrDataSource {
    override suspend fun createOcrJob(): ApiResponse<OcrJobResponse> =
        throw UnsupportedOperationException("FakeOcrDataSource.createOcrJob 은 이 테스트에서 준비되지 않았다")

    override suspend fun extractIngredients(
        jobId: String,
        imageUri: String,
    ): ApiResponse<List<OcrIngredientDto>> =
        throw UnsupportedOperationException("FakeOcrDataSource.extractIngredients 은 이 테스트에서 준비되지 않았다")
}

internal class FakeOcrProgressDataSource : OcrProgressDataSource {
    override fun observeOcrProgress(jobId: String): Flow<OcrProgressResponse> = emptyFlow()
}
