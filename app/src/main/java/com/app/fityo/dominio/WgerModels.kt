package com.app.fityo.dominio

data class WgerSuggestion(
    val id: Int,
    val baseId: Int,
    val value: String,
    val category: String? = null,
    val imageUrl: String? = null
)

data class WgerExerciseDetail(
    val id: Int,
    val name: String,
    val descriptionHtml: String,
    val muscles: List<WgerMuscle> = emptyList(),
    val images: List<WgerImage> = emptyList(),
    val videos: List<WgerVideo> = emptyList()
)

data class WgerMuscle(
    val name: String,
    val imageUrlMain: String? = null,
    val isFront: Boolean? = null
)

data class WgerImage(
    val url: String,
    val isMain: Boolean? = null
)

data class WgerVideo(
    val url: String
)
