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

    override suspend fun generate(request: WorkoutPlanRequest): Result<WorkoutPlanResult> = withContext(Dispatchers.IO) {
        if (!GemmaLlmHelper.isLibraryAvailable()) {
            return@withContext Result.failure(Exception("Gemma non disponibile in questa build"))
        }

        if (!gemma.isModelAvailable()) {
            return@withContext Result.failure(Exception("Modello Gemma non trovato"))
        }

        if (!gemma.isReady()) {
            val init = gemma.initializeModel()
            if (init.isFailure) {
                return@withContext Result.failure(init.exceptionOrNull() ?: Exception("Gemma non inizializzato"))
            }
        }

        val prompt = buildPrompt(request)
        val responseResult = gemma.generateResponse(prompt)
        if (responseResult.isFailure) {
            return@withContext Result.failure(responseResult.exceptionOrNull() ?: Exception("Nessuna risposta Gemma"))
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
        return """<start_of_turn>user
Sei un coach fitness. Genera una scheda di allenamento in JSON per:
- Stile: ${request.style}
- Intensita: ${request.intensity}
- Gruppi muscolari: $groups

REGOLE:
1. Restituisci SOLO JSON valido, senza testo extra.
2. Usa numeri interi per sets e reps.
3. Inserisci rest_seconds in secondi (numero intero).
4. Se non sai il peso, usa null.
5. Massimo 10 esercizi.

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

            for (i in 0 until exercisesArray.length()) {
                val ex = exercisesArray.optJSONObject(i) ?: continue
                val name = ex.optString("name", "").trim()
                if (name.isBlank()) continue

                val sets = parseInt(ex.opt("sets")) ?: parseInt(ex.optString("sets")) ?: 0
                val reps = parseInt(ex.opt("reps")) ?: parseInt(ex.optString("reps")) ?: 0
                val rest = parseInt(ex.opt("rest_seconds")) ?: parseInt(ex.optString("rest_seconds"))
                val equipment = ex.optString("equipment").takeIf { it.isNotBlank() && it != "null" }
                val weight = parseFloat(ex.opt("weight_kg")) ?: parseFloat(ex.optString("weight_kg"))
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
