package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.ingredient.OcrIngredientDto
import com.anddd.nevera.data.model.ingredient.OcrJobResponse

internal interface OcrDataSource {

    suspend fun createOcrJob(): ApiResponse<OcrJobResponse>

    suspend fun extractIngredients(
        jobId: String,
        imageUri: String,
    ): ApiResponse<List<OcrIngredientDto>>
}
