package com.anddd.nevera.feature.sample.main.model

import com.anddd.nevera.core.mvi.NeveraState

data class SampleUiState(
    val count: Int = 0,
) : NeveraState
