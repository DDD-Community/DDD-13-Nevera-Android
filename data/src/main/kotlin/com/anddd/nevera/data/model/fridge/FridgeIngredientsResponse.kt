package com.anddd.nevera.data.model.fridge

import com.google.gson.annotations.SerializedName

internal data class FridgeIngredientsResponse(
    @SerializedName("content") val content: List<FridgeIngredientResponse>,
    @SerializedName("last") val last: Boolean,
    @SerializedName("number") val number: Int,
)
