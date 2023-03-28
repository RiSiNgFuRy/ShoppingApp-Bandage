package com.example.shopping_app_bandage.Models

import com.google.gson.annotations.SerializedName

data class OrderDetailsSettingRequestModel(
    @SerializedName("productDetails")
    var productDetails: ArrayList<Products>
)

data class Products(
    @SerializedName("productId")
    var productId: String,
    @SerializedName("quantity")
    var quantity: Int
)
