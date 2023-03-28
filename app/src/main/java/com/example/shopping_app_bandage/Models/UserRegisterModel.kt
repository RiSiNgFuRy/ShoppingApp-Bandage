package com.example.shopping_app_bandage.Models

import com.google.gson.annotations.SerializedName

data class UserRegisterModel(
    @SerializedName("email")
    var email: String,
    @SerializedName("username")
    var username: String,
    @SerializedName("password")
    var password: String
)
