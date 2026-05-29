package com.anddd.nevera.feature.ingredient.main

import com.anddd.nevera.domain.model.ingredient.OcrExtractEvent
import com.anddd.nevera.domain.model.ingredient.OcrIngredient
import com.anddd.nevera.domain.usecase.ingredient.ExtractIngredientsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class OcrScanner @Inject constructor(
    private val extractIngredientsUseCase: ExtractIngredientsUseCase,
    private val presentationTimer: OcrScanPresentationTimer,
) {
    fun scan(imageUri: String): Flow<OcrScanEvent> {
        val startedAt = presentationTimer.startedAt()
        return extractIngredientsUseCase(imageUri).transform { event ->
            when (event) {
                is OcrExtractEvent.Progress ->
                    emit(OcrScanEvent.Progress(event.percent.toProgressValue()))

                is OcrExtractEvent.Completed -> {
                    presentationTimer.delayUntilMinimumDuration(startedAt)
                    emit(
                        if (event.items.isEmpty()) OcrScanEvent.Failed
                        else OcrScanEvent.Completed(event.items)
                    )
                }

                is OcrExtractEvent.Failed -> {
                    presentationTimer.delayUntilMinimumDuration(startedAt)
                    emit(OcrScanEvent.Failed)
                }
            }
        }
    }
}

private fun Int.toProgressValue(): Float =
    (this / 100f).coerceIn(0f, 1f)

sealed interface OcrScanEvent {
    data class Progress(val value: Float) : OcrScanEvent
    data class Completed(val items: List<OcrIngredient>) : OcrScanEvent
    data object Failed : OcrScanEvent
}
