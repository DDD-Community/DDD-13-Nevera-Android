package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.ingredient.OcrIngredientDto
import com.anddd.nevera.data.model.ingredient.OcrJobResponse
import kotlinx.coroutines.flow.Flow

internal interface OcrDataSource {

    suspend fun createOcrJob(): ApiResponse<OcrJobResponse>

    fun observeOcrProgress(jobId: String): Flow<OcrProgressResponse>

    suspend fun extractIngredients(
        jobId: String,
        imageUri: String,
    ): ApiResponse<List<OcrIngredientDto>>
}
