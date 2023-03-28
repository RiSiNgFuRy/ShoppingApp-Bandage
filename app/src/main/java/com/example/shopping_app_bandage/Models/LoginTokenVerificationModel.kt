package com.example.shopping_app_bandage.Models

import com.google.gson.annotations.SerializedName

data class LoginTokenVerificationModel(
    @SerializedName("token")
    var token: String
)
