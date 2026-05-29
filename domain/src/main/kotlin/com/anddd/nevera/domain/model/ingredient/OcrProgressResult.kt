package com.anddd.nevera.domain.model.ingredient

import com.anddd.nevera.core.common.NeveraResult

sealed interface OcrProgressResult {
    data object Opened : OcrProgressResult
    data class Progress(val result: NeveraResult<Int, OcrExtractError>) : OcrProgressResult
}
