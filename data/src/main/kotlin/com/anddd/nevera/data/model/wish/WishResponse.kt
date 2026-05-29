package com.anddd.nevera.data.model.wish

import com.google.gson.annotations.SerializedName

internal data class WishResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: Long,
)
