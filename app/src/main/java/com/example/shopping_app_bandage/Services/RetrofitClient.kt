package com.example.shopping_app_bandage.Services

import com.example.shopping_app_bandage.Interfaces.RetrofitApiInterfaces
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.file.attribute.AclEntry.Builder

object RetrofitClient {
    private val httpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(httpLoggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://1f77-125-20-24-2.in.ngrok.io")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun buildService(): RetrofitApiInterfaces{
        return retrofit.create(RetrofitApiInterfaces::class.java)
    }

}