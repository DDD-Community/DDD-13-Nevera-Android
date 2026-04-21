package com.anddd.nevera.data.model.notification

import com.google.gson.annotations.SerializedName

internal data class RegisterFcmTokenResponse(
    @SerializedName("message")
    val message: String?
)
