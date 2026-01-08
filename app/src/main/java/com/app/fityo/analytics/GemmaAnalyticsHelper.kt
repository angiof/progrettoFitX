package com.app.fityo.analytics

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.dominio.GruppoMuscolarePercentuale
import com.app.fityo.import_scheda.GemmaEngineType
import com.app.fityo.import_scheda.GemmaLlmHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class GemmaAnalyticsHelper(private val context: Context) {

    companion object {
        private const val TAG = "GemmaAnalyticsHelper"
        private const val PREFS_NAME = "gemma_analytics_cache"
        private const val KEY_LAST_UPDATE = "last_update_epoch_day"
        private const val KEY_CACHED_INSIGHTS = "cached_insights"
        private const val KEY_ENGINE_USED = "engine_used"
        private const val KEY_DATA_FINGERPRINT = "data_fingerprint"
        private const val CACHE_VALIDITY_DAYS = 7
    }

    private val gemma = GemmaLlmHelper.getInstance(context)
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isGemmaAvailable(): Boolean {
        return gemma.isModelAvailable()
    }

    fun getCurrentEngine(): GemmaEngineType {
        return GemmaLlmHelper.getEngineType()
    }

    suspend fun generateInsights(
        schede: List<SchedeEntity>,
        esercizi: List<EsserciziEntity>,
        muscleDistribution: List<GruppoMuscolarePercentuale>,
        profileId: Int? = null,
        profileName: String? = null,
        forceRefresh: Boolean = false
    ): Result<GemmaLiveInsights> = withContext(Dispatchers.IO) {

        val cacheKey = getCacheKey(profileId)
        val analysis = analyzeWorkoutData(schede, esercizi, muscleDistribution)
        val fingerprint = fingerprint(analysis)

        // Check cache
        if (!forceRefresh && isCacheValid(cacheKey, fingerprint)) {
            val cached = loadCachedInsights(cacheKey)
            if (cached != null) {
                Log.d(TAG, "Using cached insights for profile $profileId from ${cached.generatedDate}")
                return@withContext Result.success(cached)
            }
        }

        val initResult = gemma.ensureReady(GemmaLlmHelper.GemmaProfile.ANALYTICS)
        if (initResult.isFailure) {
            return@withContext Result.failure(initResult.exceptionOrNull() ?: Exception("Gemma init failed"))
        }

        try {
            // Build prompt
            val prompt = buildSmartPrompt(analysis, profileName)

            Log.d(TAG, "Sending analytics prompt to Gemma")
            val response = gemma.generateResponse(prompt, GemmaLlmHelper.GemmaProfile.ANALYTICS)

            response.fold(
                onSuccess = { rawResponse ->
                    Log.d(TAG, "Gemma response received: ${rawResponse.take(200)}...")
                    val insights = parseGemmaInsights(rawResponse, analysis, profileId, profileName)
                    cacheInsights(insights, cacheKey, fingerprint)
                    Result.success(insights)
                },
                onFailure = { error ->
                    Log.e(TAG, "Gemma generation failed", error)
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error generating insights", e)
            Result.failure(e)
        }
    }

    suspend fun refreshInsights(
        schede: List<SchedeEntity>,
        esercizi: List<EsserciziEntity>,
        muscleDistribution: List<GruppoMuscolarePercentuale>,
        profileId: Int? = null,
        profileName: String? = null
    ): Result<GemmaLiveInsights> {
        return generateInsights(schede, esercizi, muscleDistribution, profileId, profileName, forceRefresh = true)
    }

    /**
     * Genera insights senza usare Gemma - usa solo l'analisi intelligente dei dati.
     * Utile quando Gemma non e disponibile o si vuole una risposta immediata.
     */
    suspend fun generateSmartInsightsWithoutGemma(
        schede: List<SchedeEntity>,
        esercizi: List<EsserciziEntity>,
        muscleDistribution: List<GruppoMuscolarePercentuale>,
        profileId: Int? = null,
        profileName: String? = null
    ): GemmaLiveInsights = withContext(Dispatchers.IO) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val analysis = analyzeWorkoutData(schede, esercizi, muscleDistribution)
        createFallbackInsights(analysis, today, "Smart Analysis", profileId, profileName)
    }

    private fun getCacheKey(profileId: Int?): String {
        return if (profileId != null) "profile_$profileId" else "all_profiles"
    }

    private fun fingerprint(analysis: WorkoutDataAnalysis): String {
        val topMuscles = analysis.muscleDistribution
            .sortedByDescending { it.percentuale }
            .take(5)
            .joinToString("|") { "${it.gruppoMuscolare}:${String.format("%.1f", it.percentuale)}" }

        val topVolume = analysis.volumePerMuscleThisWeek.entries
            .sortedByDescending { it.value }
            .take(5)
            .joinToString("|") { "${it.key}:${it.value.toInt()}" }

        return listOf(
            analysis.totalWorkoutsLastMonth,
            analysis.totalWorkoutsLastWeek,
            analysis.daysSinceLastWorkout,
            analysis.completedWorkouts,
            topMuscles,
            topVolume
        ).joinToString("#")
    }

    private fun isCacheValid(cacheKey: String, fingerprint: String): Boolean {
        val lastUpdate = prefs.getLong("${KEY_LAST_UPDATE}_$cacheKey", 0L)
        if (lastUpdate == 0L) return false
        val today = LocalDate.now().toEpochDay()
        if ((today - lastUpdate) >= CACHE_VALIDITY_DAYS) return false

        val cachedFingerprint = prefs.getString("${KEY_DATA_FINGERPRINT}_$cacheKey", null)
        return cachedFingerprint == fingerprint
    }

    private fun loadCachedInsights(cacheKey: String): GemmaLiveInsights? {
        val json = prefs.getString("${KEY_CACHED_INSIGHTS}_$cacheKey", null) ?: return null
        return try {
            parseInsightsFromJson(json)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading cached insights", e)
            null
        }
    }

    private fun cacheInsights(insights: GemmaLiveInsights, cacheKey: String, fingerprint: String) {
        val json = insightsToJson(insights)
        prefs.edit()
            .putLong("${KEY_LAST_UPDATE}_$cacheKey", LocalDate.now().toEpochDay())
            .putString("${KEY_CACHED_INSIGHTS}_$cacheKey", json)
            .putString("${KEY_ENGINE_USED}_$cacheKey", insights.engineUsed)
            .putString("${KEY_DATA_FINGERPRINT}_$cacheKey", fingerprint)
            .apply()
        Log.d(TAG, "Insights cached for $cacheKey")
    }

    fun clearCacheForProfile(profileId: Int?) {
        val cacheKey = getCacheKey(profileId)
        prefs.edit()
            .remove("${KEY_LAST_UPDATE}_$cacheKey")
            .remove("${KEY_CACHED_INSIGHTS}_$cacheKey")
            .remove("${KEY_ENGINE_USED}_$cacheKey")
            .remove("${KEY_DATA_FINGERPRINT}_$cacheKey")
            .apply()
    }

    private fun analyzeWorkoutData(
        schede: List<SchedeEntity>,
        esercizi: List<EsserciziEntity>,
        muscleDistribution: List<GruppoMuscolarePercentuale>
    ): WorkoutDataAnalysis {
        val today = LocalDate.now()
        val oneMonthAgo = today.minusDays(30)
        val oneWeekAgo = today.minusDays(7)
        val twoWeeksAgo = today.minusDays(14)

        // Parse dates
        val schedeWithDates = schede.mapNotNull { scheda ->
            parseLocalDate(scheda.data)?.let { date -> scheda to date }
        }.sortedBy { it.second }

        val schedeLastMonth = schedeWithDates.filter { it.second >= oneMonthAgo }
        val schedeLastWeek = schedeWithDates.filter { it.second >= oneWeekAgo }
        val schedePrevWeek = schedeWithDates.filter { it.second >= twoWeeksAgo && it.second < oneWeekAgo }

        // Calculate metrics
        val totalWorkoutsLastMonth = schedeLastMonth.size
        val totalWorkoutsLastWeek = schedeLastWeek.size

        val avgRestDays = if (schedeWithDates.size >= 2) {
            val diffs = schedeWithDates.zipWithNext { a, b ->
                ChronoUnit.DAYS.between(a.second, b.second).toFloat()
            }
            diffs.average().toFloat()
        } else 0f

        val lastWorkoutDate = schedeWithDates.lastOrNull()?.second
        val daysSinceLastWorkout = lastWorkoutDate?.let { ChronoUnit.DAYS.between(it, today).toInt() } ?: 0

        // Average intensity
        val avgIntensity = schedeLastMonth.map { (scheda, _) ->
            when (scheda.intesita.lowercase()) {
                "alta" -> 3f
                "media" -> 2f
                else -> 1f
            }
        }.average().toFloat()

        fun totalVolumeSetsReps(list: List<Pair<SchedeEntity, LocalDate>>): Float {
            var total = 0f
            list.forEach { (scheda, _) ->
                val ex = esercizi.filter { it.schedaId == scheda.id }
                total += ex.sumOf { (it.nSerie * it.nRipetizione).toDouble() }.toFloat()
            }
            return total
        }

        val volThisWeek = totalVolumeSetsReps(schedeLastWeek)
        val volPrevWeek = totalVolumeSetsReps(schedePrevWeek)
        val volumeTrendPct = if (volPrevWeek > 0f) {
            (((volThisWeek - volPrevWeek) / volPrevWeek) * 100f).toInt()
        } else {
            null
        }

        // Volume per muscle this week
        val volumeThisWeek = mutableMapOf<String, Float>()
        schedeLastWeek.forEach { (scheda, _) ->
            val muscle = scheda.gruppoMuscolare
            val schedaEsercizi = esercizi.filter { it.schedaId == scheda.id }
            val volume = schedaEsercizi.sumOf { (it.nSerie * it.nRipetizione).toDouble() }.toFloat()
            volumeThisWeek[muscle] = (volumeThisWeek[muscle] ?: 0f) + volume
        }

        // Exercise frequency and stats
        val exerciseFrequency = esercizi.groupingBy { it.nome.lowercase() }.eachCount()
        val exerciseStats = exerciseFrequency.keys.associateWith { name ->
            val exercises = esercizi.filter { it.nome.lowercase() == name }
            ExerciseStats(
                count = exercises.size,
                avgSets = exercises.map { it.nSerie.toFloat() }.average().toFloat(),
                avgReps = exercises.map { it.nRipetizione.toFloat() }.average().toFloat(),
                avgWeight = exercises.mapNotNull { it.peso }.average().toFloat().takeIf { !it.isNaN() },
                maxWeight = exercises.mapNotNull { it.peso }.maxOrNull()
            )
        }

        val weeksWithWorkouts = schedeWithDates
            .map { it.second.toEpochDay() / 7 }
            .distinct()
            .sorted()

        var currentStreakWeeks = 0
        if (weeksWithWorkouts.isNotEmpty()) {
            currentStreakWeeks = 1
            for (i in weeksWithWorkouts.size - 2 downTo 0) {
                val current = weeksWithWorkouts[i]
                val next = weeksWithWorkouts[i + 1]
                if (next - current == 1L) {
                    currentStreakWeeks++
                } else {
                    break
                }
            }
        }

        val freqScore = (totalWorkoutsLastWeek * 25).coerceIn(0, 100)
        val restScore = (100 - ((kotlin.math.abs(avgRestDays - 2f) / 2f) * 100).toInt()).coerceIn(0, 100)
        val adherenceScore = ((freqScore * 0.7f) + (restScore * 0.3f)).toInt().coerceIn(0, 100)

        // Neglected and dominant muscles
        val neglectedMuscles = muscleDistribution.filter { it.percentuale < 10 }.map { it.gruppoMuscolare }
        val dominantMuscles = muscleDistribution.filter { it.percentuale > 25 }.map { it.gruppoMuscolare }
        val perc = muscleDistribution.map { it.percentuale.toFloat() }.sortedDescending()
        val top = perc.firstOrNull() ?: 0f
        val bottom = perc.lastOrNull() ?: 0f
        val spread = (top - bottom).coerceAtLeast(0f)
        val muscleBalanceScore = (100 - spread * 2f).toInt().coerceIn(0, 100)

        // Training time distribution
        val trainingTimeDistribution = schedeWithDates.mapNotNull { (scheda, _) ->
            scheda.ora?.let { ora ->
                val hour = ora.split(":").firstOrNull()?.toIntOrNull() ?: return@mapNotNull null
                when {
                    hour < 12 -> "Mattina"
                    hour < 17 -> "Pomeriggio"
                    else -> "Sera"
                }
            }
        }.groupingBy { it }.eachCount()

        // Heart rate stats
        val avgHeartRate = schedeLastMonth.mapNotNull { (scheda, _) -> scheda.avgHeartRate }.average().toInt().takeIf { it > 0 }
        val maxHeartRate = schedeLastMonth.mapNotNull { (scheda, _) -> scheda.maxHeartRate }.maxOrNull()

        // Completed count
        val completedWorkouts = schedeLastMonth.count { (scheda, _) -> scheda.completed }

        return WorkoutDataAnalysis(
            totalWorkoutsLastMonth = totalWorkoutsLastMonth,
            totalWorkoutsLastWeek = totalWorkoutsLastWeek,
            avgRestDays = avgRestDays,
            daysSinceLastWorkout = daysSinceLastWorkout,
            avgIntensity = avgIntensity,
            volumePerMuscleThisWeek = volumeThisWeek,
            exerciseStats = exerciseStats.toList().sortedByDescending { it.second.count }.take(10).toMap(),
            muscleDistribution = muscleDistribution,
            neglectedMuscles = neglectedMuscles,
            dominantMuscles = dominantMuscles,
            lastWorkoutDate = lastWorkoutDate,
            trainingTimeDistribution = trainingTimeDistribution,
            avgHeartRate = avgHeartRate,
            maxHeartRate = maxHeartRate,
            completedWorkouts = completedWorkouts,
            currentStreakWeeks = currentStreakWeeks,
            adherenceScore = adherenceScore,
            muscleBalanceScore = muscleBalanceScore,
            volumeTrendPct = volumeTrendPct
        )
    }

    private fun buildSmartPrompt(analysis: WorkoutDataAnalysis, profileName: String?): String {
        val profileIntro = if (profileName != null) {
            "Stai analizzando i dati di allenamento per: $profileName"
        } else {
            "Stai analizzando i dati di allenamento"
        }
        val variantId = LocalDate.now().toEpochDay()

        val isNewUser = analysis.totalWorkoutsLastMonth < 3
        val hasNoData = analysis.totalWorkoutsLastMonth == 0

        val volumeThisWeek = analysis.volumePerMuscleThisWeek.entries
            .sortedByDescending { it.value }
            .joinToString("\n") { "  - ${it.key}: ${String.format("%.0f", it.value)} (sets*reps)" }
            .ifBlank { "  Nessun dato questa settimana" }

        val topExercises = analysis.exerciseStats.entries
            .take(8)
            .joinToString("\n") { (name, stats) ->
                val weight = stats.avgWeight?.let { "${String.format("%.1f", it)}kg" } ?: "N/D"
                "  - $name: ${stats.count}x, ${stats.avgSets.toInt()}x${stats.avgReps.toInt()}, peso $weight"
            }
            .ifBlank { "  Nessun esercizio registrato" }

        val muscleBalance = analysis.muscleDistribution
            .sortedByDescending { it.percentuale }
            .joinToString("\n") { "  - ${it.gruppoMuscolare}: ${String.format("%.1f", it.percentuale)}%" }
            .ifBlank { "  Nessun dato" }

        val heartRateInfo = if (analysis.avgHeartRate != null) {
            "BPM medio: ${analysis.avgHeartRate}, max: ${analysis.maxHeartRate ?: "N/D"}"
        } else {
            "Non monitorato"
        }

        val trainingStyle = when {
            analysis.avgIntensity > 2.5 -> "FORZA/POWERLIFTING"
            analysis.avgIntensity > 1.5 -> "IPERTROFIA"
            else -> "RESISTENZA"
        }

        // Istruzioni speciali per casi con pochi dati
        val specialInstructions = when {
            hasNoData -> """
IMPORTANTE: L'utente non ha ancora registrato allenamenti!
Genera consigli MOTIVANTI e PRATICI per iniziare il percorso fitness.
Suggerisci come creare il primo allenamento e quali obiettivi porsi.
Sii ENTUSIASTA e INCORAGGIANTE!
"""
            isNewUser -> """
IMPORTANTE: L'utente e agli inizi con ${analysis.totalWorkoutsLastMonth} allenament${if(analysis.totalWorkoutsLastMonth == 1) "o" else "i"}.
Genera consigli INCORAGGIANTI per mantenere la costanza.
Celebra i primi passi e suggerisci come costruire una routine solida.
Sii POSITIVO e dai consigli pratici per principianti!
"""
            else -> """
Genera consigli SPECIFICI basati sui dati reali mostrati sopra.
Usa i numeri e le percentuali per personalizzare i suggerimenti.
"""
        }

        return """<start_of_turn>user
Sei un coach fitness esperto, motivante e personalizzato. $profileIntro

$specialInstructions
VARIANT_ID: $variantId

=== PROFILO ALLENAMENTO ===
Tipo prevalente: $trainingStyle
Frequenza cardiaca: $heartRateInfo

=== FREQUENZA E CONSISTENZA (ultimi 30 giorni) ===
- Sessioni ultimo mese: ${analysis.totalWorkoutsLastMonth}
- Sessioni ultima settimana: ${analysis.totalWorkoutsLastWeek}
- Giorni dall'ultimo allenamento: ${analysis.daysSinceLastWorkout}
- Riposo medio tra sessioni: ${String.format("%.1f", analysis.avgRestDays)} giorni
- Schede completate: ${analysis.completedWorkouts}/${analysis.totalWorkoutsLastMonth}

=== CONSISTENZA E QUALITA ===
- Streak settimane consecutive: ${analysis.currentStreakWeeks}
- Adherence score (0-100): ${analysis.adherenceScore}
- Muscle balance score (0-100): ${analysis.muscleBalanceScore}
- Trend volume vs settimana precedente: ${analysis.volumeTrendPct?.let { "${it}%" } ?: "N/D"}

=== VOLUME SETTIMANA CORRENTE (sets*reps per gruppo) ===
$volumeThisWeek

=== ESERCIZI PIU FREQUENTI (con pesi) ===
$topExercises

=== DISTRIBUZIONE MUSCOLARE (%) ===
$muscleBalance

=== MUSCOLI ===
- Dominanti (>25%): ${analysis.dominantMuscles.ifEmpty { listOf("Equilibrato") }.joinToString(", ")}
- Trascurati (<10%): ${analysis.neglectedMuscles.ifEmpty { listOf("Nessuno") }.joinToString(", ")}

REGOLE EXTRA:
- Cita almeno 2 numeri presenti nei dati (score, streak, sessioni, trend%).
- Ogni suggestion deve essere misurabile (es: "+1 set", "-15s recupero", "2 sessioni entro domenica").
- Evita frasi generiche non legate ai dati.

RISPONDI SOLO CON JSON VALIDO (senza commenti):
{
  "weeklyMotivation": "Messaggio motivazionale ENTUSIASTA e SPECIFICO",
  "performanceSummary": "Riassunto breve della situazione attuale",
  "suggestions": [
    {"priority": "HIGH", "title": "Titolo breve", "description": "Descrizione pratica e specifica", "actionType": "LOAD|REST|MUSCLE|DELOAD"}
  ],
  "nextWorkoutFocus": {
    "muscleGroup": "Gruppo muscolare consigliato",
    "reason": "Motivo del suggerimento",
    "suggestedIntensity": "ALTA|MEDIA|BASSA",
    "suggestedExercises": ["Esercizio1", "Esercizio2", "Esercizio3"]
  },
  "loadRecommendation": {
    "trend": "INCREASE|MAINTAIN|DECREASE",
    "percentage": 5,
    "reason": "Spiegazione breve"
  },
  "restRecommendation": {
    "currentAvgDays": ${String.format("%.1f", analysis.avgRestDays)},
    "suggestedDays": 2,
    "reason": "Consiglio sul recupero"
  },
  "deloadNeeded": false,
  "deloadReason": null
}
<start_of_turn>model
"""
    }

    private fun parseGemmaInsights(
        response: String,
        analysis: WorkoutDataAnalysis,
        profileId: Int?,
        profileName: String?
    ): GemmaLiveInsights {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val engine = GemmaLlmHelper.getEngineType().displayName

        try {
            val jsonMatch = Regex("""\{[\s\S]*\}""").find(response)
            val jsonString = jsonMatch?.value ?: throw Exception("No JSON in response")
            val json = JSONObject(jsonString)

            val suggestions = mutableListOf<LiveSuggestion>()
            json.optJSONArray("suggestions")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val s = arr.getJSONObject(i)
                    suggestions.add(
                        LiveSuggestion(
                            priority = s.optString("priority", "MEDIUM"),
                            title = s.optString("title", ""),
                            description = s.optString("description", ""),
                            actionType = s.optString("actionType", "")
                        )
                    )
                }
            }

            val nextFocus = json.optJSONObject("nextWorkoutFocus")?.let { nf ->
                val exercises = mutableListOf<String>()
                nf.optJSONArray("suggestedExercises")?.let { arr ->
                    for (i in 0 until arr.length()) exercises.add(arr.getString(i))
                }
                NextWorkoutFocus(
                    muscleGroup = nf.optString("muscleGroup", ""),
                    reason = nf.optString("reason", ""),
                    suggestedIntensity = nf.optString("suggestedIntensity", "MEDIA"),
                    suggestedExercises = exercises
                )
            }

            val loadRec = json.optJSONObject("loadRecommendation")?.let { lr ->
                LoadRecommendation(
                    trend = when (lr.optString("trend", "MAINTAIN")) {
                        "INCREASE" -> LoadTrend.INCREASE
                        "DECREASE" -> LoadTrend.DECREASE
                        else -> LoadTrend.MAINTAIN
                    },
                    percentage = lr.optInt("percentage", 0),
                    reason = lr.optString("reason", "")
                )
            }

            val restRec = json.optJSONObject("restRecommendation")?.let { rr ->
                RestRecommendation(
                    currentAvgDays = rr.optDouble("currentAvgDays", 0.0).toFloat(),
                    suggestedDays = rr.optInt("suggestedDays", 2),
                    reason = rr.optString("reason", "")
                )
            }

            return GemmaLiveInsights(
                generatedDate = today,
                engineUsed = engine,
                profileId = profileId,
                profileName = profileName,
                weeklyMotivation = json.optString("weeklyMotivation", "Continua cosi!"),
                performanceSummary = json.optString("performanceSummary", ""),
                suggestions = suggestions,
                nextWorkoutFocus = nextFocus,
                loadRecommendation = loadRec,
                restRecommendation = restRec,
                deloadNeeded = json.optBoolean("deloadNeeded", false),
                deloadReason = json.optString("deloadReason").takeIf { it.isNotBlank() },
                weeklyStats = WeeklyStats(
                    sessionsThisWeek = analysis.totalWorkoutsLastWeek,
                    totalVolumeKg = analysis.volumePerMuscleThisWeek.values.sum(),
                    mostTrainedMuscle = analysis.muscleDistribution.maxByOrNull { it.percentuale }?.gruppoMuscolare ?: ""
                )
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Gemma response, using fallback", e)
            return createFallbackInsights(analysis, today, engine, profileId, profileName)
        }
    }

    private fun createFallbackInsights(
        analysis: WorkoutDataAnalysis,
        date: String,
        engine: String,
        profileId: Int?,
        profileName: String?
    ): GemmaLiveInsights {
        val suggestions = mutableListOf<LiveSuggestion>()
        val isNewUser = analysis.totalWorkoutsLastMonth < 3
        val hasNoData = analysis.totalWorkoutsLastMonth == 0

        // Motivazione personalizzata in base ai dati
        val weeklyMotivation = when {
            hasNoData -> "Inizia il tuo percorso fitness! Ogni grande trasformazione inizia con il primo passo."
            isNewUser -> "Ottimo inizio! Hai fatto ${analysis.totalWorkoutsLastMonth} allenament${if(analysis.totalWorkoutsLastMonth == 1) "o" else "i"}. La costanza e la chiave del successo!"
            analysis.totalWorkoutsLastWeek >= 4 -> "Settimana fantastica! ${analysis.totalWorkoutsLastWeek} sessioni completate. Stai costruendo abitudini solide!"
            analysis.totalWorkoutsLastWeek >= 2 -> "Buon ritmo con ${analysis.totalWorkoutsLastWeek} allenamenti questa settimana. Continua cosi!"
            analysis.daysSinceLastWorkout > 7 -> "E il momento di tornare in palestra! Il tuo corpo ti ringraziera."
            else -> "Hai completato ${analysis.totalWorkoutsLastWeek} allenament${if(analysis.totalWorkoutsLastWeek == 1) "o" else "i"} questa settimana. Ogni sessione conta!"
        }

        // Performance summary
        val performanceSummary = when {
            hasNoData -> "Nessuna sessione registrata. Aggiungi il tuo primo allenamento!"
            isNewUser -> "Stai costruendo le basi. ${analysis.totalWorkoutsLastMonth} session${if(analysis.totalWorkoutsLastMonth == 1) "e" else "i"} registrat${if(analysis.totalWorkoutsLastMonth == 1) "a" else "e"}."
            else -> "${analysis.totalWorkoutsLastMonth} sessioni nell'ultimo mese${if(analysis.completedWorkouts > 0) ", ${analysis.completedWorkouts} completate" else ""}"
        }

        // Suggerimenti intelligenti basati sui dati disponibili
        if (hasNoData) {
            suggestions.add(
                LiveSuggestion("HIGH", "Crea il tuo primo allenamento", "Inizia con una sessione leggera per testare il tuo livello attuale", "LOAD")
            )
            suggestions.add(
                LiveSuggestion("MEDIUM", "Definisci i tuoi obiettivi", "Forza, ipertrofia o resistenza? Scegli il focus del tuo programma", "MUSCLE")
            )
            suggestions.add(
                LiveSuggestion("LOW", "Pianifica la settimana", "3-4 sessioni settimanali sono ideali per iniziare", "REST")
            )
        } else if (isNewUser) {
            suggestions.add(
                LiveSuggestion("HIGH", "Mantieni la costanza", "Le prime settimane sono cruciali. Punta a 3 allenamenti a settimana", "REST")
            )
            if (analysis.muscleDistribution.isNotEmpty()) {
                val topMuscle = analysis.muscleDistribution.maxByOrNull { it.percentuale }
                suggestions.add(
                    LiveSuggestion("MEDIUM", "Bilancia il programma", "Stai allenando principalmente ${topMuscle?.gruppoMuscolare ?: "un gruppo"}. Varia gli esercizi!", "MUSCLE")
                )
            }
            suggestions.add(
                LiveSuggestion("LOW", "Progressione graduale", "Aumenta i carichi del 5-10% quando completi tutte le ripetizioni", "LOAD")
            )
        } else {
            // Utente con piu dati
            if (analysis.daysSinceLastWorkout > 4) {
                suggestions.add(
                    LiveSuggestion("HIGH", "Riprendi ad allenarti", "Sono passati ${analysis.daysSinceLastWorkout} giorni dall'ultimo allenamento", "REST")
                )
            }

            if (analysis.neglectedMuscles.isNotEmpty()) {
                suggestions.add(
                    LiveSuggestion("MEDIUM", "Bilancia l'allenamento", "Muscoli trascurati: ${analysis.neglectedMuscles.take(3).joinToString()}", "MUSCLE")
                )
            }

            if (analysis.totalWorkoutsLastMonth > 16) {
                suggestions.add(
                    LiveSuggestion("LOW", "Valuta un deload", "Hai fatto ${analysis.totalWorkoutsLastMonth} sessioni questo mese - considera una settimana leggera", "DELOAD")
                )
            }

            // Se non ci sono altri suggerimenti, aggiungi qualcosa di utile
            if (suggestions.isEmpty()) {
                if (analysis.avgIntensity < 2f) {
                    suggestions.add(
                        LiveSuggestion("MEDIUM", "Aumenta l'intensita", "La maggior parte dei tuoi allenamenti e a bassa intensita. Prova a spingerti di piu!", "LOAD")
                    )
                } else {
                    suggestions.add(
                        LiveSuggestion("LOW", "Ottimo lavoro!", "Continua con questo ritmo. Monitora i progressi sui carichi", "LOAD")
                    )
                }
            }
        }

        // Next workout focus
        val nextFocus = when {
            hasNoData -> NextWorkoutFocus(
                muscleGroup = "Full Body",
                reason = "Inizia con un allenamento completo per valutare il tuo livello",
                suggestedIntensity = "BASSA",
                suggestedExercises = listOf("Squat", "Push-up", "Rematore", "Plank")
            )
            analysis.neglectedMuscles.isNotEmpty() -> NextWorkoutFocus(
                muscleGroup = analysis.neglectedMuscles.first(),
                reason = "Non lo alleni da tempo - ottima opportunita per bilanciare",
                suggestedIntensity = "MEDIA",
                suggestedExercises = getSuggestedExercises(analysis.neglectedMuscles.first())
            )
            analysis.muscleDistribution.isNotEmpty() -> {
                val leastTrained = analysis.muscleDistribution.minByOrNull { it.percentuale }
                NextWorkoutFocus(
                    muscleGroup = leastTrained?.gruppoMuscolare ?: "Full Body",
                    reason = "Solo ${String.format("%.0f", leastTrained?.percentuale ?: 0f)}% del volume totale - spazio per migliorare!",
                    suggestedIntensity = "MEDIA",
                    suggestedExercises = getSuggestedExercises(leastTrained?.gruppoMuscolare ?: "")
                )
            }
            else -> null
        }

        // Load e Rest recommendations
        val loadRec = when {
            hasNoData -> LoadRecommendation(LoadTrend.MAINTAIN, 0, "Inizia con carichi leggeri per imparare i movimenti")
            isNewUser -> LoadRecommendation(LoadTrend.MAINTAIN, 0, "Concentrati sulla tecnica prima di aumentare i pesi")
            analysis.avgIntensity > 2.5f -> LoadRecommendation(LoadTrend.MAINTAIN, 0, "Buona intensita! Mantieni questi carichi")
            else -> LoadRecommendation(LoadTrend.INCREASE, 5, "Prova ad aumentare i carichi del 5%")
        }

        val restRec = when {
            hasNoData -> RestRecommendation(0f, 2, "Prevedi 1-2 giorni di riposo tra le sessioni")
            analysis.avgRestDays < 1f -> RestRecommendation(analysis.avgRestDays, 2, "Stai allenandoti molto frequentemente. Il recupero e importante!")
            analysis.avgRestDays > 4f -> RestRecommendation(analysis.avgRestDays, 2, "Prova ad aumentare la frequenza per risultati migliori")
            else -> RestRecommendation(analysis.avgRestDays, 2, "Buon equilibrio tra allenamento e recupero")
        }

        return GemmaLiveInsights(
            generatedDate = date,
            engineUsed = engine,
            profileId = profileId,
            profileName = profileName,
            weeklyMotivation = weeklyMotivation,
            performanceSummary = performanceSummary,
            suggestions = suggestions,
            nextWorkoutFocus = nextFocus,
            loadRecommendation = loadRec,
            restRecommendation = restRec,
            deloadNeeded = analysis.totalWorkoutsLastMonth > 20,
            deloadReason = if (analysis.totalWorkoutsLastMonth > 20) "Volume alto nell'ultimo mese" else null,
            weeklyStats = WeeklyStats(
                sessionsThisWeek = analysis.totalWorkoutsLastWeek,
                totalVolumeKg = analysis.volumePerMuscleThisWeek.values.sum(),
                mostTrainedMuscle = analysis.muscleDistribution.maxByOrNull { it.percentuale }?.gruppoMuscolare ?: ""
            )
        )
    }

    private fun getSuggestedExercises(muscleGroup: String): List<String> {
        return when (muscleGroup.lowercase()) {
            "petto", "pettorali", "chest" -> listOf("Panca piana", "Croci ai cavi", "Push-up")
            "dorso", "schiena", "back" -> listOf("Lat machine", "Rematore", "Pull-up")
            "spalle", "deltoidi", "shoulders" -> listOf("Lento avanti", "Alzate laterali", "Face pull")
            "gambe", "quadricipiti", "legs" -> listOf("Squat", "Leg press", "Affondi")
            "bicipiti", "biceps" -> listOf("Curl bilanciere", "Curl manubri", "Hammer curl")
            "tricipiti", "triceps" -> listOf("French press", "Push-down", "Dip")
            "addominali", "core", "abs" -> listOf("Crunch", "Plank", "Leg raise")
            "glutei", "glutes" -> listOf("Hip thrust", "Squat bulgaro", "Ponte")
            else -> listOf("Esercizio composto", "Esercizio di isolamento")
        }
    }

    private fun insightsToJson(insights: GemmaLiveInsights): String {
        return JSONObject().apply {
            put("generatedDate", insights.generatedDate)
            put("engineUsed", insights.engineUsed)
            put("profileId", insights.profileId ?: JSONObject.NULL)
            put("profileName", insights.profileName ?: JSONObject.NULL)
            put("weeklyMotivation", insights.weeklyMotivation)
            put("performanceSummary", insights.performanceSummary)
            put("deloadNeeded", insights.deloadNeeded)
            put("deloadReason", insights.deloadReason ?: JSONObject.NULL)
            put("suggestions", org.json.JSONArray().apply {
                insights.suggestions.forEach { suggestion ->
                    put(JSONObject().apply {
                        put("priority", suggestion.priority)
                        put("title", suggestion.title)
                        put("description", suggestion.description)
                        put("actionType", suggestion.actionType)
                    })
                }
            })
            put("nextWorkoutFocus", insights.nextWorkoutFocus?.let { focus ->
                JSONObject().apply {
                    put("muscleGroup", focus.muscleGroup)
                    put("reason", focus.reason)
                    put("suggestedIntensity", focus.suggestedIntensity)
                    put("suggestedExercises", org.json.JSONArray(focus.suggestedExercises))
                }
            } ?: JSONObject.NULL)
            put("loadRecommendation", insights.loadRecommendation?.let { rec ->
                JSONObject().apply {
                    put("trend", rec.trend.name)
                    put("percentage", rec.percentage)
                    put("reason", rec.reason)
                }
            } ?: JSONObject.NULL)
            put("restRecommendation", insights.restRecommendation?.let { rec ->
                JSONObject().apply {
                    put("currentAvgDays", rec.currentAvgDays)
                    put("suggestedDays", rec.suggestedDays)
                    put("reason", rec.reason)
                }
            } ?: JSONObject.NULL)
            put("weeklyStats", JSONObject().apply {
                put("sessionsThisWeek", insights.weeklyStats.sessionsThisWeek)
                put("totalVolumeKg", insights.weeklyStats.totalVolumeKg)
                put("mostTrainedMuscle", insights.weeklyStats.mostTrainedMuscle)
            })
        }.toString()
    }

    private fun parseInsightsFromJson(json: String): GemmaLiveInsights {
        val obj = JSONObject(json)
        val statsObj = obj.optJSONObject("weeklyStats")
        val suggestions = mutableListOf<LiveSuggestion>()
        obj.optJSONArray("suggestions")?.let { arr ->
            for (i in 0 until arr.length()) {
                val s = arr.getJSONObject(i)
                suggestions.add(
                    LiveSuggestion(
                        priority = s.optString("priority", "MEDIUM"),
                        title = s.optString("title", ""),
                        description = s.optString("description", ""),
                        actionType = s.optString("actionType", "")
                    )
                )
            }
        }

        val nextFocus = obj.optJSONObject("nextWorkoutFocus")?.let { nf ->
            val exercises = mutableListOf<String>()
            nf.optJSONArray("suggestedExercises")?.let { arr ->
                for (i in 0 until arr.length()) exercises.add(arr.getString(i))
            }
            NextWorkoutFocus(
                muscleGroup = nf.optString("muscleGroup", ""),
                reason = nf.optString("reason", ""),
                suggestedIntensity = nf.optString("suggestedIntensity", "MEDIA"),
                suggestedExercises = exercises
            )
        }

        val loadRec = obj.optJSONObject("loadRecommendation")?.let { lr ->
            LoadRecommendation(
                trend = when (lr.optString("trend", "MAINTAIN")) {
                    "INCREASE" -> LoadTrend.INCREASE
                    "DECREASE" -> LoadTrend.DECREASE
                    else -> LoadTrend.MAINTAIN
                },
                percentage = lr.optInt("percentage", 0),
                reason = lr.optString("reason", "")
            )
        }

        val restRec = obj.optJSONObject("restRecommendation")?.let { rr ->
            RestRecommendation(
                currentAvgDays = rr.optDouble("currentAvgDays", 0.0).toFloat(),
                suggestedDays = rr.optInt("suggestedDays", 2),
                reason = rr.optString("reason", "")
            )
        }

        return GemmaLiveInsights(
            generatedDate = obj.optString("generatedDate"),
            engineUsed = obj.optString("engineUsed"),
            profileId = if (obj.isNull("profileId")) null else obj.optInt("profileId"),
            profileName = if (obj.isNull("profileName")) null else obj.optString("profileName"),
            weeklyMotivation = obj.optString("weeklyMotivation"),
            performanceSummary = obj.optString("performanceSummary"),
            suggestions = suggestions,
            nextWorkoutFocus = nextFocus,
            loadRecommendation = loadRec,
            restRecommendation = restRec,
            deloadNeeded = obj.optBoolean("deloadNeeded"),
            deloadReason = if (obj.isNull("deloadReason")) null else obj.optString("deloadReason"),
            weeklyStats = WeeklyStats(
                sessionsThisWeek = statsObj?.optInt("sessionsThisWeek") ?: 0,
                totalVolumeKg = statsObj?.optDouble("totalVolumeKg")?.toFloat() ?: 0f,
                mostTrainedMuscle = statsObj?.optString("mostTrainedMuscle") ?: ""
            )
        )
    }

    private fun parseLocalDate(value: String): LocalDate? {
        if (value.isBlank()) return null
        val datePart = value.trim().let { if (it.length >= 10) it.substring(0, 10) else it }
        val formats = listOf(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )
        for (formatter in formats) {
            try {
                return LocalDate.parse(datePart, formatter)
            } catch (_: Exception) {}
        }
        return null
    }
}

