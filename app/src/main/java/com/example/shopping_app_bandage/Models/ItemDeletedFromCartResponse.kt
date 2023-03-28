package com.example.shopping_app_bandage.Models

import com.google.gson.annotations.SerializedName

data class ItemDeletedFromCartResponse(
    @SerializedName("status")
    var status: String
)
