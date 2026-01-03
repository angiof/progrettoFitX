package com.app.fityo.mediapipe

import com.app.fityo.dominio.CompareResult
import com.app.fityo.dominio.CorrelationType
import com.app.fityo.dominio.GruppoMuscolarePercentuale
import com.app.fityo.dominio.MuscleGroupCorrelation
import com.app.fityo.dominio.SuggestionPriority
import com.app.fityo.dominio.WorkoutAnalysis
import com.app.fityo.dominio.WorkoutStats
import com.app.fityo.dominio.WorkoutSuggestion
import kotlin.math.abs

/**
 * Analizzatore che correla i cambiamenti muscolari visivi con i dati delle schede di allenamento.
 * Genera feedback incoraggianti e suggerimenti personalizzati.
 */
class WorkoutAnalyzer {

    // Mapping tra gruppi muscolari della scheda e distretti del confronto visivo
    private val muscleGroupMapping = mapOf(
        "Braccia" to listOf("braccia", "bicipiti", "tricipiti", "avambracci"),
        "Petto" to listOf("petto", "pettorale", "chest"),
        "Addominali" to listOf("addominali", "core", "addome"),
        "Gambe" to listOf("gambe", "quadricipiti", "femorali", "polpacci", "cosce"),
        "Glutei" to listOf("glutei", "glutes"),
        "Schiena" to listOf("schiena", "dorsali", "back", "dorso"),
        "Spalle" to listOf("spalle", "deltoidi", "shoulders")
    )

    /**
     * Analizza i risultati del confronto muscolare in relazione alla scheda di allenamento.
     */
    fun analyze(
        compareResult: CompareResult,
        workoutDistribution: List<GruppoMuscolarePercentuale>,
        workoutStats: WorkoutStats
    ): WorkoutAnalysis {
        val correlations = calculateCorrelations(compareResult, workoutDistribution)
        val isEffective = evaluateEffectiveness(correlations, compareResult)
        val suggestions = generateSuggestions(correlations, workoutDistribution, compareResult)

        return WorkoutAnalysis(
            isWorkoutEffective = isEffective,
            overallMessage = generateOverallMessage(isEffective, correlations, workoutStats),
            encouragementMessage = generateEncouragementMessage(compareResult, correlations, workoutStats),
            muscleGroupCorrelations = correlations,
            suggestions = suggestions,
            workoutStats = workoutStats
        )
    }

    private fun calculateCorrelations(
        compareResult: CompareResult,
        workoutDistribution: List<GruppoMuscolarePercentuale>
    ): List<MuscleGroupCorrelation> {
        val correlations = mutableListOf<MuscleGroupCorrelation>()

        // Calcola correlazione per Braccia
        val armsTraining = getTrainingPercentForDistrict("Braccia", workoutDistribution)
        correlations.add(
            MuscleGroupCorrelation(
                muscleGroup = "Braccia",
                trainingPercent = armsTraining,
                visualChangePercent = compareResult.armsResult.variationPercent,
                correlation = determineCorrelationType(armsTraining, compareResult.armsResult.variationPercent)
            )
        )

        // Calcola correlazione per Addominali
        val absTraining = getTrainingPercentForDistrict("Addominali", workoutDistribution)
        correlations.add(
            MuscleGroupCorrelation(
                muscleGroup = "Addominali",
                trainingPercent = absTraining,
                visualChangePercent = compareResult.absResult.variationPercent,
                correlation = determineCorrelationType(absTraining, compareResult.absResult.variationPercent)
            )
        )

        // Calcola correlazione per Gambe
        val legsTraining = getTrainingPercentForDistrict("Gambe", workoutDistribution)
        correlations.add(
            MuscleGroupCorrelation(
                muscleGroup = "Gambe",
                trainingPercent = legsTraining,
                visualChangePercent = compareResult.legsResult.variationPercent,
                correlation = determineCorrelationType(legsTraining, compareResult.legsResult.variationPercent)
            )
        )

        // Calcola correlazione per Glutei
        val glutesTraining = getTrainingPercentForDistrict("Glutei", workoutDistribution)
        correlations.add(
            MuscleGroupCorrelation(
                muscleGroup = "Glutei",
                trainingPercent = glutesTraining,
                visualChangePercent = compareResult.glutesResult.variationPercent,
                correlation = determineCorrelationType(glutesTraining, compareResult.glutesResult.variationPercent)
            )
        )

        return correlations
    }

    private fun getTrainingPercentForDistrict(
        district: String,
        workoutDistribution: List<GruppoMuscolarePercentuale>
    ): Float {
        val relatedGroups = muscleGroupMapping[district] ?: listOf(district.lowercase())

        return workoutDistribution
            .filter { gruppo ->
                relatedGroups.any { it.equals(gruppo.gruppoMuscolare, ignoreCase = true) }
            }
            .sumOf { it.percentuale.toDouble() }
            .toFloat()
    }

    private fun determineCorrelationType(trainingPercent: Float, visualChange: Float): CorrelationType {
        return when {
            // Allenamento alto (>20%) con miglioramento notevole (>3%)
            trainingPercent >= 20f && visualChange >= 3f -> CorrelationType.POSITIVE_STRONG

            // Allenamento medio (10-20%) con miglioramento (>1%)
            trainingPercent >= 10f && visualChange >= 1f -> CorrelationType.POSITIVE_WEAK

            // Nessun allenamento significativo (<10%) con nessun cambiamento
            trainingPercent < 10f && abs(visualChange) < 2f -> CorrelationType.NEEDS_ATTENTION

            // Allenamento presente ma diminuzione visiva
            trainingPercent >= 10f && visualChange < -2f -> CorrelationType.UNEXPECTED_DECREASE

            // Caso neutrale
            else -> CorrelationType.NEUTRAL
        }
    }

