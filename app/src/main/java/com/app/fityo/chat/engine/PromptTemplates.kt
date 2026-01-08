package com.app.fityo.chat.engine

import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity

/**
 * Context data classes per i vari tipi di prompt.
 */

data class HistoryContext(
    val recentWorkouts: List<SchedeEntity>,
    val lastWorkout: SchedeEntity?,
    val averageTime: String?,
    val daysSinceLastWorkout: Int,
    val targetMuscle: String? = null,
    val targetDay: String? = null
)

data class PerformanceContext(
    val exerciseName: String,
    val progression: List<ExerciseProgress>,
    val maxWeight: Float?,
    val averageWeight: Float?,
    val trend: String,
    val lastDetails: EsserciziEntity?
)

data class PlanningContext(
    val recentDays: Int,
    val recentExercises: List<String>,
    val recentMuscleGroups: List<String>,
    val musclesToAvoid: List<String>,
    val availableExercises: List<String>,
    val requestedCount: Int?
)

data class GeneralContext(
    val weeklyCount: Int,
    val monthlyCount: Int,
    val mostTrainedMuscle: String?,
    val daysSinceLastWorkout: Int,
    val weeklyAverage: Double
)

data class ComparisonContext(
    val currentWeekVolume: Int,
    val previousWeekVolume: Int,
    val volumeChange: Int,
    val currentMonthWorkouts: Int,
    val previousMonthWorkouts: Int,
    val frequencyChange: Int
)

data class EquipmentContext(
    val targetMuscle: String?,
    val equipmentForMuscle: List<String>,
    val mostUsedEquipment: List<Pair<String, Int>>,
    val lastExerciseEquipment: String?
)

/**
 * Template factory per Gemma 2.
 * Formato: <start_of_turn>user\n[content]<end_of_turn>\n<start_of_turn>model
 */
object PromptTemplates {

    /**
     * Template HISTORY - Domande su date, orari, frequenza.
     */
    fun buildHistoryPrompt(context: HistoryContext, query: String): String {
        val workoutsList = if (context.recentWorkouts.isNotEmpty()) {
            context.recentWorkouts.take(8).joinToString("\n") { scheda ->
                "- ${scheda.data}: ${scheda.gruppoMuscolare}${scheda.ora?.let { " alle $it" } ?: ""}"
            }
        } else {
            "Nessun allenamento registrato"
        }

        return """
<start_of_turn>user
Sei un assistente fitness. Rispondi in italiano, massimo 2 frasi.

Allenamenti recenti:
$workoutsList

Ultimo: ${context.lastWorkout?.let { "${it.data} (${it.gruppoMuscolare})" } ?: "nessuno"}
Giorni da ultimo allenamento: ${if (context.daysSinceLastWorkout >= 0) context.daysSinceLastWorkout else "N/D"}
${context.targetMuscle?.let { "Muscolo cercato: $it" } ?: ""}

Domanda: $query<end_of_turn>
<start_of_turn>model
""".trimIndent()
    }

    /**
     * Template PERFORMANCE - Domande su pesi, progressi, record.
     */
    fun buildPerformancePrompt(context: PerformanceContext, query: String): String {
        val progressionList = if (context.progression.isNotEmpty()) {
            context.progression.take(5).joinToString("\n") { prog ->
                "- ${prog.date}: ${prog.weight?.let { "${it}kg" } ?: "N/D"} x ${prog.sets}x${prog.reps}"
            }
        } else {
            "Nessun dato"
        }

        return """
<start_of_turn>user
Sei un assistente fitness. Rispondi in italiano con numeri precisi, massimo 2 frasi.

Esercizio: ${context.exerciseName}
Storico:
$progressionList

Record: ${context.maxWeight?.let { "${it}kg" } ?: "N/D"}
Ultimo: ${context.lastDetails?.let { "${it.nSerie}x${it.nRipetizione} ${it.peso?.let { p -> "con ${p}kg" } ?: ""}" } ?: "N/D"}

Domanda: $query<end_of_turn>
<start_of_turn>model
""".trimIndent()
    }

