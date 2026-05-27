package com.anddd.nevera.data.model.user

import com.google.gson.annotations.SerializedName

internal data class ProfileResponse(
    @SerializedName("profileImageUrl")
    val profileImageUrl: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("hasWish")
    val hasWish: Boolean,
)
