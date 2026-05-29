package com.anddd.nevera.data.model.wish

import com.google.gson.annotations.SerializedName

internal data class CreateWishRequest(
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: Long,
)
