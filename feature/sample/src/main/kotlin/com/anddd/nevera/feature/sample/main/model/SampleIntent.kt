package com.anddd.nevera.feature.sample.main.model

import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface SampleIntent : NeveraIntent {
    data object ClickButton : SampleIntent
}