package com.app.fityo.data_layer.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenFoodFactsService {

    companion object {
        // Campi nutrizionali richiesti
        private const val NUTRIENT_FIELDS = "product_name,product_name_it,brands,image_url," +
                "image_front_small_url,nutriments,nutriscore_grade,quantity"
    }

    @GET("api/v2/product/{barcode}.json")
    suspend fun getProduct(
        @Path("barcode") barcode: String,
        @Query("fields") fields: String = NUTRIENT_FIELDS,
        @Query("lc") locale: String = "it"
    ): OpenFoodFactsProductResponse

    @GET("cgi/search.pl")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("search_simple") searchSimple: Int = 1,
        @Query("action") action: String = "process",
        @Query("json") json: Int = 1,
        @Query("fields") fields: String = NUTRIENT_FIELDS,
        @Query("lc") locale: String = "it",
        @Query("cc") country: String = "it",
        @Query("page_size") pageSize: Int = 10,
        @Query("sort_by") sortBy: String = "popularity_key"
    ): OpenFoodFactsSearchResponse
}
