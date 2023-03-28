package com.example.shopping_app_bandage.Models

import com.google.gson.annotations.SerializedName

data class UserLoginModel (
    @SerializedName("email")
    var email: String,
    @SerializedName("password")
    var password: String
)