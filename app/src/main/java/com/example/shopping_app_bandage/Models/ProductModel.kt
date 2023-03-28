package com.example.shopping_app_bandage.Models

import com.google.gson.annotations.SerializedName

data class ProductModel(

	@field:SerializedName("sellingPrice")
	val sellingPrice: Double? = null,

	@field:SerializedName("availableUnits")
	val availableUnits: Int? = null,

	@field:SerializedName("imageUrl")
	val imageUrl: ArrayList<String>,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("costPrice")
	val costPrice: Double? = null,

	@field:SerializedName("details")
	val details: String? = null,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("brand")
	val brand: String? = null
)
