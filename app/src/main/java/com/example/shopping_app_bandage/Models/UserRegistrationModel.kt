package com.example.shopping_app_bandage.Models

import com.google.gson.annotations.SerializedName

data class UserRegistrationModel(
    @SerializedName("email")
    var email: String,
    @SerializedName("username")
    var userName: String,
    @SerializedName("password")
    var password: String
)
