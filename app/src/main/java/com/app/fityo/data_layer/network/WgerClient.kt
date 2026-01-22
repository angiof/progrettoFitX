package com.app.fityo.data_layer.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WgerClient {
    private const val BASE_URL = "https://wger.de/"

    val service: WgerApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WgerApi::class.java)
    }
}
