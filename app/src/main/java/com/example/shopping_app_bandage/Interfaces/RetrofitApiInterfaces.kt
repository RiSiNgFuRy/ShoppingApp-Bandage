package com.example.shopping_app_bandage.Interfaces

import com.example.shopping_app_bandage.Models.*
import retrofit2.Response
import retrofit2.http.*

interface RetrofitApiInterfaces {
    @POST("/login")
    suspend fun verifyLoginCredentials(@Body loginCredential: UserLoginModel): Response<UserLoginResponseModel>

    @POST("/token-verification")
    suspend fun verifyToken(@Body tokenModel: LoginTokenVerificationModel): Response<TokenStatus>

    @POST("/registration")
    suspend fun registerUser(@Body registerCredentials: UserRegisterModel): Response<UserRegisterResponseModel>

    @GET("/product")
    suspend fun getAllProducts(): Response<ArrayList<ProductModel>>

    @GET("/product/{id}")
    suspend fun getProductById(@Path("id") productId: String): Response<ArrayList<ProductModel>>

    @GET("/user/{userId}/cart")
    suspend fun getUserCartItems(@Path("userId") userId: String): Response<CartModel>

    @POST("/user/{userId}/cart")
    suspend fun addToCart(
        @Path("userId") userId: String,
        @Body productId: ItemAddToCartRequestModel
    ): Response<ItemAddedToCartResponse>

    @DELETE("/user/{userId}/cart")
    suspend fun deleteItemFromCart(
        @Path("userId") userId: String,
        @Query("deleteProductId") productId: String
    ): Response<ItemDeletedFromCartResponse>

    @PATCH("/user/{userId}/cart")
    suspend fun changeQuantityOfCartItem(
        @Path("userId") userId: String,
        @Body changeQtyModel: ItemChangedQuantityCartRequestModel
    ): Response<ItemChangedQuantityCartResponse>

    @POST("/user/{userId}/orders")
    suspend fun setOrderDetails(
        @Path("userId") userId: String,
        @Body orderSettingRequestModel: OrderDetailsSettingRequestModel
    ): Response<OrderDetailsSettingResponseModel>

}