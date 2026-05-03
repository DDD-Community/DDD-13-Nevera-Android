package com.anddd.nevera.feature.mypage.main.model

sealed interface MyPageIntent {
    data object Load : MyPageIntent
    data object Submit : MyPageIntent
    data object Reset : MyPageIntent
}
