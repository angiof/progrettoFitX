package com.app.fityo.dominio

/**
 * Risultato dell'analisi che correla i cambiamenti visivi con la scheda di allenamento.
 */
data class WorkoutAnalysis(
    val isWorkoutEffective: Boolean,
    val overallMessage: String,
    val encouragementMessage: String,
    val muscleGroupCorrelations: List<MuscleGroupCorrelation>,
    val suggestions: List<WorkoutSuggestion>,
    val workoutStats: WorkoutStats
)

/**
 * Correlazione tra un gruppo muscolare, la percentuale di allenamento e il cambiamento visivo.
 */
data class MuscleGroupCorrelation(
    val muscleGroup: String,
    val trainingPercent: Float,
    val visualChangePercent: Float,
    val correlation: CorrelationType
)

/**
 * Tipo di correlazione tra allenamento e risultati visivi.
 */
enum class CorrelationType {
    POSITIVE_STRONG,    // Allenamento alto + miglioramento visibile
    POSITIVE_WEAK,      // Allenamento medio + leggero miglioramento
    NEUTRAL,            // Nessun cambiamento significativo
    NEEDS_ATTENTION,    // Allenamento basso + nessun miglioramento
    UNEXPECTED_DECREASE // Regressione nonostante allenamento
}

/**
 * Suggerimento per migliorare l'allenamento.
 */
data class WorkoutSuggestion(
    val muscleGroup: String,
    val suggestion: String,
    val priority: SuggestionPriority
)

enum class SuggestionPriority {
    HIGH,
    MEDIUM,
    LOW
}

/**
 * Statistiche generali sull'allenamento.
 */
data class WorkoutStats(
    val totalWorkouts: Int,
    val workoutsPerWeek: Double,
    val mostTrainedGroup: String?,
    val leastTrainedGroup: String?,
    val daysSinceLastWorkout: Int?
)
