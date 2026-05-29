package com.anddd.nevera.data.mapper

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.mapper.error.toOcrExtractError
import com.anddd.nevera.data.model.ingredient.OcrProgressDto
import com.anddd.nevera.domain.model.ingredient.OcrExtractError

internal fun ApiResponse<OcrProgressDto>.toProgressResult(): NeveraResult<Int, OcrExtractError> {
    val progress = result?.progress
    if (progress != null) {
        return NeveraResult.Success(progress)
    }

    val networkError = error?.let {
        NetworkError.HttpError(
            code = it.code ?: -1,
            message = it.message,
        )
    } ?: NetworkError.UnknownError(message = "Empty OCR progress result")

    return NeveraResult.Failure(networkError.toOcrExtractError())
}
