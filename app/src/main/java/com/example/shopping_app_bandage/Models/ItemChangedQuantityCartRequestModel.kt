package com.example.shopping_app_bandage.Models

import com.google.gson.annotations.SerializedName

data class ItemChangedQuantityCartRequestModel(
    @SerializedName("productId")
    var productId: String,
    @SerializedName("quantity")
    var quantity: Int,
)
