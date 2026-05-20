package com.anddd.nevera.feature.main.home.model

import com.anddd.nevera.core.mvi.NeveraMutation

sealed interface HomeMutation : NeveraMutation {
    data object Loading : HomeMutation
    data class LoadComplete(val wishUiModel: WishUiModel) : HomeMutation
}
