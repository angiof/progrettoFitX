package com.app.fityo.data_layer.network

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface WgerApi {

    @Headers("Accept: application/json")
    @GET("api/v2/exercise/search/")
    suspend fun searchExercise(
        @Query("term") query: String,
        @Query("language") lang: String = "it"
    ): WgerSearchResponse

    @Headers("Accept: application/json")
    @GET("api/v2/exerciseinfo/{id}/")
    suspend fun getExerciseDetails(
        @Path("id") exerciseId: Int
    ): WgerExerciseDetailResponse
}
