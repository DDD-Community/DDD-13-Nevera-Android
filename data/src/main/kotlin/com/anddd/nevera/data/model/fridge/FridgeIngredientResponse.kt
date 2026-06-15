package com.anddd.nevera.data.model.fridge

import com.google.gson.annotations.SerializedName

internal data class FridgeIngredientResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("location") val location: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("expirationDate") val expirationDate: String,
    @SerializedName("dDay") val dDay: Int? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("cost") val cost: Int,
    @SerializedName("createdAt") val createdAt: String,
)
