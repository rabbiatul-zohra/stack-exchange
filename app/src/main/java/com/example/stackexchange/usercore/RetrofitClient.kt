package com.example.stackexchange.usercore

import com.example.stackexchange.usercore.data.UserService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://api.stackexchange.com/2.3/"

    private val gson: Gson = GsonBuilder()
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api: UserService = retrofit.create(UserService::class.java)
}