package com.anddd.nevera.data.model.user

import com.google.gson.annotations.SerializedName

internal data class OnboardingStatusResponse(
    @SerializedName("changedNickname")
    val changedNickname: Boolean,
    @SerializedName("hasWish")
    val hasWish: Boolean,
)
