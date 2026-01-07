package com.app.fityo.analytics

import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.data_layer.db.UserProfileEntity
import com.app.fityo.dominio.GruppoMuscolarePercentuale
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Analizzatore tensor-based per ottimizzazione allenamenti.
 */
class TensorWorkoutAnalyzer(
    private val modelStateStore: WorkoutModelStateStore? = null
) : WorkoutAnalyticsEngine {

    companion object {
        private val RECOVERY_HOURS = mapOf(
            "Petto" to 48, "Schiena" to 48, "Spalle" to 48,
            "Bicipiti" to 36, "Tricipiti" to 36, "Gambe" to 72,
            "Addominali" to 24, "Glutei" to 48, "Polpacci" to 36
        )
    }

    private data class FeatureScore(val value: Float, val reliability: Float)

    private data class PerformanceMetrics(
        val score: Float,
        val recentVolume: Float,
        val recentIntensity: Float,
        val avgProgressTrend: Float
    )

    override fun analyze(
        schede: List<SchedeEntity>,
        esercizi: List<EsserciziEntity>,
        profile: UserProfileEntity?,
        muscleDistribution: List<GruppoMuscolarePercentuale>
    ): WorkoutInsights {
        val orderedSchede = sortSchedeByDate(schede)
        val exerciseTensors = buildExerciseTensors(esercizi)
        val (overtrainingRisk, fatigueScore) = assessOvertraining(orderedSchede)
        val recoveryStatus = calculateRecoveryStatus(orderedSchede)
        val modelState = modelStateStore?.load()
        val performance = calculatePerformanceScore(orderedSchede, esercizi, exerciseTensors, modelState)
        val (weeklyProgress, monthlyProgress) = calculateProgress(orderedSchede, esercizi)
        val optimalLoads = calculateOptimalLoads(exerciseTensors, fatigueScore, recoveryStatus)
        val (plateauDetected, plateauGroups) = detectPlateaus(exerciseTensors)
        val muscleBalance = analyzeMuscleBalance(muscleDistribution)
        val suggestions = generateSuggestions(plateauDetected, overtrainingRisk, recoveryStatus, muscleBalance)
        val digitalTwin = profile?.let {
            generateDigitalTwin(orderedSchede, esercizi, it, performance.score, monthlyProgress, exerciseTensors)
        }
        val optimalRestDays = calculateOptimalRestDays(fatigueScore, recoveryStatus)

        modelStateStore?.save(
            updateModelState(
                previous = modelState,
                recentVolume = performance.recentVolume,
                recentIntensity = performance.recentIntensity,
                avgProgressTrend = performance.avgProgressTrend,
                now = LocalDate.now()
            )
        )

        return WorkoutInsights(
            performanceScore = performance.score,
            weeklyProgress = weeklyProgress,
            monthlyProgress = monthlyProgress,
            optimalLoads = optimalLoads,
            plateauDetected = plateauDetected,
            plateauMuscleGroups = plateauGroups,
            overtrainingRisk = overtrainingRisk,
            fatigueScore = fatigueScore,
            recoveryStatus = recoveryStatus,
            optimalRestDays = optimalRestDays,
            nextOptimalWorkoutDate = LocalDate.now().plusDays(optimalRestDays.toLong())
                .format(DateTimeFormatter.ofPattern("dd MMM")),
            muscleBalance = muscleBalance,
            suggestions = suggestions,
            digitalTwin = digitalTwin
        )
    }

    private fun buildExerciseTensors(
        esercizi: List<EsserciziEntity>
    ): Map<String, ExerciseTensor> {
        val exerciseMap = mutableMapOf<String, MutableList<EsserciziEntity>>()
        esercizi.forEach { e ->
            exerciseMap.getOrPut(e.nome.lowercase().trim()) { mutableListOf() }.add(e)
        }

        return exerciseMap.mapValues { (name, list) ->
            val weightEntries = list.filter { it.peso != null }
            ExerciseTensor(
                name = name,
                weights = weightEntries.map { it.peso ?: 0f },
                reps = weightEntries.map { it.nRipetizione },
                sets = weightEntries.map { it.nSerie },
                volumes = list.map { it.nSerie * it.nRipetizione * (it.peso ?: 1f) }
            )
        }
    }

    private fun calculatePerformanceScore(
        schede: List<SchedeEntity>,
        esercizi: List<EsserciziEntity>,
        tensors: Map<String, ExerciseTensor>,
        modelState: WorkoutModelState?
    ): PerformanceMetrics {
        if (schede.isEmpty()) {
            return PerformanceMetrics(0f, 0f, 0f, 0f)
        }

        val volumes = calculateSessionVolumes(schede, esercizi)
        val intensities = schede.map { intensityValue(it.intesita) }
        val recentCount = min(5, volumes.size)

        val recentVolume = if (recentCount > 0) volumes.takeLast(recentCount).average().toFloat() else 0f
        val recentIntensity = if (recentCount > 0) intensities.takeLast(recentCount).average().toFloat() else 0f

        val volumeScore = blendRangeAndTrend(
            recent = recentVolume,
            series = volumes,
            storedMin = modelState?.minVolume,
            storedMax = modelState?.maxVolume,
            storedAvg = modelState?.emaVolume
        )
        val intensityScore = blendRangeAndTrend(
            recent = recentIntensity,
            series = intensities,
            storedMin = modelState?.minIntensity,
            storedMax = modelState?.maxIntensity,
            storedAvg = modelState?.emaIntensity
        )

        val completionRate = schede.count { it.completed }.toFloat() / schede.size
        val consistencyScore = calculateConsistencyScore(schede)

        val progressionTrends = tensors.values.mapNotNull { tensor ->
            if (!tensor.hasProgressionData()) return@mapNotNull null
            if (tensor.weights.isNotEmpty()) tensor.oneRepMaxTrend() else tensor.volumeTrend()
        }
        val avgProgressTrend = if (progressionTrends.isNotEmpty()) progressionTrends.average().toFloat() else 0f
        val smoothedProgress = if (modelState != null && modelState.emaProgress != 0f) {
            avgProgressTrend * 0.6f + modelState.emaProgress * 0.4f
        } else {
            avgProgressTrend
        }
        val progressionScore = trendToScore(smoothedProgress, 15f)

        val features = listOf(
            FeatureScore(volumeScore, reliability(volumes.size, 12)),
            FeatureScore(intensityScore, reliability(intensities.size, 12)),
            FeatureScore(completionRate, reliability(schede.size, 10)),
            FeatureScore(consistencyScore, reliability(schede.size, 8)),
            FeatureScore(
                progressionScore,
                if (progressionTrends.isNotEmpty()) reliability(progressionTrends.size, 6) else 0f
            )
        )

        val blendedScore = combineFeatureScores(features)
        return PerformanceMetrics(
            score = (blendedScore * 100).coerceIn(0f, 100f),
            recentVolume = recentVolume,
            recentIntensity = recentIntensity,
            avgProgressTrend = avgProgressTrend
        )
    }

    private fun calculateProgress(schede: List<SchedeEntity>, esercizi: List<EsserciziEntity>): Pair<Float, Float> {
        val volumes = calculateSessionVolumes(schede, esercizi)

        if (volumes.size < 2) return 0f to 0f

        val recentWeek = volumes.takeLast(min(7, volumes.size)).average()
        val previousWeek = if (volumes.size > 7) {
            volumes.dropLast(7).takeLast(min(7, volumes.size - 7)).average()
        } else volumes.first().toDouble()

        val weeklyProgress = if (previousWeek > 0) {
            ((recentWeek - previousWeek) / previousWeek * 100).toFloat()
        } else 0f

        val monthlyProgress = if (volumes.size > 30) {
            val recent = volumes.takeLast(30).average()
            val previous = volumes.dropLast(30).takeLast(30).average()
            if (previous > 0) ((recent - previous) / previous * 100).toFloat() else 0f
        } else weeklyProgress

        return weeklyProgress.coerceIn(-100f, 100f) to monthlyProgress.coerceIn(-100f, 100f)
    }

    private fun calculateOptimalLoads(
        tensors: Map<String, ExerciseTensor>,
        fatigueScore: Float,
        recovery: RecoveryStatus
    ): Map<String, OptimalLoad> {
        val readiness = ((1f - fatigueScore) * (recovery.percentage / 100f)).coerceIn(0f, 1f)
        val baseProgression = when {
            readiness > 0.75f -> 0.03f
            readiness > 0.6f -> 0.02f
            readiness > 0.45f -> 0.01f
            else -> 0f
        }

        return tensors.mapValues { (_, tensor) ->
            val currentWeight = tensor.weights.lastOrNull() ?: 0f
            val trend = tensor.oneRepMaxTrend()
            val plateauBoost = if (tensor.isInPlateau()) 0.015f else 0f
            val trendBoost = (trend / 100f * 0.5f).coerceIn(-0.02f, 0.03f)

            val progression = (baseProgression + plateauBoost + trendBoost).coerceIn(-0.05f, 0.06f)
            val suggestedWeight = if (currentWeight > 0f) currentWeight * (1 + progression) else 0f

            OptimalLoad(
                exerciseName = tensor.name,
                currentWeight = currentWeight,
                suggestedWeight = suggestedWeight,
                suggestedReps = tensor.reps.lastOrNull()?.coerceIn(6, 12) ?: 10,
                suggestedSets = tensor.sets.lastOrNull()?.coerceIn(3, 5) ?: 3,
                progressionPercent = progression * 100,
                confidence = (tensor.dataPoints() / 8f).coerceIn(0.1f, 1f)
            )
        }
    }

    private fun detectPlateaus(tensors: Map<String, ExerciseTensor>): Pair<Boolean, List<String>> {
        val plateauExercises = tensors.filter { it.value.isInPlateau() }.keys.toList()
        return plateauExercises.isNotEmpty() to plateauExercises
    }

    private fun assessOvertraining(schede: List<SchedeEntity>): Pair<OvertrainingLevel, Float> {
        if (schede.isEmpty()) return OvertrainingLevel.LOW to 0f

        val now = LocalDate.now()
        val recentSchede = schede.filter { parseLocalDate(it.data)?.isAfter(now.minusDays(13)) == true }
        val window = if (recentSchede.isNotEmpty()) recentSchede else schede.takeLast(14)

        val frequency = window.size / 14f
        val avgIntensity = window.map { intensityValue(it.intesita) }.average().toFloat()

        val fatigueScore = (frequency / 0.7f * 0.55f + avgIntensity * 0.45f).coerceIn(0f, 1f)

        val level = when {
            fatigueScore < 0.3f -> OvertrainingLevel.LOW
            fatigueScore < 0.5f -> OvertrainingLevel.MODERATE
            fatigueScore < 0.7f -> OvertrainingLevel.HIGH
            else -> OvertrainingLevel.CRITICAL
        }

        return level to fatigueScore
    }

    private fun calculateRecoveryStatus(schede: List<SchedeEntity>): RecoveryStatus {
        val now = LocalDate.now()
        val muscleRecovery = mutableMapOf<String, Float>()

        schede.groupBy { it.gruppoMuscolare }.forEach { (muscle, muscleSchede) ->
            val lastWorkout = muscleSchede.mapNotNull {
                parseLocalDate(it.data)
            }.maxOrNull()

            val recoveryPercent = if (lastWorkout != null) {
                val hours = ChronoUnit.HOURS.between(lastWorkout.atStartOfDay(), now.atStartOfDay())
                val neededHours = RECOVERY_HOURS[muscle] ?: 48
                (hours.toFloat() / neededHours * 100).coerceIn(0f, 100f)
            } else 100f

            muscleRecovery[muscle] = recoveryPercent
        }

        val avgRecovery = muscleRecovery.values.average().toFloat()
        val level = when {
            avgRecovery >= 100 -> RecoveryLevel.FULLY_RECOVERED
            avgRecovery >= 75 -> RecoveryLevel.MOSTLY_RECOVERED
            avgRecovery >= 50 -> RecoveryLevel.PARTIAL_RECOVERY
            else -> RecoveryLevel.NEEDS_REST
        }

        return RecoveryStatus(
            level = level,
            percentage = avgRecovery.coerceAtMost(100f),
            muscleGroupRecovery = muscleRecovery,
            estimatedFullRecoveryHours = if (avgRecovery < 100) ((100 - avgRecovery) / 100 * 48).toInt() else 0
        )
    }

    private fun analyzeMuscleBalance(distribution: List<GruppoMuscolarePercentuale>): MuscleBalance {
        val ideal = mapOf(
            "Petto" to 15f, "Schiena" to 20f, "Spalle" to 10f, "Bicipiti" to 8f,
            "Tricipiti" to 8f, "Gambe" to 25f, "Addominali" to 7f, "Glutei" to 7f
        )

        val scores = mutableMapOf<String, Float>()
        val imbalances = mutableListOf<MuscleImbalance>()
        val overtrained = mutableListOf<String>()
        val undertrained = mutableListOf<String>()

        distribution.forEach { item ->
            scores[item.gruppoMuscolare] = item.percentuale
            val idealVal = ideal[item.gruppoMuscolare] ?: 10f
            val deviation = (item.percentuale - idealVal) / idealVal * 100

            when {
                deviation > 30 -> {
                    overtrained.add(item.gruppoMuscolare)
                    imbalances.add(MuscleImbalance(item.gruppoMuscolare, item.percentuale, idealVal, deviation, "Riduci frequenza"))
                }
                deviation < -30 -> {
                    undertrained.add(item.gruppoMuscolare)
                    imbalances.add(MuscleImbalance(item.gruppoMuscolare, item.percentuale, idealVal, deviation, "Aumenta frequenza"))
                }
            }
        }

        val avgDeviation = distribution.map { abs(it.percentuale - (ideal[it.gruppoMuscolare] ?: 10f)) }.average()
        val balanceScore = (100 - avgDeviation * 2).coerceIn(0.0, 100.0).toFloat()

        return MuscleBalance(scores, imbalances, overtrained, undertrained, balanceScore)
    }

    private fun generateSuggestions(
        plateau: Boolean,
        overtraining: OvertrainingLevel,
        recovery: RecoveryStatus,
        balance: MuscleBalance
    ): List<WorkoutSuggestion> {
        val suggestions = mutableListOf<WorkoutSuggestion>()

        when (overtraining) {
            OvertrainingLevel.CRITICAL -> suggestions.add(WorkoutSuggestion(
                SuggestionType.REST_DAY, SuggestionPriority.CRITICAL,
                "Riposo Necessario", "Il tuo corpo ha bisogno di recuperare", "Riposa 2-3 giorni", null
            ))
            OvertrainingLevel.HIGH -> suggestions.add(WorkoutSuggestion(
                SuggestionType.DECREASE_LOAD, SuggestionPriority.HIGH,
                "Riduci Intensità", "Considera una settimana di scarico", "Riduci carichi del 20%", null
            ))
            else -> {}
        }

        if (plateau) {
            suggestions.add(WorkoutSuggestion(
                SuggestionType.PLATEAU_BREAK, SuggestionPriority.HIGH,
                "Plateau Rilevato", "Prova a variare esercizi o intensità", "Cambia routine", null
            ))
        }

        balance.undertrainedGroups.take(2).forEach { muscle ->
            suggestions.add(WorkoutSuggestion(
                SuggestionType.BALANCE_TRAINING, SuggestionPriority.MEDIUM,
                "Allena $muscle", "Gruppo muscolare sotto-allenato", "Aggiungi esercizi", muscle
            ))
        }

        return suggestions.sortedByDescending { it.priority.ordinal }
    }

    private fun generateDigitalTwin(
        schede: List<SchedeEntity>,
        esercizi: List<EsserciziEntity>,
        profile: UserProfileEntity,
        performance: Float,
        monthlyProgress: Float,
        tensors: Map<String, ExerciseTensor>
    ): DigitalTwinPrediction {
        val totalVolume = esercizi.sumOf { (it.nSerie * it.nRipetizione * (it.peso ?: 1f)).toDouble() }.toFloat()
        val avgIntensity = schede.map { intensityValue(it.intesita) }.average().toFloat()

        val strengthIndex = (totalVolume / 1000f) * avgIntensity
        val progressionTrends = tensors.values.mapNotNull { tensor ->
            if (!tensor.hasProgressionData()) return@mapNotNull null
            if (tensor.weights.isNotEmpty()) tensor.oneRepMaxTrend() else tensor.volumeTrend()
        }
        val avgProgressTrend = if (progressionTrends.isNotEmpty()) {
            progressionTrends.average().toFloat()
        } else {
            monthlyProgress
        }
        val rawGrowthRate = (avgProgressTrend / 100f).coerceIn(-0.02f, 0.06f)
        val growthRate = if (abs(rawGrowthRate) < 0.005f) 0.01f else rawGrowthRate

        val ageFactor = 1 - (profile.age - 25).coerceIn(0, 40) / 100f
        val maxPotential = strengthIndex * 2.5f * ageFactor
        val potentialUsage = (strengthIndex / maxPotential * 100).coerceIn(0f, 100f)
        val timeToPotential = if (growthRate > 0f) {
            "${((100 - potentialUsage) / (growthRate * 100)).toInt()} mesi"
        } else {
            "n/d"
        }

        return DigitalTwinPrediction(
            currentStrengthIndex = strengthIndex,
            projectedStrength1Month = strengthIndex * (1 + growthRate),
            projectedStrength3Months = strengthIndex * (1 + growthRate).pow(3),
            projectedStrength6Months = strengthIndex * (1 + growthRate).pow(6),
            currentPotentialUsage = potentialUsage,
            estimatedMaxPotential = maxPotential,
            timeToMaxPotential = timeToPotential,
            vsOptimalSelf = potentialUsage,
            vsLastMonth = growthRate * 100,
            vs3MonthsAgo = growthRate * 300,
            scenarios = listOf(
                TrainingScenario("Mantenimento", "Ritmo attuale", growthRate * 100, "3 mesi", OvertrainingLevel.LOW, true),
                TrainingScenario("Intensivo", "+50% frequenza", growthRate * 150, "3 mesi", OvertrainingLevel.MODERATE, false)
            ),
            injuryRiskScore = if (performance > 80) 20f else 40f,
            burnoutRiskWeeks = if (performance > 80) null else 8
        )
    }

    private fun sortSchedeByDate(schede: List<SchedeEntity>): List<SchedeEntity> {
        if (schede.isEmpty()) return schede
        return schede.mapIndexed { index, scheda -> index to scheda }
            .sortedWith(
                compareBy<Pair<Int, SchedeEntity>>(
                    { parseLocalDate(it.second.data) ?: LocalDate.MIN },
                    { it.first }
                )
            )
            .map { it.second }
    }

    private fun parseLocalDate(value: String): LocalDate? {
        if (value.isBlank()) return null
        val datePart = value.trim().let { if (it.length >= 10) it.substring(0, 10) else it }
        val formats = listOf(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        )
        for (formatter in formats) {
            try {
                return LocalDate.parse(datePart, formatter)
            } catch (_: Exception) {
            }
        }
        return null
    }

    private fun intensityValue(intensity: String): Float {
        return when (intensity.lowercase()) {
            "alta" -> 1f
            "media" -> 0.66f
            else -> 0.33f
        }
    }

    private fun calculateSessionVolumes(
        schede: List<SchedeEntity>,
        esercizi: List<EsserciziEntity>
    ): List<Float> {
        return schede.map { scheda ->
            esercizi.filter { it.schedaId == scheda.id }
                .sumOf { (it.nSerie * it.nRipetizione * (it.peso ?: 1f)).toDouble() }
                .toFloat()
        }
    }

    private fun calculateConsistencyScore(schede: List<SchedeEntity>): Float {
        val dates = schede.mapNotNull { parseLocalDate(it.data) }.sorted()
        if (dates.size < 3) return 0.5f

        val gaps = dates.zipWithNext { a, b ->
            ChronoUnit.DAYS.between(a, b).toFloat().coerceAtLeast(1f)
        }
        val mean = gaps.average().toFloat()
        val variance = gaps.map { gap ->
            val diff = gap - mean
            diff * diff
        }.average().toFloat()
        val std = sqrt(variance)
        val regularity = if (mean > 0f) (1f - (std / mean)).coerceIn(0f, 1f) else 0f

        val now = LocalDate.now()
        val recentCount = dates.count { ChronoUnit.DAYS.between(it, now) <= 14 }
        val recentFrequency = recentCount / 2f
        val spanDays = ChronoUnit.DAYS.between(dates.first(), dates.last()).toFloat().coerceAtLeast(7f)
        val overallFrequency = schede.size / (spanDays / 7f)
        val frequencyScore = if (overallFrequency > 0f) {
            (recentFrequency / overallFrequency).coerceIn(0f, 1.5f)
        } else {
            (recentFrequency / 3f).coerceIn(0f, 1f)
        }
        val frequencyNormalized = (frequencyScore / 1.2f).coerceIn(0f, 1f)

        return (regularity * 0.7f + frequencyNormalized * 0.3f).coerceIn(0f, 1f)
    }

    private fun reliability(samples: Int, full: Int): Float {
        if (samples <= 0 || full <= 0) return 0f
        return (samples.toFloat() / full).coerceIn(0.2f, 1f)
    }

    private fun combineFeatureScores(features: List<FeatureScore>): Float {
        val totalWeight = features.sumOf { it.reliability.toDouble() }.toFloat()
        if (totalWeight <= 0f) return 0f
        val weighted = features.sumOf { (it.value * it.reliability).toDouble() }.toFloat()
        return (weighted / totalWeight).coerceIn(0f, 1f)
    }

    private fun blendRangeAndTrend(
        recent: Float,
        series: List<Float>,
        storedMin: Float?,
        storedMax: Float?,
        storedAvg: Float?
    ): Float {
        if (series.isEmpty() && (storedMin == null || storedMax == null)) return 0f
        val seriesMin = if (series.isNotEmpty()) series.minOrNull() ?: recent else recent
        val seriesMax = if (series.isNotEmpty()) series.maxOrNull() ?: recent else recent
        val minVal = if (storedMin != null && storedMin > 0f) min(storedMin, seriesMin) else seriesMin
        val maxVal = if (storedMax != null && storedMax > 0f) max(storedMax, seriesMax) else seriesMax
        val baseline = if (storedAvg != null && storedAvg > 0f) {
            storedAvg
        } else {
            if (series.isNotEmpty()) series.average().toFloat() else recent
        }
        val rangeScore = normalizeInRange(recent, minVal, maxVal)
        val trendScore = trendToScore(percentChange(recent, baseline), 20f)
        return (rangeScore * 0.65f + trendScore * 0.35f).coerceIn(0f, 1f)
    }

    private fun normalizeInRange(value: Float, minVal: Float, maxVal: Float): Float {
        if (maxVal - minVal <= 0f) return 0.5f
        return ((value - minVal) / (maxVal - minVal)).coerceIn(0f, 1f)
    }

    private fun percentChange(recent: Float, baseline: Float): Float {
        if (baseline <= 0f) return 0f
        return ((recent - baseline) / baseline) * 100f
    }

    private fun trendToScore(trendPercent: Float, cap: Float): Float {
        val clamped = trendPercent.coerceIn(-cap, cap)
        return (0.5f + (clamped / (cap * 2f))).coerceIn(0f, 1f)
    }

    private fun updateModelState(
        previous: WorkoutModelState?,
        recentVolume: Float,
        recentIntensity: Float,
        avgProgressTrend: Float,
        now: LocalDate
    ): WorkoutModelState {
        val nowEpoch = now.toEpochDay()
        if (previous == null) {
            return WorkoutModelState(
                lastUpdatedEpochDay = nowEpoch,
                emaVolume = recentVolume,
                emaIntensity = recentIntensity,
                emaProgress = avgProgressTrend,
                minVolume = recentVolume,
                maxVolume = recentVolume,
                minIntensity = recentIntensity,
                maxIntensity = recentIntensity,
                sessionsSeen = 1
            )
        }

        val daysSince = nowEpoch - previous.lastUpdatedEpochDay
        if (daysSince > 28) {
            return WorkoutModelState(
                lastUpdatedEpochDay = nowEpoch,
                emaVolume = recentVolume,
                emaIntensity = recentIntensity,
                emaProgress = avgProgressTrend,
                minVolume = recentVolume,
                maxVolume = recentVolume,
                minIntensity = recentIntensity,
                maxIntensity = recentIntensity,
                sessionsSeen = 1,
                version = previous.version
            )
        }

        val alpha = (0.35f - 0.2f * reliability(previous.sessionsSeen, 20)).coerceIn(0.15f, 0.35f)
        val volumeSample = if (recentVolume > 0f) recentVolume else previous.emaVolume
        val intensitySample = if (recentIntensity > 0f) recentIntensity else previous.emaIntensity
        val progressSample = if (avgProgressTrend != 0f) avgProgressTrend else previous.emaProgress

        val emaVolume = emaUpdate(previous.emaVolume, volumeSample, alpha)
        val emaIntensity = emaUpdate(previous.emaIntensity, intensitySample, alpha)
        val emaProgress = emaUpdate(previous.emaProgress, progressSample, alpha)

        val minVolume = if (volumeSample > 0f) {
            if (previous.minVolume > 0f) min(previous.minVolume, volumeSample) else volumeSample
        } else {
            previous.minVolume
        }
        val maxVolume = if (volumeSample > 0f) {
            if (previous.maxVolume > 0f) max(previous.maxVolume, volumeSample) else volumeSample
        } else {
            previous.maxVolume
        }
        val minIntensity = if (intensitySample > 0f) {
            if (previous.minIntensity > 0f) min(previous.minIntensity, intensitySample) else intensitySample
        } else {
            previous.minIntensity
        }
        val maxIntensity = if (intensitySample > 0f) {
            if (previous.maxIntensity > 0f) max(previous.maxIntensity, intensitySample) else intensitySample
        } else {
            previous.maxIntensity
        }

        return WorkoutModelState(
            lastUpdatedEpochDay = nowEpoch,
            emaVolume = emaVolume,
            emaIntensity = emaIntensity,
            emaProgress = emaProgress,
            minVolume = minVolume,
            maxVolume = maxVolume,
            minIntensity = minIntensity,
            maxIntensity = maxIntensity,
            sessionsSeen = previous.sessionsSeen + 1,
            version = previous.version
        )
    }

    private fun emaUpdate(previous: Float, sample: Float, alpha: Float): Float {
        if (sample == 0f) return previous
        return if (previous == 0f) sample else previous + alpha * (sample - previous)
    }

    private fun calculateOptimalRestDays(fatigue: Float, recovery: RecoveryStatus): Int {
        val base = when (recovery.level) {
            RecoveryLevel.FULLY_RECOVERED -> 0
            RecoveryLevel.MOSTLY_RECOVERED -> 1
            RecoveryLevel.PARTIAL_RECOVERY -> 2
            RecoveryLevel.NEEDS_REST -> 3
        }
        return (base + (fatigue * 2).toInt()).coerceIn(0, 5)
    }
}
