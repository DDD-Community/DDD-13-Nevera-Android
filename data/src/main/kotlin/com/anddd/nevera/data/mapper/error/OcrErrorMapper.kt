package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.ingredient.OcrExtractError
import timber.log.Timber

private object OcrErrorCode {
    // OCR 에러 (6xxx)
    const val PROCESS_ERROR = 6001
    const val INVALID_IMAGE_FORMAT = 6002
    const val GOOGLE_VISION_API_ERROR = 6003
    const val EMPTY_IMAGE_FILE = 6004
    const val FILE_SIZE_EXCEEDED = 6005

    // LLM 에러 (5xxx)
    const val LLM_PARSE_ERROR = 5001
    const val LLM_GENERATE_ERROR = 5002
}

internal fun NetworkError.toOcrExtractError(): OcrExtractError = when (this) {
    is NetworkError.HttpError -> when (code) {
        OcrErrorCode.EMPTY_IMAGE_FILE -> OcrExtractError.EmptyImageFile
        OcrErrorCode.PROCESS_ERROR -> OcrExtractError.OcrProcessFailed
        OcrErrorCode.GOOGLE_VISION_API_ERROR -> OcrExtractError.GoogleVisionApiFailed
        OcrErrorCode.FILE_SIZE_EXCEEDED -> OcrExtractError.FileSizeExceeded
        OcrErrorCode.INVALID_IMAGE_FORMAT -> OcrExtractError.InvalidImageFormat
        OcrErrorCode.LLM_GENERATE_ERROR -> OcrExtractError.LlmGenerateError
        OcrErrorCode.LLM_PARSE_ERROR -> OcrExtractError.LlmParseError
        else -> OcrExtractError.Common(toCommonError())
    }
    else -> OcrExtractError.Common(toCommonError())
}.also { mapped ->
    Timber.d("OcrExtractError[code=$code]: $message")
}
