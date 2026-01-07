package com.app.fityo.analytics

/**
 * Risultati dell'analisi tensor degli allenamenti.
 */
data class WorkoutInsights(
    val performanceScore: Float,
    val weeklyProgress: Float,
    val monthlyProgress: Float,
    val optimalLoads: Map<String, OptimalLoad>,
    val plateauDetected: Boolean,
    val plateauMuscleGroups: List<String>,
    val overtrainingRisk: OvertrainingLevel,
    val fatigueScore: Float,
    val recoveryStatus: RecoveryStatus,
    val optimalRestDays: Int,
    val nextOptimalWorkoutDate: String,
    val muscleBalance: MuscleBalance,
    val suggestions: List<WorkoutSuggestion>,
    val digitalTwin: DigitalTwinPrediction?
)

data class OptimalLoad(
    val exerciseName: String,
    val currentWeight: Float,
    val suggestedWeight: Float,
    val suggestedReps: Int,
    val suggestedSets: Int,
    val progressionPercent: Float,
    val confidence: Float
)

enum class OvertrainingLevel {
    LOW, MODERATE, HIGH, CRITICAL
}

data class RecoveryStatus(
    val level: RecoveryLevel,
    val percentage: Float,
    val muscleGroupRecovery: Map<String, Float>,
    val estimatedFullRecoveryHours: Int
)

enum class RecoveryLevel {
    FULLY_RECOVERED, MOSTLY_RECOVERED, PARTIAL_RECOVERY, NEEDS_REST
}

data class MuscleBalance(
    val scores: Map<String, Float>,
    val imbalances: List<MuscleImbalance>,
    val overtrainedGroups: List<String>,
    val undertrainedGroups: List<String>,
    val balanceScore: Float
)

data class MuscleImbalance(
    val muscleGroup: String,
    val currentScore: Float,
    val idealScore: Float,
    val deviation: Float,
    val recommendation: String
)

data class WorkoutSuggestion(
    val type: SuggestionType,
    val priority: SuggestionPriority,
    val title: String,
    val description: String,
    val actionText: String?,
    val relatedMuscleGroup: String?
)

enum class SuggestionType {
    INCREASE_LOAD, DECREASE_LOAD, ADD_EXERCISE, REST_DAY,
    CHANGE_INTENSITY, BALANCE_TRAINING, PLATEAU_BREAK, RECOVERY_FOCUS
}

enum class SuggestionPriority {
    LOW, MEDIUM, HIGH, CRITICAL
}

data class DigitalTwinPrediction(
    val currentStrengthIndex: Float,
    val projectedStrength1Month: Float,
    val projectedStrength3Months: Float,
    val projectedStrength6Months: Float,
    val currentPotentialUsage: Float,
    val estimatedMaxPotential: Float,
    val timeToMaxPotential: String,
    val vsOptimalSelf: Float,
    val vsLastMonth: Float,
    val vs3MonthsAgo: Float,
    val scenarios: List<TrainingScenario>,
    val injuryRiskScore: Float,
    val burnoutRiskWeeks: Int?
)

data class TrainingScenario(
    val name: String,
    val description: String,
    val projectedGain: Float,
    val timeframe: String,
    val riskLevel: OvertrainingLevel,
    val recommended: Boolean
)

data class ExerciseTensor(
    val name: String,
    val weights: List<Float>,
    val reps: List<Int>,
    val sets: List<Int>,
    val volumes: List<Float>
) {
    fun weightTrend(): Float = seriesTrend(cleanedWeights())

    fun oneRepMaxTrend(): Float = seriesTrend(oneRepMaxSeries())

    fun volumeTrend(): Float = seriesTrend(cleanedSeries(volumes))

    fun hasProgressionData(): Boolean {
        return oneRepMaxSeries().size >= 3 || cleanedWeights().size >= 3 || volumes.size >= 3
    }

    fun dataPoints(): Int {
        val weightPoints = cleanedWeights().size
        return maxOf(weightPoints, volumes.size)
    }

    fun isInPlateau(threshold: Float = 2f): Boolean {
        val series = when {
            oneRepMaxSeries().size >= 4 -> oneRepMaxSeries()
            cleanedWeights().size >= 4 -> cleanedWeights()
            volumes.size >= 4 -> volumes
            else -> return false
        }

        val recent = series.takeLast(4)
        val avg = recent.average()
        if (avg <= 0.0) return false

        val deviation = recent.map { kotlin.math.abs(it - avg) }.average()
        val deviationPercent = (deviation / avg * 100)
        val trend = simpleTrend(recent)

        return deviationPercent < threshold && kotlin.math.abs(trend) < threshold
    }

    private fun cleanedWeights(): List<Float> = weights.filter { it > 0f }

    private fun cleanedSeries(series: List<Float>): List<Float> = series.filter { it > 0f }

    private fun oneRepMaxSeries(): List<Float> {
        if (weights.isEmpty() || reps.isEmpty()) return emptyList()
        return weights.zip(reps) { weight, rep ->
            if (weight > 0f) weight * (1f + rep / 30f) else 0f
        }.filter { it > 0f }
    }

    private fun seriesTrend(series: List<Float>): Float {
        if (series.size < 2) return 0f
        val recentCount = if (series.size < 3) series.size else 3
        if (series.size <= recentCount) {
            return simpleTrend(series)
        }
        val recent = series.takeLast(recentCount).average()
        val older = series.dropLast(recentCount).takeLast(recentCount).average()
        return if (older > 0) ((recent - older) / older * 100).toFloat() else 0f
    }

    private fun simpleTrend(series: List<Float>): Float {
        if (series.size < 2) return 0f
        val first = series.first()
        val last = series.last()
        return if (first > 0f) ((last - first) / first * 100).toFloat() else 0f
    }
}

data class SessionStats(
    val totalWorkouts: Int,
    val totalVolume: Float,
    val avgIntensity: Float,
    val weeklyFrequency: Float,
    val consistencyScore: Float,
    val streakDays: Int,
    val strongestMuscle: String?,
    val weakestMuscle: String?
)
