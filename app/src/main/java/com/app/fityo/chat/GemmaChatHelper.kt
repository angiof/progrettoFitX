package com.app.fityo.chat

import android.content.Context
import android.util.Log
import com.app.fityo.chat.data.ChatIntent
import com.app.fityo.chat.engine.*
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.import_scheda.GemmaLlmHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Helper per gestire le conversazioni del chatbot fitness.
 * Integra intent classification, context fetching e Gemma LLM.
 */
class GemmaChatHelper(
    private val context: Context,
    private val db: DbFit
) {
    private val gemma = GemmaLlmHelper.getInstance(context)
    private val dataProvider = WorkoutDataProvider(db)
    private val classifier = IntentClassifier()

    // Cache dei profili per lookup per nome
    private var profilesCache: List<com.app.fityo.data_layer.db.CoachProfileEntity> = emptyList()

    companion object {
        private const val TAG = "GemmaChatHelper"
    }

    /**
     * Aggiorna la cache dei profili.
     */
    suspend fun refreshProfilesCache() {
        profilesCache = db.coachProfileDao().getAllProfilesSync()
    }

    /**
     * Cerca un profilo per nome nella domanda.
     */
    private suspend fun findProfileByNameInQuestion(question: String): Int? {
        if (profilesCache.isEmpty()) {
            refreshProfilesCache()
        }

        val normalized = question.lowercase()
        for (profile in profilesCache) {
            if (normalized.contains(profile.name.lowercase())) {
                Log.d(TAG, "Found profile '${profile.name}' (id=${profile.id}) in question")
                return profile.id
            }
        }
        return null
    }

    /**
     * Estrae il nome del profilo dalla domanda se presente.
     */
    private suspend fun extractProfileName(question: String): String? {
        if (profilesCache.isEmpty()) {
            refreshProfilesCache()
        }

        val normalized = question.lowercase()
        for (profile in profilesCache) {
            if (normalized.contains(profile.name.lowercase())) {
                return profile.name
            }
        }
        return null
    }

    /**
     * Processa una domanda dell'utente e genera una risposta.
     * Usa Gemma se disponibile, altrimenti fallback rule-based.
     */
    suspend fun processQuestion(
        question: String,
        profileId: Int?
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // 0. Cerca se c'e un nome di profilo nella domanda
            val profileFromQuestion = findProfileByNameInQuestion(question)
            val effectiveProfileId = profileFromQuestion ?: profileId
            val profileName = extractProfileName(question)

            Log.d(TAG, "Processing question - profileFromQuestion: $profileFromQuestion, effectiveProfileId: $effectiveProfileId, profileName: $profileName")

            // 1. Classifica l'intent
            val intent = classifier.classify(question)
            Log.d(TAG, "Classified intent: ${intent.name} for question: $question")

            // 2. Genera risposta rule-based (dati sempre corretti dal DB)
            val ruleBasedResponse = generateFallbackResponse(intent, question, effectiveProfileId, profileName)

            // 3. Se Gemma e disponibile, prova a riformulare in modo piu naturale
            // Gemma NON interpreta i dati, solo riformula il testo gia corretto
            if (gemma.isModelAvailable()) {
                try {
                    if (!gemma.isReady()) {
                        gemma.ensureReady(GemmaLlmHelper.GemmaProfile.ANALYTICS)
                    }

                    val reformulated = reformulateWithGemma(ruleBasedResponse)
                    if (reformulated != null) {
                        Log.d(TAG, "Gemma reformulated response successfully")
                        return@withContext Result.success(reformulated)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Gemma reformulation failed, using rule-based", e)
                }
            }

            // Fallback: usa risposta rule-based originale (sempre corretta)
            return@withContext Result.success(ruleBasedResponse)

        } catch (e: Exception) {
            Log.e(TAG, "Error processing question", e)
            Result.failure(e)
        }
    }

    /**
     * Genera una risposta senza Gemma (fallback rule-based).
     */
    suspend fun processQuestionWithoutGemma(
        question: String,
        profileId: Int?
    ): String = withContext(Dispatchers.IO) {
        // Cerca se c'e un nome di profilo nella domanda
        val profileFromQuestion = findProfileByNameInQuestion(question)
        val effectiveProfileId = profileFromQuestion ?: profileId
        val profileName = extractProfileName(question)

        val intent = classifier.classify(question)
        generateFallbackResponse(intent, question, effectiveProfileId, profileName)
    }

    /**
     * Verifica se Gemma e disponibile.
     */
    fun isGemmaAvailable(): Boolean = gemma.isModelAvailable()

    /**
     * Verifica se Gemma e pronto (gia caricato).
     */
    fun isGemmaReady(): Boolean = gemma.isReady()

    // ==================== PROMPT BUILDING ====================

    private suspend fun buildPromptForIntent(
        intent: ChatIntent,
        question: String,
        profileId: Int?
    ): String {
        return when (intent) {
            is ChatIntent.HistoryQuery -> {
                val ctx = buildHistoryContext(question, profileId)
                PromptTemplates.buildHistoryPrompt(ctx, question)
            }
            is ChatIntent.PerformanceQuery -> {
                val ctx = buildPerformanceContext(question, profileId)
                PromptTemplates.buildPerformancePrompt(ctx, question)
            }
            is ChatIntent.PlanningQuery -> {
                val ctx = buildPlanningContext(question, profileId)
                PromptTemplates.buildPlanningPrompt(ctx, question)
            }
            is ChatIntent.ComparisonQuery -> {
                val ctx = buildComparisonContext(profileId)
                PromptTemplates.buildComparisonPrompt(ctx, question)
            }
            is ChatIntent.EquipmentQuery -> {
                val ctx = buildEquipmentContext(question, profileId)
                PromptTemplates.buildEquipmentPrompt(ctx, question)
            }
            is ChatIntent.ProfileQuery -> {
                // ProfileQuery usa solo fallback, ma aggiungiamo il caso per completezza
                val ctx = buildGeneralContext(profileId)
                PromptTemplates.buildGeneralPrompt(ctx, question)
            }
            is ChatIntent.GeneralQuery, is ChatIntent.Unknown -> {
                val ctx = buildGeneralContext(profileId)
                PromptTemplates.buildGeneralPrompt(ctx, question)
            }
        }
    }

    // ==================== CONTEXT BUILDERS ====================

    private suspend fun buildHistoryContext(question: String, profileId: Int?): HistoryContext {
        val targetMuscle = classifier.extractMuscleGroup(question)
        val targetDay = classifier.extractDayOfWeek(question)

        val recentWorkouts = dataProvider.getLastNWorkouts(10, profileId)
        val lastWorkout = if (targetMuscle != null) {
            dataProvider.getLastWorkoutByMuscle(targetMuscle, profileId)
        } else {
            recentWorkouts.firstOrNull()
        }

        return HistoryContext(
            recentWorkouts = recentWorkouts,
            lastWorkout = lastWorkout,
            averageTime = dataProvider.getAverageGymTime(profileId),
            daysSinceLastWorkout = dataProvider.getDaysSinceLastWorkout(profileId),
            targetMuscle = targetMuscle,
            targetDay = targetDay
        )
    }

    private suspend fun buildPerformanceContext(question: String, profileId: Int?): PerformanceContext {
        val exerciseName = classifier.extractExerciseName(question) ?: "esercizio"
        val progression = dataProvider.getExerciseProgression(exerciseName, 10, profileId)

        val weights = progression.mapNotNull { it.weight }
        val averageWeight = if (weights.isNotEmpty()) weights.average().toFloat() else null
        val maxWeight = dataProvider.getMaxWeight(exerciseName, profileId)

        val trend = when {
            progression.size < 2 -> "dati insufficienti"
            weights.isEmpty() -> "nessun dato peso"
            else -> {
                val recentAvg = weights.take(3).average()
                val olderAvg = weights.takeLast(3).average()
                when {
                    recentAvg > olderAvg * 1.05 -> "in crescita"
                    recentAvg < olderAvg * 0.95 -> "in calo"
                    else -> "stabile"
                }
            }
        }

        return PerformanceContext(
            exerciseName = exerciseName,
            progression = progression,
            maxWeight = maxWeight,
            averageWeight = averageWeight,
            trend = trend,
            lastDetails = dataProvider.getLastExerciseDetails(exerciseName, profileId)
        )
    }

    private suspend fun buildPlanningContext(question: String, profileId: Int?): PlanningContext {
        val requestedCount = classifier.extractNumber(question)
        val recentDays = 7

        val recentExercises = dataProvider.getExercisesFromLastNDays(recentDays, profileId)
        val recentMuscleGroups = dataProvider.getMuscleGroupsTrainedRecently(3, profileId)

        // Muscoli da evitare = quelli allenati negli ultimi 2 giorni
        val musclesToAvoid = dataProvider.getMuscleGroupsTrainedRecently(2, profileId)

        val availableExercises = dataProvider.getAllAvailableExercises(profileId)

        return PlanningContext(
            recentDays = recentDays,
            recentExercises = recentExercises,
            recentMuscleGroups = recentMuscleGroups,
            musclesToAvoid = musclesToAvoid,
            availableExercises = availableExercises,
            requestedCount = requestedCount
        )
    }

    private suspend fun buildGeneralContext(profileId: Int?): GeneralContext {
        return GeneralContext(
            weeklyCount = dataProvider.getWeeklyWorkoutCount(profileId),
            monthlyCount = dataProvider.getMonthlyWorkoutCount(profileId),
            mostTrainedMuscle = dataProvider.getMostTrainedMuscle(profileId),
            daysSinceLastWorkout = dataProvider.getDaysSinceLastWorkout(profileId),
            weeklyAverage = dataProvider.getWeeklyAverage(profileId)
        )
    }

    private suspend fun buildComparisonContext(profileId: Int?): ComparisonContext {
        val volumeComparison = dataProvider.compareWeeklyVolume(profileId)
        val frequencyComparison = dataProvider.compareMonthlyFrequency(profileId)

        return ComparisonContext(
            currentWeekVolume = volumeComparison.currentWeekVolume,
            previousWeekVolume = volumeComparison.previousWeekVolume,
            volumeChange = volumeComparison.changePercent,
            currentMonthWorkouts = frequencyComparison.currentMonthWorkouts,
            previousMonthWorkouts = frequencyComparison.previousMonthWorkouts,
            frequencyChange = frequencyComparison.changePercent
        )
    }

    private suspend fun buildEquipmentContext(question: String, profileId: Int?): EquipmentContext {
        val targetMuscle = classifier.extractMuscleGroup(question)

        val equipmentForMuscle = if (targetMuscle != null) {
            dataProvider.getEquipmentForMuscle(targetMuscle, profileId)
        } else {
            emptyList()
        }

        val mostUsedEquipment = dataProvider.getMostUsedEquipment(5, profileId)

        // Ultimo attrezzo usato
        val lastExercise = dataProvider.getLastNWorkouts(1, profileId).firstOrNull()?.id?.let { schedaId ->
            db.essercissiDao().getEserciziByschedaIdSync(schedaId).lastOrNull()
        }

        return EquipmentContext(
            targetMuscle = targetMuscle,
            equipmentForMuscle = equipmentForMuscle,
            mostUsedEquipment = mostUsedEquipment,
            lastExerciseEquipment = lastExercise?.attrezzo?.takeIf { it.isNotBlank() }
        )
    }

    // ==================== FALLBACK RESPONSES ====================

    private suspend fun generateFallbackResponse(
        intent: ChatIntent,
        question: String,
        profileId: Int?,
        profileName: String? = null
    ): String {
        // Determina il soggetto della frase basato sul profilo
        val subject = if (profileName != null) profileName else "Tu"
        val possessive = if (profileName != null) "di $profileName" else "tuo/a"
        val verb = if (profileName != null) "ha" else "hai"

        return when (intent) {
            is ChatIntent.HistoryQuery -> generateHistoryFallback(question, profileId, subject, possessive, verb)
            is ChatIntent.PerformanceQuery -> generatePerformanceFallback(question, profileId, subject, possessive, verb)
            is ChatIntent.PlanningQuery -> generatePlanningFallback(question, profileId, subject, possessive, verb)
            is ChatIntent.ComparisonQuery -> generateComparisonFallback(profileId, subject, possessive)
            is ChatIntent.EquipmentQuery -> generateEquipmentFallback(question, profileId, subject, possessive, verb)
            is ChatIntent.ProfileQuery -> generateProfileFallback()
            is ChatIntent.GeneralQuery, is ChatIntent.Unknown -> generateGeneralFallback(profileId, subject, possessive)
        }
    }

    private suspend fun generateHistoryFallback(
        question: String,
        profileId: Int?,
        subject: String,
        possessive: String,
        verb: String
    ): String {
        val targetMuscle = classifier.extractMuscleGroup(question)

        if (targetMuscle != null) {
            val lastWorkout = dataProvider.getLastWorkoutByMuscle(targetMuscle, profileId)
            return if (lastWorkout != null) {
                "L'ultima volta che $subject $verb allenato $targetMuscle e stata il ${lastWorkout.data}${lastWorkout.ora?.let { " alle $it" } ?: ""}."
            } else {
                "Non ho trovato allenamenti per $targetMuscle nello storico $possessive."
            }
        }

        val lastWorkout = dataProvider.getLastNWorkouts(1, profileId).firstOrNull()
        return if (lastWorkout != null) {
            val daysSince = dataProvider.getDaysSinceLastWorkout(profileId)
            "L'ultimo allenamento $possessive e stato il ${lastWorkout.data} (${lastWorkout.gruppoMuscolare}). Sono passati $daysSince giorni."
        } else {
            "Non ci sono ancora allenamenti registrati per $subject."
        }
    }

    private suspend fun generatePerformanceFallback(
        question: String,
        profileId: Int?,
        subject: String,
        possessive: String,
        verb: String
    ): String {
        val exerciseName = classifier.extractExerciseName(question)

        if (exerciseName != null) {
            val maxWeight = dataProvider.getMaxWeight(exerciseName, profileId)
            val lastDetails = dataProvider.getLastExerciseDetails(exerciseName, profileId)

            return buildString {
                if (maxWeight != null) {
                    append("Il peso massimo $possessive in $exerciseName e ${maxWeight}kg. ")
                }
                if (lastDetails != null) {
                    append("Ultimo allenamento: ${lastDetails.nSerie}x${lastDetails.nRipetizione}")
                    lastDetails.peso?.let { append(" con ${it}kg") }
                    append(".")
                }
                if (isEmpty()) {
                    append("Non ho trovato dati per $exerciseName nello storico $possessive.")
                }
            }
        }

        return "Dimmi quale esercizio ti interessa e ti daro i dettagli sulla progressione."
    }

    private suspend fun generatePlanningFallback(
        question: String,
        profileId: Int?,
        subject: String,
        possessive: String,
        verb: String
    ): String {
        val requestedCount = classifier.extractNumber(question) ?: 4
        val recentMuscles = dataProvider.getMuscleGroupsTrainedRecently(2, profileId)
        val availableExercises = dataProvider.getAllAvailableExercises(profileId)

        if (availableExercises.isEmpty()) {
            return "Non ci sono ancora esercizi registrati per $subject. Inizia a tracciare gli allenamenti!"
        }

        // Suggerisci esercizi non fatti di recente
        val recentExercises = dataProvider.getExercisesFromLastNDays(3, profileId)
        val suggestions = availableExercises
            .filter { it !in recentExercises }
            .shuffled()
            .take(requestedCount)

        return if (suggestions.isNotEmpty()) {
            buildString {
                append("Ecco $requestedCount esercizi consigliati per $subject:\n")
                suggestions.forEachIndexed { index, ex ->
                    append("${index + 1}. $ex\n")
                }
                if (recentMuscles.isNotEmpty()) {
                    append("\nEvita ${recentMuscles.joinToString(", ")} (allenati di recente).")
                }
            }
        } else {
            "$subject $verb fatto tutti gli esercizi del repertorio di recente! Meglio riposare o provare qualcosa di nuovo."
        }
    }

    private suspend fun generateComparisonFallback(
        profileId: Int?,
        subject: String,
        possessive: String
    ): String {
        val volumeComp = dataProvider.compareWeeklyVolume(profileId)
        val freqComp = dataProvider.compareMonthlyFrequency(profileId)

        return buildString {
            append("Confronto $possessive con il periodo precedente:\n")
            append("- Volume settimanale: ${volumeComp.currentWeekVolume} vs ${volumeComp.previousWeekVolume} ")
            append("(${if (volumeComp.changePercent >= 0) "+" else ""}${volumeComp.changePercent}%)\n")
            append("- Frequenza mensile: ${freqComp.currentMonthWorkouts} vs ${freqComp.previousMonthWorkouts} allenamenti ")
            append("(${if (freqComp.changePercent >= 0) "+" else ""}${freqComp.changePercent}%)")
        }
    }

    private suspend fun generateEquipmentFallback(
        question: String,
        profileId: Int?,
        subject: String,
        possessive: String,
        verb: String
    ): String {
        val targetMuscle = classifier.extractMuscleGroup(question)

        if (targetMuscle != null) {
            val equipment = dataProvider.getEquipmentForMuscle(targetMuscle, profileId)
            return if (equipment.isNotEmpty()) {
                "Per $targetMuscle $subject $verb usato: ${equipment.joinToString(", ")}."
            } else {
                "Non ho dati sugli attrezzi usati per $targetMuscle nello storico $possessive."
            }
        }

        val mostUsed = dataProvider.getMostUsedEquipment(5, profileId)
        return if (mostUsed.isNotEmpty()) {
            buildString {
                append("Gli attrezzi piu usati $possessive:\n")
                mostUsed.forEach { (name, count) ->
                    append("- $name: $count volte\n")
                }
            }
        } else {
            "Non ci sono ancora attrezzi registrati negli allenamenti $possessive."
        }
    }

    private suspend fun generateGeneralFallback(
        profileId: Int?,
        subject: String,
        possessive: String
    ): String {
        val weeklyCount = dataProvider.getWeeklyWorkoutCount(profileId)
        val monthlyCount = dataProvider.getMonthlyWorkoutCount(profileId)
        val mostTrained = dataProvider.getMostTrainedMuscle(profileId)
        val daysSince = dataProvider.getDaysSinceLastWorkout(profileId)

        return buildString {
            append("Statistiche $possessive:\n")
            append("- Allenamenti questa settimana: $weeklyCount\n")
            append("- Allenamenti questo mese: $monthlyCount\n")
            if (mostTrained != null) {
                append("- Muscolo piu allenato: $mostTrained\n")
            }
            if (daysSince >= 0) {
                append("- Giorni dall'ultimo allenamento: $daysSince")
            }
        }
    }

    private suspend fun generateProfileFallback(): String {
        if (profilesCache.isEmpty()) {
            refreshProfilesCache()
        }

        return if (profilesCache.isNotEmpty()) {
            buildString {
                append("Ecco i profili disponibili:\n")
                profilesCache.forEach { profile ->
                    append("- ${profile.name}\n")
                }
                append("\nPuoi chiedermi informazioni su un profilo specifico, ad esempio: \"Cosa ha fatto ${profilesCache.firstOrNull()?.name ?: "Marco"}?\"")
            }
        } else {
            "Non ci sono ancora profili registrati. Puoi creare un profilo dalla Dashboard."
        }
    }

    // ==================== GEMMA REFORMULATION ====================

    /**
     * Riformula una risposta rule-based usando Gemma.
     * Il prompt e MOLTO semplice - Gemma deve solo riscrivere, non interpretare dati.
     */
    private suspend fun reformulateWithGemma(ruleBasedResponse: String): String? {
        // Prompt ultra-semplice per Gemma 2B
        // NON chiediamo di interpretare dati, solo di riscrivere il testo
        val prompt = """
<start_of_turn>user
Riscrivi in modo amichevole:
$ruleBasedResponse<end_of_turn>
<start_of_turn>model
""".trimIndent()

        return try {
            val result = gemma.generateResponse(prompt, GemmaLlmHelper.GemmaProfile.ANALYTICS)

            result.getOrNull()?.let { rawResponse ->
                val cleaned = cleanGemmaResponse(rawResponse)

                // Verifica che la risposta sia valida
                if (cleaned.isBlank() || cleaned.length < 10) {
                    Log.w(TAG, "Gemma response too short, using rule-based")
                    return null
                }

                // Verifica che contenga almeno un numero/dato della risposta originale
                // Questo assicura che Gemma non abbia inventato dati
                val numbersInOriginal = Regex("\\d+").findAll(ruleBasedResponse)
                    .map { it.value }.toSet()

                val numbersInReformulated = Regex("\\d+").findAll(cleaned)
                    .map { it.value }.toSet()

                // Se l'originale aveva numeri, la riformulazione deve averne almeno uno
                if (numbersInOriginal.isNotEmpty() && numbersInReformulated.isEmpty()) {
                    Log.w(TAG, "Gemma dropped all numbers, using rule-based")
                    return null
                }

                cleaned
            }
        } catch (e: Exception) {
            Log.w(TAG, "Gemma reformulation error", e)
            null
        }
    }

    // ==================== UTILITY ====================

    private fun cleanGemmaResponse(response: String): String {
        var cleaned = response

        // Rimuovi marker di turno Gemma
        cleaned = cleaned
            .replace("<start_of_turn>model", "")
            .replace("<end_of_turn>", "")
            .replace("<start_of_turn>user", "")
            .replace("<bos>", "")
            .replace("<eos>", "")
            .trim()

        // Se la risposta contiene parti del prompt, e corrotta
        val problematicPatterns = listOf(
            "Sei un assistente fitness",
            "Sei un personal trainer",
            "Rispondi in italiano",
            "massimo 2 frasi",
            "Domanda:",
            "<start_of_turn>",
            "<end_of_turn>",
            "Riscrivi in modo amichevole",  // Nuovo prompt di riformulazione
            "modo amichevole:"
        )

        for (pattern in problematicPatterns) {
            if (cleaned.contains(pattern, ignoreCase = true)) {
                Log.w(TAG, "Response contains prompt fragment: $pattern")
                return "" // Forza fallback
            }
        }

        // Rimuovi eventuale ripetizione del marker model
        val modelMarker = cleaned.lastIndexOf("model")
        if (modelMarker >= 0 && modelMarker < 50) {
            cleaned = cleaned.substring(modelMarker + 5).trim()
        }

        // Rimuovi asterischi e markdown eccessivo
        cleaned = cleaned
            .replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")
            .replace(Regex("^\\d+\\.\\s*\\*\\*"), "")
            .trim()

        return cleaned
    }
}
