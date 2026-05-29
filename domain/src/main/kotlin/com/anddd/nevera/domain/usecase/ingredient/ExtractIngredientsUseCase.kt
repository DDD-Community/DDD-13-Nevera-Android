package com.anddd.nevera.domain.usecase.ingredient

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.ingredient.OcrExtractError
import com.anddd.nevera.domain.model.ingredient.OcrExtractEvent
import com.anddd.nevera.domain.model.ingredient.OcrProgressResult
import com.anddd.nevera.domain.repository.IngredientRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import javax.inject.Inject

class ExtractIngredientsUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository,
) {
    operator fun invoke(imageUri: String): Flow<OcrExtractEvent> = channelFlow {
        val jobId = when (val result = ingredientRepository.createOcrJob()) {
            is NeveraResult.Success -> result.data
            is NeveraResult.Failure -> {
                send(OcrExtractEvent.Failed(result.error))
                return@channelFlow
            }
        }

        val progressFailure = CompletableDeferred<OcrExtractError>()
        val progressJob = launch {
            ingredientRepository.observeOcrProgress(jobId).collect { event ->
                when (event) {
                    OcrProgressResult.Opened -> Unit

                    is OcrProgressResult.Progress -> when (val result = event.result) {
                        is NeveraResult.Success ->
                            send(OcrExtractEvent.Progress(result.data))

                        is NeveraResult.Failure ->
                            progressFailure.complete(result.error)
                    }
                }
            }
        }

        val extraction = async {
            ingredientRepository.extractIngredients(jobId, imageUri)
        }

        select {
            progressFailure.onAwait { error ->
                extraction.cancelAndJoin()
                progressJob.cancelAndJoin()
                send(OcrExtractEvent.Failed(error))
            }

            extraction.onAwait { result ->
                progressJob.cancelAndJoin()
                when (result) {
                    is NeveraResult.Success -> {
                        send(OcrExtractEvent.Progress(100))
                        send(OcrExtractEvent.Completed(result.data))
                    }

                    is NeveraResult.Failure ->
                        send(OcrExtractEvent.Failed(result.error))
                }
            }
        }
    }
}
