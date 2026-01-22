package com.app.fityo.data_layer.network

import com.google.gson.annotations.SerializedName

data class WgerSearchResponse(
    val suggestions: List<WgerSuggestionDto> = emptyList()
)

data class WgerSuggestionDto(
    val value: String,
    val data: WgerSuggestionDataDto
)

data class WgerSuggestionDataDto(
    val id: Int,
    @SerializedName("base_id")
    val baseId: Int? = null,
    val name: String? = null,
    val category: String? = null,
    val image: String? = null
)

data class WgerExerciseDetailResponse(
    val id: Int,
    val category: WgerCategoryDto? = null,
    val muscles: List<WgerMuscleDto> = emptyList(),
    @SerializedName("muscles_secondary")
    val musclesSecondary: List<WgerMuscleDto> = emptyList(),
    val images: List<WgerExerciseImageDto> = emptyList(),
    val translations: List<WgerTranslationDto> = emptyList(),
    val videos: List<WgerExerciseVideoDto> = emptyList()
)

data class WgerCategoryDto(
    val id: Int,
    val name: String
)

data class WgerMuscleDto(
    val id: Int? = null,
    val name: String,
    @SerializedName("name_en")
    val nameEn: String? = null,
    @SerializedName("image_url_main")
    val imageUrlMain: String? = null,
    @SerializedName("is_front")
    val isFront: Boolean? = null
)

data class WgerTranslationDto(
    val id: Int,
    val name: String,
    val description: String? = null,
    val language: Int
)

data class WgerExerciseImageDto(
    val image: String,
    @SerializedName("is_main")
    val isMain: Boolean? = null
)

data class WgerExerciseVideoDto(
    val video: String? = null,
    val url: String? = null
)
