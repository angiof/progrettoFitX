package com.app.fityo.tutor.analysis

import android.content.Context
import com.app.fityo.dominio.ExerciseErrorType
import com.app.fityo.dominio.ExerciseType
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

/**
 * Analyzer per Squat che usa la configurazione JSON.
 * Tutte le soglie, messaggi e hint sono definiti in assets/exercise_configs.json
 *
 * Checkpoint supportati:
 * - knee_angle: Profondità (angolo ginocchio al punto più basso)
 * - back_angle: Inclinazione schiena
 * - knee_valgus: Ginocchia verso l'interno
 * - heels: Talloni a terra
 */
class ConfigurableSquatAnalyzer(context: Context) : ConfigurableAnalyzer(context) {

    override val exerciseType = ExerciseType.SQUAT

    override fun extractMetrics(result: PoseLandmarkerResult): Map<String, Float?> {
        val metrics = mutableMapOf<String, Float?>()

        // 1. Angolo ginocchio (profondità)
        val leftKneeAngle = AngleCalculator.calculateKneeAngle(result, isLeft = true)
        val rightKneeAngle = AngleCalculator.calculateKneeAngle(result, isLeft = false)
        val avgKneeAngle = listOfNotNull(leftKneeAngle, rightKneeAngle)
            .takeIf { it.isNotEmpty() }
            ?.average()?.toFloat()
        metrics["knee_angle"] = avgKneeAngle

        // 2. Angolo schiena
        metrics["back_angle"] = AngleCalculator.calculateBackAngle(result)

        // 3. Knee valgus (rapporto larghezza ginocchia/caviglie)
        val leftKnee = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_KNEE)
        val rightKnee = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_KNEE)
        val leftAnkle = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_ANKLE)
        val rightAnkle = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_ANKLE)

        if (leftKnee != null && rightKnee != null && leftAnkle != null && rightAnkle != null) {
            val kneeWidth = rightKnee.x - leftKnee.x
            val ankleWidth = rightAnkle.x - leftAnkle.x
            if (ankleWidth > 0) {
                // Ratio: 1.0 = allineato, < 1.0 = valgus
                metrics["knee_valgus"] = (kneeWidth / ankleWidth).coerceIn(0.5f, 1.5f)
            }
        }

        // 4. Talloni (altezza relativa rispetto alla punta del piede)
        val leftHeelRaised = AngleCalculator.isHeelRaised(result, isLeft = true)
        val rightHeelRaised = AngleCalculator.isHeelRaised(result, isLeft = false)
        // Converti boolean in valore: 0 = a terra, 1 = sollevato
        metrics["heels"] = if (leftHeelRaised || rightHeelRaised) 1f else 0f

        return metrics
    }

    override fun getErrorTypeForCheckpoint(checkpointId: String): ExerciseErrorType {
        return when (checkpointId) {
            "knee_angle" -> ExerciseErrorType.SQUAT_DEPTH_INSUFFICIENT
            "back_angle" -> ExerciseErrorType.SQUAT_BACK_CURVED
            "knee_valgus" -> ExerciseErrorType.SQUAT_KNEES_CAVE_IN
            "heels" -> ExerciseErrorType.SQUAT_HEELS_RAISED
            else -> ExerciseErrorType.SQUAT_DEPTH_INSUFFICIENT
        }
    }

    override fun getAffectedLandmarks(checkpointId: String): List<Int> {
        return when (checkpointId) {
            "knee_angle" -> listOf(
                AngleCalculator.LandmarkIndex.LEFT_HIP,
                AngleCalculator.LandmarkIndex.LEFT_KNEE,
                AngleCalculator.LandmarkIndex.LEFT_ANKLE,
                AngleCalculator.LandmarkIndex.RIGHT_HIP,
                AngleCalculator.LandmarkIndex.RIGHT_KNEE,
                AngleCalculator.LandmarkIndex.RIGHT_ANKLE
            )
            "back_angle" -> listOf(
                AngleCalculator.LandmarkIndex.LEFT_SHOULDER,
                AngleCalculator.LandmarkIndex.RIGHT_SHOULDER,
                AngleCalculator.LandmarkIndex.LEFT_HIP,
                AngleCalculator.LandmarkIndex.RIGHT_HIP
            )
            "knee_valgus" -> listOf(
                AngleCalculator.LandmarkIndex.LEFT_KNEE,
                AngleCalculator.LandmarkIndex.RIGHT_KNEE
            )
            "heels" -> listOf(
                AngleCalculator.LandmarkIndex.LEFT_HEEL,
                AngleCalculator.LandmarkIndex.RIGHT_HEEL
            )
            else -> emptyList()
        }
    }
}
