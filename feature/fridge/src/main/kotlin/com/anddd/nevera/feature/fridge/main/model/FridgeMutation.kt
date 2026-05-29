package com.anddd.nevera.feature.fridge.main.model

import com.anddd.nevera.core.mvi.NeveraMutation

sealed interface FridgeMutation : NeveraMutation {
    data object LoadSuccess : FridgeMutation
    data class SelectStorageFilter(val filter: StorageLocationFilter) : FridgeMutation
}
