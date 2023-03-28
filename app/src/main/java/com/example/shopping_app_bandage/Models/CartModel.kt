package com.example.shopping_app_bandage.Models

import com.google.gson.annotations.SerializedName

data class CartModel(

	@field:SerializedName("totalAmount")
	val totalAmount: Double? = null,

	@field:SerializedName("cartItems")
	val cartItems: ArrayList<CartItems>,

	@field:SerializedName("userId")
	val userId: String? = null
)

data class CartItems(

	@field:SerializedName("product")
	val product: ProductModel,

	@field:SerializedName("quantity")
	val quantity: Int? = null
)
