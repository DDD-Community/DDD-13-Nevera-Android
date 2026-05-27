package com.anddd.nevera.data.model.ingredient

import com.google.gson.annotations.SerializedName

internal data class IngredientResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("categoryDisplayName")
    val categoryDisplayName: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("cost")
    val cost: Int,
)