    /**
     * Template PLANNING - Suggerimenti esercizi.
     */
    fun buildPlanningPrompt(context: PlanningContext, query: String): String {
        val recentExList = if (context.recentExercises.isNotEmpty()) {
            context.recentExercises.take(10).joinToString(", ")
        } else {
            "nessuno"
        }

        val availableList = if (context.availableExercises.isNotEmpty()) {
            context.availableExercises.take(15).joinToString(", ")
        } else {
            "panca, squat, stacco, curl, lat machine, croci, dip"
        }

        val count = context.requestedCount ?: 4

        return """
<start_of_turn>user
Sei un personal trainer. Suggerisci $count esercizi in italiano con serie e ripetizioni.

Esercizi fatti di recente (da evitare): $recentExList
Esercizi disponibili: $availableList

Richiesta: $query<end_of_turn>
<start_of_turn>model
""".trimIndent()
    }

    /**
     * Template GENERAL - Statistiche generali.
     */
    fun buildGeneralPrompt(context: GeneralContext, query: String): String {
        return """
<start_of_turn>user
Sei un assistente fitness. Rispondi in italiano, massimo 2 frasi.

Statistiche:
- Allenamenti settimana: ${context.weeklyCount}
- Allenamenti mese: ${context.monthlyCount}
- Muscolo preferito: ${context.mostTrainedMuscle ?: "N/D"}
- Giorni da ultimo allenamento: ${if (context.daysSinceLastWorkout >= 0) context.daysSinceLastWorkout else "N/D"}

Domanda: $query<end_of_turn>
<start_of_turn>model
""".trimIndent()
    }

    /**
     * Template COMPARISON - Confronti tra periodi.
     */
    fun buildComparisonPrompt(context: ComparisonContext, query: String): String {
        return """
<start_of_turn>user
Sei un assistente fitness. Confronta i dati e rispondi in italiano, massimo 2 frasi.

Volume settimanale:
- Questa settimana: ${context.currentWeekVolume}
- Settimana scorsa: ${context.previousWeekVolume}
- Variazione: ${if (context.volumeChange >= 0) "+" else ""}${context.volumeChange}%

Frequenza mensile:
- Questo mese: ${context.currentMonthWorkouts} allenamenti
- Mese scorso: ${context.previousMonthWorkouts} allenamenti

Domanda: $query<end_of_turn>
<start_of_turn>model
""".trimIndent()
    }

    /**
     * Template EQUIPMENT - Domande su attrezzi.
     */
    fun buildEquipmentPrompt(context: EquipmentContext, query: String): String {
        val topEquipment = if (context.mostUsedEquipment.isNotEmpty()) {
            context.mostUsedEquipment.take(5).joinToString(", ") { "${it.first} (${it.second}x)" }
        } else {
            "nessun dato"
        }

        return """
<start_of_turn>user
Sei un assistente fitness. Rispondi in italiano, massimo 2 frasi.

${context.targetMuscle?.let { "Attrezzi usati per $it: ${context.equipmentForMuscle.joinToString(", ").ifEmpty { "nessun dato" }}" } ?: ""}
Attrezzi piu usati: $topEquipment

Domanda: $query<end_of_turn>
<start_of_turn>model
""".trimIndent()
    }

    /**
     * Fallback template.
     */
    fun buildFallbackPrompt(query: String, generalContext: GeneralContext): String {
        return """
<start_of_turn>user
Sei un assistente fitness. Rispondi in italiano, massimo 2 frasi.

Info: ${generalContext.weeklyCount} allenamenti questa settimana, ${generalContext.monthlyCount} questo mese.

Domanda: $query<end_of_turn>
<start_of_turn>model
""".trimIndent()
    }
}
