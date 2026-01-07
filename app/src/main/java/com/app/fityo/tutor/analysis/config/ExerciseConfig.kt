package com.app.fityo.tutor.analysis.config

import com.app.fityo.dominio.ErrorSeverity

/**
 * Configurazione per un singolo esercizio caricata da JSON.
 * Permette di modificare soglie e messaggi senza ricompilare.
 */
data class ExerciseConfig(
    val exerciseId: String,
    val displayName: String,
    val description: String,
    val checkpoints: Map<String, CheckpointConfig>
)

/**
 * Configurazione per un checkpoint (punto di verifica) di un esercizio.
 * Es: angolo ginocchio, inclinazione schiena, etc.
 */
data class CheckpointConfig(
    val joint: String,
    val description: String,
    val thresholds: List<ThresholdLevel>,
    val correctionHints: Map<String, String>
)

/**
 * Singola soglia con il suo livello di severità.
 */
data class ThresholdLevel(
    val name: String,
    val minValue: Float? = null,
    val maxValue: Float? = null,
    val severity: ErrorSeverity,
    val message: String
) {
    /**
     * Verifica se un valore rientra in questa soglia.
     */
    fun matches(value: Float): Boolean {
        val minOk = minValue?.let { value >= it } ?: true
        val maxOk = maxValue?.let { value <= it } ?: true
        return minOk && maxOk
    }
}

/**
 * Impostazioni globali per l'analisi.
 */
data class GlobalAnalysisSettings(
    val consecutiveFramesThreshold: Int = 3,
    val visibilityThreshold: Float = 0.5f,
    val confidenceThreshold: Float = 0.6f,
    val feedbackDelay: Long = 500L
)

/**
 * Risultato dell'analisi di un checkpoint.
 */
data class CheckpointResult(
    val checkpointId: String,
    val currentValue: Float,
    val thresholdLevel: ThresholdLevel?,
    val correctionHint: String?,
    val isWithinTolerance: Boolean
) {
    val severity: ErrorSeverity
        get() = thresholdLevel?.severity ?: ErrorSeverity.SUGGESTION

    val message: String
        get() = thresholdLevel?.message ?: "Valore: ${currentValue.toInt()}°"
}
