package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.ingredient.OcrProgressDto

internal sealed interface OcrProgressResponse {
    data object Opened : OcrProgressResponse
    data class Progress(val response: ApiResponse<OcrProgressDto>) : OcrProgressResponse
}
