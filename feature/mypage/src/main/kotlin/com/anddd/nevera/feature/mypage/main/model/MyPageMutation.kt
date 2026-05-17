package com.anddd.nevera.feature.mypage.main.model

import com.anddd.nevera.core.mvi.NeveraMutation

sealed interface MyPageMutation : NeveraMutation {
    data object Loading : MyPageMutation
    data object LoadComplete : MyPageMutation
}
