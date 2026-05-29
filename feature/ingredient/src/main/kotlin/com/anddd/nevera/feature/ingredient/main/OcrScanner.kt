package com.anddd.nevera.feature.ingredient.main

import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.domain.model.ingredient.OcrIngredient
import com.anddd.nevera.domain.usecase.ingredient.ExtractIngredientsUseCase
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * OCR 스캔의 진행률 시뮬레이션과 API 호출을 하나의 Flow로 조율하는 feature 레이어 클래스
 *
 * - 진행률 시뮬레이션(UI 관심사)과 API 호출이 [channelFlow] 안에서 병렬 처리됨
 * - ViewModel은 반환된 [Flow]를 collect하여 Mutation / SideEffect만 발행
 */
class OcrScanner @Inject constructor(
    private val extractIngredientsUseCase: ExtractIngredientsUseCase,
) {
    fun scan(imageUri: String): Flow<OcrScanEvent> = channelFlow {
        // 진행률 시뮬레이션 — API 호출과 병렬로 실행
        val progressJob = launch {
            repeat(18) { i ->
                delay(200L)
                send(OcrScanEvent.Progress((i + 1) / 20f))
            }
        }

        extractIngredientsUseCase(imageUri).apply {
            progressJob.cancelAndJoin()
        }.onSuccess { ingredients ->
            send(OcrScanEvent.Progress(1f))
            delay(300L)
            if (ingredients.isEmpty()) {
                send(OcrScanEvent.Failed)
            } else {
                send(OcrScanEvent.Completed(ingredients))
            }
        }.onFailure {
            send(OcrScanEvent.Failed)
        }
    }
}

sealed interface OcrScanEvent {
    data class Progress(val value: Float) : OcrScanEvent
    data class Completed(val items: List<OcrIngredient>) : OcrScanEvent
    data object Failed : OcrScanEvent
}
