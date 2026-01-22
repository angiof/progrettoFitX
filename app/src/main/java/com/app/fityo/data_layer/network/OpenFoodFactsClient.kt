package com.app.fityo.data_layer.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenFoodFactsClient {
    private const val BASE_URL = "https://world.openfoodfacts.org/"

    val service: OpenFoodFactsService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodFactsService::class.java)
    }
}
