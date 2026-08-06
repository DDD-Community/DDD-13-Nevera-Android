package com.anddd.nevera.feature.ingredient.main

import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.domain.usecase.ingredient.RegisterIngredientsUseCase
import com.anddd.nevera.feature.ingredient.main.model.IngredientIntent
import com.anddd.nevera.feature.ingredient.main.model.IngredientMutation
import com.anddd.nevera.feature.ingredient.main.model.IngredientMutation.AllSelectionToggled
import com.anddd.nevera.feature.ingredient.main.model.IngredientMutation.EmptyItemAdded
import com.anddd.nevera.feature.ingredient.main.model.IngredientMutation.ImageUriLoaded
import com.anddd.nevera.feature.ingredient.main.model.IngredientMutation.ItemUpdated
import com.anddd.nevera.feature.ingredient.main.model.IngredientMutation.ProgressUpdated
import com.anddd.nevera.feature.ingredient.main.model.IngredientMutation.RegisterFailed
import com.anddd.nevera.feature.ingredient.main.model.IngredientMutation.RegisterStarted
import com.anddd.nevera.feature.ingredient.main.model.IngredientMutation.ScanCompleted
import com.anddd.nevera.feature.ingredient.main.model.IngredientMutation.ScanFailed
import com.anddd.nevera.feature.ingredient.main.model.IngredientMutation.SetScrollTarget
import com.anddd.nevera.feature.ingredient.main.model.IngredientMutation.ClearScrollTarget
import com.anddd.nevera.feature.ingredient.main.model.IngredientPhase
import com.anddd.nevera.feature.ingredient.main.model.IngredientSideEffect
import com.anddd.nevera.feature.ingredient.main.model.IngredientUiModel
import com.anddd.nevera.feature.ingredient.main.model.IngredientUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import org.orbitmvi.orbit.syntax.Syntax

@HiltViewModel(assistedFactory = IngredientViewModel.Factory::class)
class IngredientViewModel @AssistedInject constructor(
    @Assisted private val imageUri: String,
    private val ocrScanner: OcrScanner,
    private val registerIngredientsUseCase: RegisterIngredientsUseCase,
) : NeveraViewModel<IngredientUiState, IngredientSideEffect, IngredientIntent, IngredientMutation>(
    IngredientUiState()
) {


    private var scanJob: Job? = null

    init {
        intent { applyMutation(ImageUriLoaded(imageUri)) }
        handleIntent(IngredientIntent.StartScan)
    }

    override fun handleIntent(intent: IngredientIntent) {
        when (intent) {
            IngredientIntent.StartScan -> startScan()
            IngredientIntent.CancelScan -> cancelScan()
            is IngredientIntent.UpdateItem -> updateItem(intent.item)
            is IngredientIntent.ToggleAllSelection -> toggleAllSelection(intent.selectAll)
            IngredientIntent.AddEmptyItem -> addEmptyItem()
            IngredientIntent.Register -> register()
            IngredientIntent.ImageClick -> navigateToPhotoDetail()
            IngredientIntent.ScrollHandled -> clearScrollTarget()
        }
    }

    // ── 스캔 시작 ──────────────────────────────────────────────────────────────
    private fun startScan() {
        scanJob?.cancel()
        scanJob = intent {
            ocrScanner.scan(imageUri).collect { event ->
                when (event) {
                    is OcrScanEvent.Progress -> applyMutation(ProgressUpdated(event.value))
                    is OcrScanEvent.Completed -> applyMutation(ScanCompleted(event.items.toUiModels()))
                    OcrScanEvent.Failed -> {
                        applyMutation(ScanFailed)
                        postSideEffect(IngredientSideEffect.NavigateToOcrError)
                    }
                }
            }
        }
    }

    // ── 스캔 취소 ──────────────────────────────────────────────────────────────
    private fun cancelScan() {
        scanJob?.cancel()
        intent { postSideEffect(IngredientSideEffect.NavigateBack) }
    }

    // ── 아이템 업데이트 ────────────────────────────────────────────────────────
    private fun updateItem(item: IngredientUiModel) = intent {
        applyMutation(ItemUpdated(item))
    }

    // ── 전체 선택 / 해제 ───────────────────────────────────────────────────────
    private fun toggleAllSelection(selectAll: Boolean) = intent {
        applyMutation(AllSelectionToggled(selectAll))
    }

    // ── 사진 상세 화면 이동 ────────────────────────────────────────────────────
    private fun navigateToPhotoDetail() = intent {
        postSideEffect(IngredientSideEffect.NavigateToPhotoDetail(state.imageUri))
    }

    // ── 빈 아이템 추가 ─────────────────────────────────────────────────────────
    private fun addEmptyItem() = intent {
        val newItemIndex = state.items.size // 추가 전 크기 = 새 아이템의 items 리스트 0-based 인덱스
        applyMutation(EmptyItemAdded)
        applyMutation(SetScrollTarget(newItemIndex))
    }

    private fun clearScrollTarget() = intent {
        applyMutation(ClearScrollTarget)
    }

    // ── 식재료 등록 ────────────────────────────────────────────────────────────
    private fun register() = intent {
        applyMutation(RegisterStarted)
        val selectedIngredients = state.selectedItems.toDomains()
        val totalCost = state.totalCost

        registerIngredientsUseCase(selectedIngredients)
            .onSuccess {
                postSideEffect(IngredientSideEffect.NavigateToSuccess(totalCost))
            }.onFailure {
                applyMutation(RegisterFailed)
                postSideEffect(IngredientSideEffect.ShowRegisterFailedToast)
            }
    }

    // ── Mutation 적용 ──────────────────────────────────────────────────────────
    override suspend fun Syntax<IngredientUiState, IngredientSideEffect>.applyMutation(
        mutation: IngredientMutation,
    ) {
        when (mutation) {
            is ImageUriLoaded -> reduce {
                state.copy(imageUri = mutation.imageUri)
            }

            is ProgressUpdated -> reduce {
                state.copy(scanProgress = mutation.progress)
            }

            is ScanCompleted -> reduce {
                state.copy(phase = IngredientPhase.ScanSuccess, items = mutation.items.toImmutableList())
            }

            ScanFailed -> reduce {
                state.copy(phase = IngredientPhase.Scanning)
            }

            is ItemUpdated -> reduce {
                state.copy(
                    items = state.items.map { if (it.id == mutation.item.id) mutation.item else it }.toImmutableList()
                )
            }

            is AllSelectionToggled -> reduce {
                state.copy(items = state.items.map { it.copy(isSelected = mutation.selectAll) }.toImmutableList())
            }

            EmptyItemAdded -> reduce {
                state.copy(items = (state.items + IngredientUiModel.empty()).toImmutableList())
            }

            RegisterStarted -> reduce {
                state.copy(phase = IngredientPhase.Registering)
            }

            RegisterFailed -> reduce {
                state.copy(phase = IngredientPhase.ScanSuccess)
            }

            is SetScrollTarget -> reduce { state.copy(scrollTargetIndex = mutation.index) }
            ClearScrollTarget -> reduce { state.copy(scrollTargetIndex = null) }
        }
    }

    /**
     * Route 인자를 생성 시점에 주입한다.
     *
     * Navigation 3에는 NavBackStackEntry가 없어 SavedStateHandle로 Route를 읽을 수 없다.
     * 목적지 인자는 화면을 만드는 쪽이 명시적으로 넘긴다.
     */
    @AssistedFactory
    interface Factory {
        fun create(imageUri: String): IngredientViewModel
    }

}
