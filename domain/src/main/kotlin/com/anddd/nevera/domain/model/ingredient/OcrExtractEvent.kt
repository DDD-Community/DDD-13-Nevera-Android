package com.anddd.nevera.domain.model.ingredient

sealed interface OcrExtractEvent {
    data class Progress(val percent: Int) : OcrExtractEvent
    data class Completed(val items: List<OcrIngredient>) : OcrExtractEvent
    data class Failed(val error: OcrExtractError) : OcrExtractEvent
}
