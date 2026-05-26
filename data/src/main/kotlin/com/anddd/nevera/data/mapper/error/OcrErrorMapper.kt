package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.ingredient.OcrExtractError

private object OcrErrorCode {
    const val PROCESS_ERROR = 3001
    const val GOOGLE_VISION_API_ERROR = 3003
    const val EMPTY_IMAGE_FILE = 3004
}

internal fun NetworkError.toOcrExtractError(): OcrExtractError = when (this) {
    is NetworkError.HttpError -> when (code) {
        OcrErrorCode.EMPTY_IMAGE_FILE -> OcrExtractError.EmptyImageFile
        OcrErrorCode.PROCESS_ERROR -> OcrExtractError.OcrProcessFailed
        OcrErrorCode.GOOGLE_VISION_API_ERROR -> OcrExtractError.GoogleVisionApiFailed
        else -> OcrExtractError.Common(toCommonError())
    }
    else -> OcrExtractError.Common(toCommonError())
}
