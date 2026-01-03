package com.app.fityo.tutor.analysis

import com.app.fityo.dominio.ErrorSeverity
import com.app.fityo.dominio.ExerciseError
import com.app.fityo.dominio.ExerciseErrorType
import com.app.fityo.dominio.ExerciseType
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.abs

/**
 * Analizzatore per l'esercizio Affondi (Lunges).
 *
 * Errori rilevati:
 * - Asimmetria delle anche
 * - Angolo ginocchio anteriore errato (< 70° o > 110°)
 * - Ginocchio posteriore troppo alto
 * - Busto troppo inclinato
 */
class LungesAnalyzer : ExerciseAnalyzer {

    override val exerciseType = ExerciseType.LUNGES

    companion object {
        const val MIN_FRONT_KNEE_ANGLE = 70f
        const val MAX_FRONT_KNEE_ANGLE = 110f
        const val MAX_HIP_ASYMMETRY = 5f  // Percentuale
        const val MAX_TORSO_LEAN = 30f     // Gradi dalla verticale
        const val CONSECUTIVE_FRAMES_THRESHOLD = 3
    }

    private var consecutiveHipErrors = 0
    private var consecutiveKneeErrors = 0
    private var consecutiveTorsoErrors = 0

    override fun analyzeFrame(
        result: PoseLandmarkerResult,
        timestampMs: Long,
        previousResults: List<FrameAnalysisResult>
    ): List<ExerciseError> {
        val errors = mutableListOf<ExerciseError>()

        // Calcola metriche
        val leftKneeAngle = AngleCalculator.calculateKneeAngle(result, isLeft = true)
        val rightKneeAngle = AngleCalculator.calculateKneeAngle(result, isLeft = false)
        val backAngle = AngleCalculator.calculateBackAngle(result)
        val hipAsymmetry = AngleCalculator.calculateHeightDifference(
            result,
            AngleCalculator.LandmarkIndex.LEFT_HIP,
            AngleCalculator.LandmarkIndex.RIGHT_HIP
        )

        // Determina quale gamba è davanti (ginocchio più flesso)
        val frontKneeAngle: Float?
        val backKneeAngle: Float?
        val isLeftFront: Boolean

        if (leftKneeAngle != null && rightKneeAngle != null) {
            if (leftKneeAngle < rightKneeAngle) {
                frontKneeAngle = leftKneeAngle
                backKneeAngle = rightKneeAngle
                isLeftFront = true
            } else {
                frontKneeAngle = rightKneeAngle
                backKneeAngle = leftKneeAngle
                isLeftFront = false
            }
        } else {
            frontKneeAngle = leftKneeAngle ?: rightKneeAngle
            backKneeAngle = null
            isLeftFront = leftKneeAngle != null
        }

        // 1. Verifica asimmetria anche
        hipAsymmetry?.let {
            if (abs(it) > MAX_HIP_ASYMMETRY) {
                consecutiveHipErrors++
                if (consecutiveHipErrors >= CONSECUTIVE_FRAMES_THRESHOLD) {
                    errors.add(
                        ExerciseError(
                            timestampMs = timestampMs,
                            endTimestampMs = timestampMs,
                            errorType = ExerciseErrorType.LUNGES_HIP_ASYMMETRY,
                            severity = if (abs(it) > 10f) ErrorSeverity.ERROR else ErrorSeverity.WARNING,
                            message = "Anche non allineate (${abs(it).toInt()}% di differenza)",
                            affectedLandmarks = listOf(
                                AngleCalculator.LandmarkIndex.LEFT_HIP,
                                AngleCalculator.LandmarkIndex.RIGHT_HIP
                            ),
                            correctionHint = "Mantieni le anche parallele al suolo. Non ruotare il bacino."
                        )
                    )
                }
            } else {
                consecutiveHipErrors = 0
            }
        }

        // 2. Verifica angolo ginocchio anteriore
        frontKneeAngle?.let {
            if (it < MIN_FRONT_KNEE_ANGLE || it > MAX_FRONT_KNEE_ANGLE) {
                consecutiveKneeErrors++
                if (consecutiveKneeErrors >= CONSECUTIVE_FRAMES_THRESHOLD) {
                    val message = if (it < MIN_FRONT_KNEE_ANGLE) {
                        "Ginocchio anteriore troppo flesso (${it.toInt()}°)"
                    } else {
                        "Ginocchio anteriore non flesso abbastanza (${it.toInt()}°)"
                    }

                    errors.add(
                        ExerciseError(
                            timestampMs = timestampMs,
                            endTimestampMs = timestampMs,
                            errorType = ExerciseErrorType.LUNGES_KNEE_ANGLE_WRONG,
                            severity = ErrorSeverity.ERROR,
                            message = message,
                            affectedLandmarks = listOf(
                                if (isLeftFront) AngleCalculator.LandmarkIndex.LEFT_KNEE
                                else AngleCalculator.LandmarkIndex.RIGHT_KNEE
                            ),
                            correctionHint = "Il ginocchio anteriore dovrebbe formare un angolo di circa 90° con il ginocchio sopra la caviglia."
                        )
                    )
                }
            } else {
                consecutiveKneeErrors = 0
            }
        }

        // 3. Verifica ginocchio posteriore
        backKneeAngle?.let {
            // Il ginocchio posteriore dovrebbe essere molto flesso (vicino al suolo)
            if (it > 150f) {
                errors.add(
                    ExerciseError(
                        timestampMs = timestampMs,
                        endTimestampMs = timestampMs,
                        errorType = ExerciseErrorType.LUNGES_BACK_KNEE_HIGH,
                        severity = ErrorSeverity.WARNING,
                        message = "Ginocchio posteriore troppo alto (${it.toInt()}°)",
                        affectedLandmarks = listOf(
                            if (!isLeftFront) AngleCalculator.LandmarkIndex.LEFT_KNEE
                            else AngleCalculator.LandmarkIndex.RIGHT_KNEE
                        ),
                        correctionHint = "Abbassa il ginocchio posteriore verso il suolo senza toccarlo."
                    )
                )
            }
        }

        // 4. Verifica inclinazione busto
        backAngle?.let {
            if (it > MAX_TORSO_LEAN) {
                consecutiveTorsoErrors++
                if (consecutiveTorsoErrors >= CONSECUTIVE_FRAMES_THRESHOLD) {
                    errors.add(
                        ExerciseError(
                            timestampMs = timestampMs,
                            endTimestampMs = timestampMs,
                            errorType = ExerciseErrorType.LUNGES_TORSO_LEAN,
                            severity = if (it > 45f) ErrorSeverity.ERROR else ErrorSeverity.WARNING,
                            message = "Busto troppo inclinato in avanti (${it.toInt()}°)",
                            affectedLandmarks = listOf(
                                AngleCalculator.LandmarkIndex.LEFT_SHOULDER,
                                AngleCalculator.LandmarkIndex.RIGHT_SHOULDER
                            ),
                            correctionHint = "Mantieni il busto eretto, petto in fuori."
                        )
                    )
                }
            } else {
                consecutiveTorsoErrors = 0
            }
        }

        return errors
    }

    override fun finalizeAnalysis(allErrors: List<ExerciseError>): List<ExerciseError> {
        val groupedErrors = allErrors.groupBy { it.errorType }
        val finalErrors = mutableListOf<ExerciseError>()

        groupedErrors.forEach { (errorType, errors) ->
            if (errors.isNotEmpty()) {
                val firstError = errors.first()
                val lastError = errors.last()
                finalErrors.add(
                    firstError.copy(
                        endTimestampMs = lastError.timestampMs,
                        message = "${firstError.message} (${errors.size} occorrenze)"
                    )
                )
            }
        }

        // Reset
        consecutiveHipErrors = 0
        consecutiveKneeErrors = 0
        consecutiveTorsoErrors = 0

        return finalErrors.distinctBy { it.errorType }
    }

    override fun calculateScore(errors: List<ExerciseError>, totalFrames: Int): Float {
        var score = 100f

        errors.forEach { error ->
            val penalty = when (error.severity) {
                ErrorSeverity.WARNING -> 5f
                ErrorSeverity.ERROR -> 15f
                ErrorSeverity.CRITICAL -> 25f
            }
            score -= penalty
        }

        return score.coerceIn(0f, 100f)
    }
}
