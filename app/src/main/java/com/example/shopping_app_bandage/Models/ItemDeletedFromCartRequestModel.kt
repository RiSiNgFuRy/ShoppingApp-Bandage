package com.example.shopping_app_bandage.Models

import com.google.gson.annotations.SerializedName

data class ItemDeletedFromCartRequestModel(
    @SerializedName("productId")
    var productId: String
)