// Data classes
data class WorkoutDataAnalysis(
    val totalWorkoutsLastMonth: Int,
    val totalWorkoutsLastWeek: Int,
    val avgRestDays: Float,
    val daysSinceLastWorkout: Int,
    val avgIntensity: Float,
    val volumePerMuscleThisWeek: Map<String, Float>,
    val exerciseStats: Map<String, ExerciseStats>,
    val muscleDistribution: List<GruppoMuscolarePercentuale>,
    val neglectedMuscles: List<String>,
    val dominantMuscles: List<String>,
    val lastWorkoutDate: LocalDate?,
    val trainingTimeDistribution: Map<String, Int>,
    val avgHeartRate: Int?,
    val maxHeartRate: Int?,
    val completedWorkouts: Int,
    val currentStreakWeeks: Int,
    val adherenceScore: Int,
    val muscleBalanceScore: Int,
    val volumeTrendPct: Int?
)

data class ExerciseStats(
    val count: Int,
    val avgSets: Float,
    val avgReps: Float,
    val avgWeight: Float?,
    val maxWeight: Float?
)

data class GemmaLiveInsights(
    val generatedDate: String,
    val engineUsed: String,
    val profileId: Int?,
    val profileName: String?,
    val weeklyMotivation: String,
    val performanceSummary: String,
    val suggestions: List<LiveSuggestion>,
    val nextWorkoutFocus: NextWorkoutFocus?,
    val loadRecommendation: LoadRecommendation?,
    val restRecommendation: RestRecommendation?,
    val deloadNeeded: Boolean,
    val deloadReason: String?,
    val weeklyStats: WeeklyStats
)

data class LiveSuggestion(
    val priority: String,
    val title: String,
    val description: String,
    val actionType: String
)

data class NextWorkoutFocus(
    val muscleGroup: String,
    val reason: String,
    val suggestedIntensity: String,
    val suggestedExercises: List<String>
)

data class LoadRecommendation(
    val trend: LoadTrend,
    val percentage: Int,
    val reason: String
)

enum class LoadTrend { INCREASE, MAINTAIN, DECREASE }

data class RestRecommendation(
    val currentAvgDays: Float,
    val suggestedDays: Int,
    val reason: String
)

data class WeeklyStats(
    val sessionsThisWeek: Int,
    val totalVolumeKg: Float,
    val mostTrainedMuscle: String
)
