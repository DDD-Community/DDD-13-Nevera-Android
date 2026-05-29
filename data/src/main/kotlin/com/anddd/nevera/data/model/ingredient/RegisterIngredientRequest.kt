package com.anddd.nevera.data.model.ingredient

import com.google.gson.annotations.SerializedName

internal data class RegisterIngredientRequest(
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("location") val location: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("expirationDate") val expirationDate: String?,
    @SerializedName("cost") val cost: Int,
)
