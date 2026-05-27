package com.anddd.nevera.data.model.home

import com.google.gson.annotations.SerializedName

internal data class HomeSummaryResponse(
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("wishId")
    val wishId: Long?,
    @SerializedName("wishName")
    val wishName: String?,
    @SerializedName("wishAmount")
    val wishAmount: Int?,
    @SerializedName("accumulated")
    val accumulated: Int?,
    @SerializedName("remaining")
    val remaining: Int?,
    @SerializedName("achieved")
    val achieved: Boolean?,
    @SerializedName("totalConsumed")
    val totalConsumed: Int,
    @SerializedName("totalWasted")
    val totalWasted: Int,
)
