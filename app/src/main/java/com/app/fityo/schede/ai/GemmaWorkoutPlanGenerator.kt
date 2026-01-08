package com.app.fityo.schede.ai

import android.content.Context
import com.app.fityo.import_scheda.GemmaLlmHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class GemmaWorkoutPlanGenerator(
    context: Context
) : WorkoutPlanGenerator {

    private val gemma = GemmaLlmHelper.getInstance(context.applicationContext)

    override suspend fun generate(request: WorkoutPlanRequest): Result<WorkoutPlanResult> =
        withContext(Dispatchers.IO) {
            if (!GemmaLlmHelper.isLibraryAvailable()) {
                return@withContext Result.failure(Exception("Gemma non disponibile in questa build"))
            }

            if (!gemma.isModelAvailable()) {
                return@withContext Result.failure(Exception("Modello Gemma non trovato"))
            }

            val prompt = buildPrompt(request)
            val responseResult = gemma.generateResponse(prompt, GemmaLlmHelper.GemmaProfile.WORKOUT)
            if (responseResult.isFailure) {
                return@withContext Result.failure(
                    responseResult.exceptionOrNull() ?: Exception("Nessuna risposta Gemma")
                )
            }

            val response = responseResult.getOrThrow()
            val exercises = parseExercises(response)
            if (exercises.isEmpty()) {
                return@withContext Result.failure(Exception("Nessun esercizio generato"))
            }

            Result.success(WorkoutPlanResult(exercises = exercises, rawResponse = response))
        }

    private fun buildPrompt(request: WorkoutPlanRequest): String {
        val groups = request.muscleGroups.joinToString(", ")
        val exercisePool = buildExercisePool(request.muscleGroups)
        val poolSection = if (exercisePool.isNotBlank()) {
            "LISTA_ESERCIZI_PREFERITI: $exercisePool"
        } else {
            "LISTA_ESERCIZI_PREFERITI: (non disponibile) usa solo esercizi coerenti con i gruppi indicati"
        }
        val variantId = System.nanoTime()
        return """<start_of_turn>user
Sei un coach fitness senior, specializzato in allenamenti personalizzati. Non essere banale.
- Non confondere le discipline
Genera una scheda di allenamento in JSON per:
- Stile: ${request.style}
- Intensita: ${request.intensity}
- Gruppi muscolari: $groups
$poolSection
VARIANT_ID: $variantId

REGOLE:
1. Restituisci SOLO JSON valido, senza testo extra.
2. Usa numeri interi per sets e reps.
3. Inserisci rest_seconds in secondi (numero intero).
4. Se non sai il peso, usa null.
5. Massimo 12 esercizi.
6. Restituisci sempre una notes custom con attenzione tecnica e accessorio consigliato.
7. Non dimenticare il nome dell'attrezzo/equipment (se applicabile).
8. Alla fine aggiungi sempre un esercizio di addominali, ma mai i classici crunch.
9. Non ripetere esercizi: nomi unici nella sessione.
10. Non usare esercizi fuori dai gruppi scelti. Se c'e LISTA_ESERCIZI_PREFERITI, usa solo quelli o varianti minime.
11. Non inventare peso: weight_kg solo se esplicito o richiesto, altrimenti null.
12. Evita sempre la stessa lista: preferisci varianti coerenti con lo stile.



SCHEMA JSON:
{
  "exercises": [
    {
      "name": "Nome esercizio",
      "sets": 4,
      "reps": 8,
      "rest_seconds": 120,
      "equipment": "Attrezzo o null",
      "weight_kg": 40,
      "notes": "Note o null"
    }
  ]
}

<start_of_turn>model
"""
    }

    private fun parseExercises(response: String): List<WorkoutPlanExercise> {
        val jsonMatch = Regex("""\{[\s\S]*\}""").find(response)
        val jsonString = jsonMatch?.value ?: return emptyList()

        return try {
            val json = JSONObject(jsonString)
            val exercisesArray = json.optJSONArray("exercises") ?: JSONArray()
            val exercises = mutableListOf<WorkoutPlanExercise>()
            val seen = mutableSetOf<String>()

            for (i in 0 until exercisesArray.length()) {
                val ex = exercisesArray.optJSONObject(i) ?: continue
                val name = ex.optString("name", "").trim()
                if (name.isBlank()) continue
                val key = name.lowercase()
                if (!seen.add(key)) continue

                val sets = parseInt(ex.opt("sets")) ?: parseInt(ex.optString("sets")) ?: 0
                val reps = parseInt(ex.opt("reps")) ?: parseInt(ex.optString("reps")) ?: 0
                val rest =
                    parseInt(ex.opt("rest_seconds")) ?: parseInt(ex.optString("rest_seconds"))
                val equipment = ex.optString("equipment").takeIf { it.isNotBlank() && it != "null" }
                val weight =
                    parseFloat(ex.opt("weight_kg")) ?: parseFloat(ex.optString("weight_kg"))
                val notes = ex.optString("notes").takeIf { it.isNotBlank() && it != "null" }

                exercises.add(
                    WorkoutPlanExercise(
                        name = name,
                        sets = sets.coerceAtLeast(1),
                        reps = reps.coerceAtLeast(1),
                        restSeconds = rest,
                        equipment = equipment,
                        weightKg = weight,
                        notes = notes
                    )
                )
            }

            exercises
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun buildExercisePool(groups: List<String>): String {
        val pool = mutableListOf<String>()
        for (group in groups) {
            when (normalizeGroupKey(group)) {
                "addominali" -> pool.addAll(
                    listOf(
                        "Plank",
                        "Side plank",
                        "Hollow hold",
                        "Dead bug",
                        "Leg raise",
                        "Hanging leg raise",
                        "Ab wheel",
                        "Russian twist",
                        "Cable crunch",
                        "Pallof press"
                    )
                )

                "pettorali" -> pool.addAll(
                    listOf(
                        "Panca piana",
                        "Panca inclinata",
                        "Panca declinata",
                        "Chest press",
                        "Distensioni manubri",
                        "Croci manubri",
                        "Croci cavi",
                        "Push up",
                        "Dip"
                    )
                )

                "dorsali" -> pool.addAll(
                    listOf(
                        "Trazioni",
                        "Lat machine",
                        "Pulley",
                        "Rematore bilanciere",
                        "Rematore manubrio",
                        "Pullover cavi",
                        "Hyperextension"
                    )
                )

                "spalle" -> pool.addAll(
                    listOf(
                        "Military press",
                        "Shoulder press",
                        "Alzate laterali",
                        "Alzate frontali",
                        "Alzate posteriori",
                        "Face pull"
                    )
                )

                "bicipiti" -> pool.addAll(
                    listOf(
                        "Curl bilanciere",
                        "Curl manubri",
                        "Curl martello",
                        "Curl concentrato",
                        "Curl ai cavi"
                    )
                )

                "tricipiti" -> pool.addAll(
                    listOf(
                        "French press",
                        "Pushdown cavi",
                        "Skull crusher",
                        "Estensioni manubrio",
                        "Dip tricipiti"
                    )
                )

                "gambe" -> pool.addAll(
                    listOf(
                        "Squat",
                        "Leg press",
                        "Affondi",
                        "Stacco rumeno",
                        "Leg curl",
                        "Leg extension",
                        "Calf raise"
                    )
                )

                "glutei" -> pool.addAll(
                    listOf(
                        "Hip thrust",
                        "Glute bridge",
                        "Cable kickback",
                        "Abduzioni"
                    )
                )

                "polpacci" -> pool.addAll(
                    listOf(
                        "Calf raise",
                        "Seated calf raise",
                        "Donkey calf raise"
                    )
                )
            }
        }

        val finalPool = pool.distinct().shuffled().take(18)
        return finalPool.joinToString(", ")
    }


    private fun normalizeGroupKey(group: String): String? {
        val g = group.lowercase().trim()
        return when {
            g.contains("addom") || g.contains("core") -> "addominali"
            g.contains("petto") || g.contains("pettoral") || g.contains("chest") -> "pettorali"
            g.contains("dors") || g.contains("schiena") || g.contains("back") -> "dorsali"
            g.contains("spall") || g.contains("shoulder") -> "spalle"
            g.contains("bicip") -> "bicipiti"
            g.contains("tricip") -> "tricipiti"
            g.contains("quad") || g.contains("gambe") || g.contains("leg") -> "gambe"
            g.contains("glute") -> "glutei"
            g.contains("polpacc") || g.contains("calf") -> "polpacci"
            else -> null
        }
    }

    private fun parseInt(value: Any?): Int? {
        return when (value) {
            is Number -> value.toInt()
            is String -> parseInt(value)
            else -> null
        }
    }

    private fun parseInt(value: String?): Int? {
        if (value.isNullOrBlank()) return null
        return Regex("""\d+""").find(value)?.value?.toIntOrNull()
    }

    private fun parseFloat(value: Any?): Float? {
        return when (value) {
            is Number -> value.toFloat()
            is String -> parseFloat(value)
            else -> null
        }
    }

    private fun parseFloat(value: String?): Float? {
        if (value.isNullOrBlank()) return null
        return Regex("""\d+(?:[.,]\d+)?""").find(value)?.value?.replace(",", ".")?.toFloatOrNull()
    }
}
