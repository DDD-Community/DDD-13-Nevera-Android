package com.anddd.nevera.data.model.notification

import com.google.gson.annotations.SerializedName

internal data class RegisterFcmTokenRequest(
    @SerializedName("token")
    val token: String,
)
