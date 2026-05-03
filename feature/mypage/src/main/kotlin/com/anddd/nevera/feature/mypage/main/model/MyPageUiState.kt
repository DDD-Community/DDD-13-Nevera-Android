package com.anddd.nevera.feature.mypage.main.model

data class MyPageUiState(
    val status: MyPageStatus = MyPageStatus.Idle,
)

sealed interface MyPageStatus {
    data object Idle : MyPageStatus
    data object Loading : MyPageStatus
    data object Success : MyPageStatus
}
