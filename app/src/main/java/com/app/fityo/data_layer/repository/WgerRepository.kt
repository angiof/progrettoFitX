package com.app.fityo.data_layer.repository

import com.app.fityo.data_layer.network.WgerApi
import com.app.fityo.dominio.WgerExerciseDetail
import com.app.fityo.dominio.WgerImage
import com.app.fityo.dominio.WgerMuscle
import com.app.fityo.dominio.WgerSuggestion
import com.app.fityo.dominio.WgerVideo

class WgerRepository(
    private val api: WgerApi,
    private val baseUrl: String = "https://wger.de"
) {
    companion object {
        private const val LANGUAGE_IT = 13
        private const val LANGUAGE_EN = 2
    }

    suspend fun searchExercises(query: String, language: String = "it"): Result<List<WgerSuggestion>> {
        return runCatching {
            val response = api.searchExercise(query = query.trim(), lang = language)
            response.suggestions.mapNotNull { suggestion ->
                suggestion.data.baseId?.let { baseId ->
                    WgerSuggestion(
                        id = suggestion.data.id,
                        baseId = baseId,
                        value = suggestion.value,
                        category = suggestion.data.category,
                        imageUrl = suggestion.data.image?.let { normalizeUrl(it) }
                    )
                }
            }
        }
    }

    suspend fun getExerciseDetail(baseId: Int): Result<WgerExerciseDetail> {
        return runCatching {
            val response = api.getExerciseDetails(exerciseId = baseId)

            // Find Italian translation, fallback to English, then first available
            val translation = response.translations.find { it.language == LANGUAGE_IT }
                ?: response.translations.find { it.language == LANGUAGE_EN }
                ?: response.translations.firstOrNull()

            val allMuscles = response.muscles + response.musclesSecondary

            WgerExerciseDetail(
                id = response.id,
                name = translation?.name ?: "Esercizio #${response.id}",
                descriptionHtml = translation?.description.orEmpty(),
                muscles = allMuscles.map { muscle ->
                    WgerMuscle(
                        name = muscle.name,
                        imageUrlMain = muscle.imageUrlMain?.let { normalizeUrl(it) },
                        isFront = muscle.isFront
                    )
                },
                images = response.images.map { image ->
                    WgerImage(
                        url = normalizeUrl(image.image),
                        isMain = image.isMain
                    )
                },
                videos = response.videos.mapNotNull { video ->
                    (video.video ?: video.url)?.let { url ->
                        WgerVideo(url = normalizeUrl(url))
                    }
                }
            )
        }
    }

    private fun normalizeUrl(path: String): String {
        return if (path.startsWith("http")) path else baseUrl + path
    }
}
