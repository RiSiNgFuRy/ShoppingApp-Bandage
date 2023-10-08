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
        .baseUrl("https://4db2-2405-201-4039-800f-e520-ca5a-90c-f02e.ngrok-free.app")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun buildService(): RetrofitApiInterfaces{
        return retrofit.create(RetrofitApiInterfaces::class.java)
    }

}