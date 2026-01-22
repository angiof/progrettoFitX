package com.app.fityo.data_layer.network

import com.google.gson.annotations.SerializedName

data class OpenFoodFactsProductResponse(
    val status: Int? = null,
    val product: OpenFoodFactsProduct? = null
)

data class OpenFoodFactsSearchResponse(
    val products: List<OpenFoodFactsProduct>? = null,
    val count: Int? = null,
    val page: Int? = null,
    @SerializedName("page_size")
    val pageSize: Int? = null
)

data class OpenFoodFactsProduct(
    @SerializedName("product_name")
    val productName: String? = null,
    @SerializedName("product_name_it")
    val productNameIt: String? = null,
    @SerializedName("brands")
    val brands: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("image_front_small_url")
    val imageSmallUrl: String? = null,
    val nutriments: OpenFoodFactsNutriments? = null,
    @SerializedName("nutriscore_grade")
    val nutriscoreGrade: String? = null,
    @SerializedName("quantity")
    val quantity: String? = null
)

data class OpenFoodFactsNutriments(
    // Proteine
    @SerializedName("proteins_100g")
    val proteins: Float? = null,

    // Carboidrati
    @SerializedName("carbohydrates_100g")
    val carbs: Float? = null,

    // Grassi
    @SerializedName("fat_100g")
    val fats: Float? = null,

    // Grassi saturi
    @SerializedName("saturated-fat_100g")
    val saturatedFats: Float? = null,

    // Zuccheri
    @SerializedName("sugars_100g")
    val sugars: Float? = null,

    // Fibre
    @SerializedName("fiber_100g")
    val fibers: Float? = null,

    // Sale
    @SerializedName("salt_100g")
    val salt: Float? = null,

    // Sodio (alternativa al sale)
    @SerializedName("sodium_100g")
    val sodium: Float? = null,

    // Energia in kcal
    @SerializedName("energy-kcal_100g")
    val energyKcal: Float? = null,

    // Energia in kJ (fallback)
    @SerializedName("energy_100g")
    val energy: Float? = null
)
