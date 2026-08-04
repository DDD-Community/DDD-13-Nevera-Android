package com.anddd.nevera.feature.mypage.main.model

import com.anddd.nevera.domain.model.user.Profile

data class ProfileUiModel(
    val nickname: String = "김푸드",
    val email: String = "anddd@email.com",
    val profileImage: String? = "https://fastly.picsum.photos/id/459/200/200.jpg?hmac=WxFjGfN8niULmp7dDQKtjraxfa4WFX-jcTtkMyH4I-Y",
    val hasWish: Boolean = false,
)

internal fun Profile.toUiModel(): ProfileUiModel {
    return ProfileUiModel(
        nickname = this.nickname,
        email = this.email,
        profileImage = this.profileImageUrl,
        hasWish = hasWish,
    )
}
