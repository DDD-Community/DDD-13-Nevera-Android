package com.anddd.nevera.domain.model.user

data class Profile(
    val profileImageUrl: String,
    val nickname: String,
    val email: String,
    val hasWish: Boolean,
)