    private fun evaluateEffectiveness(
        correlations: List<MuscleGroupCorrelation>,
        compareResult: CompareResult
    ): Boolean {
        val positiveCorrelations = correlations.count {
            it.correlation == CorrelationType.POSITIVE_STRONG ||
            it.correlation == CorrelationType.POSITIVE_WEAK
        }
        val hasNotableResults = compareResult.notableCount > 0
        val averagePositive = compareResult.allResults.any { it.variationPercent > 0 }

        return positiveCorrelations >= 2 || hasNotableResults || averagePositive
    }

    private fun generateOverallMessage(
        isEffective: Boolean,
        correlations: List<MuscleGroupCorrelation>,
        stats: WorkoutStats
    ): String {
        if (stats.totalWorkouts == 0) {
            return "Non hai ancora schede di allenamento registrate. Inizia ad allenarti per vedere i progressi!"
        }

        val strongCorrelations = correlations.count { it.correlation == CorrelationType.POSITIVE_STRONG }
        val needsAttention = correlations.count { it.correlation == CorrelationType.NEEDS_ATTENTION }

        return when {
            strongCorrelations >= 2 -> "Ottimi progressi! La tua scheda sta funzionando perfettamente! 💪"
            isEffective -> "Stai facendo progressi! Continua così, i risultati si vedono! 📈"
            needsAttention >= 2 -> "Alcuni gruppi muscolari necessitano più attenzione. Vedi i suggerimenti sotto."
            else -> "I risultati sono in arrivo! La costanza è la chiave del successo. 🎯"
        }
    }

    private fun generateEncouragementMessage(
        compareResult: CompareResult,
        correlations: List<MuscleGroupCorrelation>,
        stats: WorkoutStats
    ): String {
        val messages = mutableListOf<String>()

        // Messaggio basato sui miglioramenti visivi
        val bestImprovement = compareResult.allResults.maxByOrNull { it.variationPercent }
        if (bestImprovement != null && bestImprovement.variationPercent > 2f) {
            val districtName = when (bestImprovement.district.name) {
                "ARMS" -> "braccia"
                "ABS" -> "addominali"
                "LEGS" -> "gambe"
                "GLUTES" -> "glutei"
                else -> bestImprovement.district.name.lowercase()
            }
            messages.add("I tuoi $districtName mostrano un miglioramento del ${String.format("%.1f", bestImprovement.variationPercent)}%!")
        }

        // Messaggio basato sulla consistenza
        stats.workoutsPerWeek.let { perWeek ->
            when {
                perWeek >= 4 -> messages.add("Wow! ${String.format("%.1f", perWeek)} allenamenti a settimana! Sei una macchina! 🔥")
                perWeek >= 3 -> messages.add("${String.format("%.1f", perWeek)} sessioni settimanali - ottima costanza!")
                perWeek >= 2 -> messages.add("Stai costruendo una buona routine!")
                perWeek > 0 -> messages.add("Ogni allenamento conta! Prova ad aumentare la frequenza.")
            }
        }

        // Messaggio basato sulle correlazioni positive
        val positiveCount = correlations.count {
            it.correlation == CorrelationType.POSITIVE_STRONG ||
            it.correlation == CorrelationType.POSITIVE_WEAK
        }
        if (positiveCount > 0) {
            messages.add("$positiveCount gruppi muscolari stanno rispondendo bene al tuo allenamento!")
        }

        // Messaggio motivazionale di default
        if (messages.isEmpty()) {
            messages.add("Continua ad allenarti con costanza, i risultati arriveranno! 💪")
        }

        return messages.joinToString(" ")
    }

    private fun generateSuggestions(
        correlations: List<MuscleGroupCorrelation>,
        workoutDistribution: List<GruppoMuscolarePercentuale>,
        compareResult: CompareResult
    ): List<WorkoutSuggestion> {
        val suggestions = mutableListOf<WorkoutSuggestion>()

        // Suggerimenti per gruppi che necessitano attenzione
        correlations
            .filter { it.correlation == CorrelationType.NEEDS_ATTENTION }
            .forEach { correlation ->
                suggestions.add(
                    WorkoutSuggestion(
                        muscleGroup = correlation.muscleGroup,
                        suggestion = "Aggiungi più esercizi per ${correlation.muscleGroup.lowercase()} nella tua scheda (attualmente solo ${String.format("%.0f", correlation.trainingPercent)}%)",
                        priority = SuggestionPriority.HIGH
                    )
                )
            }

        // Suggerimenti per regressioni inaspettate
        correlations
            .filter { it.correlation == CorrelationType.UNEXPECTED_DECREASE }
            .forEach { correlation ->
                suggestions.add(
                    WorkoutSuggestion(
                        muscleGroup = correlation.muscleGroup,
                        suggestion = "I ${correlation.muscleGroup.lowercase()} mostrano una leggera diminuzione. Considera di aumentare l'intensità o variare gli esercizi.",
                        priority = SuggestionPriority.MEDIUM
                    )
                )
            }

        // Suggerimenti per bilanciamento
        val trainedGroups = workoutDistribution.map { it.gruppoMuscolare.lowercase() }
        val untrained = listOf("braccia", "gambe", "addominali", "glutei", "petto", "schiena")
            .filter { group ->
                trainedGroups.none { it.contains(group, ignoreCase = true) }
            }

        if (untrained.isNotEmpty()) {
            suggestions.add(
                WorkoutSuggestion(
                    muscleGroup = "Generale",
                    suggestion = "Considera di aggiungere esercizi per: ${untrained.joinToString(", ")}",
                    priority = SuggestionPriority.LOW
                )
            )
        }

        return suggestions.sortedBy { it.priority.ordinal }
    }
}
