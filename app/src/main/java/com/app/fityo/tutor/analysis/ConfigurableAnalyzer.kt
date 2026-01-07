package com.app.fityo.tutor.analysis

import android.content.Context
import com.app.fityo.dominio.ErrorSeverity
import com.app.fityo.dominio.ExerciseError
import com.app.fityo.dominio.ExerciseErrorType
import com.app.fityo.dominio.ExerciseType
import com.app.fityo.tutor.analysis.config.CheckpointResult
import com.app.fityo.tutor.analysis.config.ExerciseConfig
import com.app.fityo.tutor.analysis.config.ExerciseConfigLoader
import com.app.fityo.tutor.analysis.config.GlobalAnalysisSettings
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

/**
 * Analyzer configurabile che carica soglie e messaggi dal file JSON.
 * Permette di modificare la validazione senza ricompilare l'app.
 *
 * Uso:
 * 1. Estendi questa classe per ogni esercizio
 * 2. Implementa extractMetrics() per calcolare i valori dai landmark
 * 3. Il resto della logica (soglie, messaggi, severità) viene dal JSON
 */
abstract class ConfigurableAnalyzer(
    private val context: Context
) : ExerciseAnalyzer {

    protected val config: ExerciseConfig? by lazy {
        ExerciseConfigLoader.getConfig(context, exerciseType)
    }

    protected val globalSettings: GlobalAnalysisSettings by lazy {
        ExerciseConfigLoader.getGlobalSettings(context)
    }

    // Tracking per debouncing (frame consecutivi per confermare errore)
    private val consecutiveErrors = mutableMapOf<String, Int>()

    // Valori min/max raggiunti durante l'analisi
    protected val minValues = mutableMapOf<String, Float>()
    protected val maxValues = mutableMapOf<String, Float>()

    /**
     * Estrae le metriche dal frame corrente.
     * Ogni analyzer deve implementare questo metodo per calcolare i valori specifici.
     *
     * @return Map di checkpoint_id -> valore calcolato
     */
    abstract fun extractMetrics(result: PoseLandmarkerResult): Map<String, Float?>

    /**
     * Mappa checkpoint_id -> ExerciseErrorType per generare errori appropriati.
     */
    abstract fun getErrorTypeForCheckpoint(checkpointId: String): ExerciseErrorType

    /**
     * Landmark coinvolti per ogni checkpoint (per evidenziare nell'UI).
     */
    abstract fun getAffectedLandmarks(checkpointId: String): List<Int>

    override fun analyzeFrame(
        result: PoseLandmarkerResult,
        timestampMs: Long,
        previousResults: List<FrameAnalysisResult>
    ): List<ExerciseError> {
        val errors = mutableListOf<ExerciseError>()
        val metrics = extractMetrics(result)

        metrics.forEach { (checkpointId, value) ->
            if (value == null) return@forEach

            // Traccia min/max
            trackMinMax(checkpointId, value)

            // Valuta il checkpoint usando la configurazione JSON
            val evaluation = ExerciseConfigLoader.evaluateCheckpoint(
                context = context,
                exerciseType = exerciseType,
                checkpointId = checkpointId,
                value = value
            )

            // Gestisci debouncing
            if (!evaluation.isWithinTolerance) {
                val currentCount = consecutiveErrors.getOrDefault(checkpointId, 0) + 1
                consecutiveErrors[checkpointId] = currentCount

                // Genera errore solo dopo N frame consecutivi
                if (currentCount >= globalSettings.consecutiveFramesThreshold) {
                    errors.add(createError(checkpointId, evaluation, timestampMs))
                }
            } else {
                consecutiveErrors[checkpointId] = 0
            }
        }

        return errors
    }

    override fun finalizeAnalysis(allErrors: List<ExerciseError>): List<ExerciseError> {
        val finalErrors = mutableListOf<ExerciseError>()

        // Valuta i valori min/max raggiunti durante l'intera analisi
        config?.checkpoints?.forEach { (checkpointId, checkpoint) ->
            // Per alcuni checkpoint valutiamo il minimo (es. profondità squat)
            minValues[checkpointId]?.let { minValue ->
                val evaluation = ExerciseConfigLoader.evaluateCheckpoint(
                    context = context,
                    exerciseType = exerciseType,
                    checkpointId = checkpointId,
                    value = minValue
                )

                if (!evaluation.isWithinTolerance && evaluation.severity != ErrorSeverity.SUGGESTION) {
                    finalErrors.add(createError(checkpointId, evaluation, 0L))
                } else if (evaluation.severity == ErrorSeverity.SUGGESTION) {
                    // Aggiungi anche i SUGGESTION per feedback incoraggiante
                    finalErrors.add(createError(checkpointId, evaluation, 0L))
                }
            }
        }

        // Aggrega errori simili
        val groupedErrors = allErrors.groupBy { it.errorType }
        groupedErrors.forEach { (_, errors) ->
            if (errors.isNotEmpty()) {
                val firstError = errors.first()
                val lastError = errors.last()
                finalErrors.add(
                    firstError.copy(
                        endTimestampMs = lastError.timestampMs,
                        message = "${firstError.message} (${errors.size} volte)"
                    )
                )
            }
        }

        // Reset per prossima analisi
        reset()

        return finalErrors.distinctBy { it.errorType }
    }

    override fun calculateScore(errors: List<ExerciseError>, totalFrames: Int): Float {
        var score = 100f

        errors.forEach { error ->
            val penalty = when (error.severity) {
                ErrorSeverity.SUGGESTION -> 2f   // Penalità minima per suggerimenti
                ErrorSeverity.WARNING -> 5f
                ErrorSeverity.ERROR -> 15f
                ErrorSeverity.CRITICAL -> 25f
            }
            score -= penalty
        }

        return score.coerceIn(0f, 100f)
    }

    private fun trackMinMax(checkpointId: String, value: Float) {
        val currentMin = minValues[checkpointId]
        val currentMax = maxValues[checkpointId]

        if (currentMin == null || value < currentMin) {
            minValues[checkpointId] = value
        }
        if (currentMax == null || value > currentMax) {
            maxValues[checkpointId] = value
        }
    }

    private fun createError(
        checkpointId: String,
        evaluation: CheckpointResult,
        timestampMs: Long
    ): ExerciseError {
        return ExerciseError(
            timestampMs = timestampMs,
            endTimestampMs = timestampMs,
            errorType = getErrorTypeForCheckpoint(checkpointId),
            severity = evaluation.severity,
            message = evaluation.message,
            affectedLandmarks = getAffectedLandmarks(checkpointId),
            correctionHint = evaluation.correctionHint ?: getDefaultHint(checkpointId)
        )
    }

    private fun getDefaultHint(checkpointId: String): String {
        return config?.checkpoints?.get(checkpointId)?.description
            ?: "Controlla la tua forma"
    }

    protected fun reset() {
        consecutiveErrors.clear()
        minValues.clear()
        maxValues.clear()
    }
}
