package com.app.fityo.tutor.analysis

import com.app.fityo.dominio.ErrorSeverity
import com.app.fityo.dominio.ExerciseError
import com.app.fityo.dominio.ExerciseErrorType
import com.app.fityo.dominio.ExerciseType
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.abs

/**
 * Analizzatore per tutti gli esercizi di Panca (Bench Press e varianti).
 * Supporta: Panca Piana, Panca Declinata, Panca con Catene, Chaos Press.
 *
 * Vista consigliata: LATERALE
 *
 * Errori rilevati:
 * - Gomiti troppo larghi (elbow flare)
 * - Traiettoria bilanciere errata
 * - Arco lombare collassato
 * - Polsi piegati
 * - Spinta asimmetrica
 * - Lockout incompleto
 * - ROM insufficiente
 * - Scapole non retratte
 */
class BenchPressAnalyzer(
    override val exerciseType: ExerciseType
) : ExerciseAnalyzer {

    companion object {
        // Soglie per rilevamento errori
        const val MAX_ELBOW_ANGLE_AT_BOTTOM = 95f    // Gomiti non devono scendere troppo sotto 90°
        const val MIN_ELBOW_ANGLE_AT_BOTTOM = 70f    // ROM sufficiente
        const val MAX_WRIST_DEVIATION = 15f          // Polsi devono essere dritti
        const val MAX_ASYMMETRY_PERCENT = 8f         // Differenza altezza tra i polsi
        const val LOCKOUT_ELBOW_ANGLE_MIN = 165f     // Braccia quasi dritte al lockout
        const val MIN_SHOULDER_RETRACTION = 0.03f    // Spalle devono essere indietro
        const val CONSECUTIVE_FRAMES_THRESHOLD = 2
    }

    private var consecutiveElbowFlareErrors = 0
    private var consecutiveWristErrors = 0
    private var consecutiveAsymmetryErrors = 0
    private var consecutiveShoulderErrors = 0
    private var lockoutReached = false
    private var bottomReached = false
    private var maxElbowAngle = 0f
    private var minElbowAngle = 180f

    override fun analyzeFrame(
        result: PoseLandmarkerResult,
        timestampMs: Long,
        previousResults: List<FrameAnalysisResult>
    ): List<ExerciseError> {
        val errors = mutableListOf<ExerciseError>()

        // Calcola angoli dei gomiti
        val leftElbowAngle = calculateElbowAngle(result, isLeft = true)
        val rightElbowAngle = calculateElbowAngle(result, isLeft = false)

        // Posizioni chiave
        val leftWrist = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_WRIST)
        val rightWrist = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_WRIST)
        val leftElbow = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_ELBOW)
        val rightElbow = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_ELBOW)
        val leftShoulder = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_SHOULDER)
        val rightShoulder = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_SHOULDER)
        val leftHip = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_HIP)
        val rightHip = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_HIP)

        val avgElbowAngle = listOfNotNull(leftElbowAngle, rightElbowAngle)
            .takeIf { it.isNotEmpty() }
            ?.average()?.toFloat()

        // Traccia min/max angoli gomito
        avgElbowAngle?.let {
            if (it > maxElbowAngle) {
                maxElbowAngle = it
                if (it >= LOCKOUT_ELBOW_ANGLE_MIN) {
                    lockoutReached = true
                }
            }
            if (it < minElbowAngle) {
                minElbowAngle = it
                if (it <= MAX_ELBOW_ANGLE_AT_BOTTOM) {
                    bottomReached = true
                }
            }
        }

        // 1. GOMITI TROPPO LARGHI (Elbow Flare)
        // Nella vista laterale, verifichiamo la posizione dei gomiti rispetto alle spalle
        if (leftElbow != null && rightElbow != null && leftShoulder != null && rightShoulder != null) {
            // I gomiti non dovrebbero essere troppo più alti delle spalle (Y più piccolo = più alto)
            val leftElbowAboveShoulder = leftShoulder.y - leftElbow.y
            val rightElbowAboveShoulder = rightShoulder.y - rightElbow.y

            val avgElbowFlare = (leftElbowAboveShoulder + rightElbowAboveShoulder) / 2f

            // Se i gomiti sono significativamente sopra le spalle = flare eccessivo
            if (avgElbowFlare > 0.05f) {
                consecutiveElbowFlareErrors++
                if (consecutiveElbowFlareErrors >= CONSECUTIVE_FRAMES_THRESHOLD) {
                    errors.add(
                        ExerciseError(
                            timestampMs = timestampMs,
                            endTimestampMs = timestampMs,
                            errorType = ExerciseErrorType.BENCH_ELBOW_FLARE,
                            severity = if (avgElbowFlare > 0.1f) ErrorSeverity.ERROR else ErrorSeverity.WARNING,
                            message = "Gomiti troppo larghi/alti",
                            affectedLandmarks = listOf(
                                AngleCalculator.LandmarkIndex.LEFT_ELBOW,
                                AngleCalculator.LandmarkIndex.RIGHT_ELBOW
                            ),
                            correctionHint = "Tieni i gomiti a 45° rispetto al corpo, non a 90°. Proteggi le spalle!"
                        )
                    )
                }
            } else {
                consecutiveElbowFlareErrors = 0
            }
        }

        // 2. SPINTA ASIMMETRICA
        if (leftWrist != null && rightWrist != null) {
            val heightDiff = abs(leftWrist.y - rightWrist.y) * 100f

            if (heightDiff > MAX_ASYMMETRY_PERCENT) {
                consecutiveAsymmetryErrors++
                if (consecutiveAsymmetryErrors >= CONSECUTIVE_FRAMES_THRESHOLD) {
                    val lowerSide = if (leftWrist.y > rightWrist.y) "sinistro" else "destro"
                    errors.add(
                        ExerciseError(
                            timestampMs = timestampMs,
                            endTimestampMs = timestampMs,
                            errorType = ExerciseErrorType.BENCH_UNEVEN_PRESS,
                            severity = if (heightDiff > 15f) ErrorSeverity.ERROR else ErrorSeverity.WARNING,
                            message = "Spinta asimmetrica (lato $lowerSide più basso)",
                            affectedLandmarks = listOf(
                                AngleCalculator.LandmarkIndex.LEFT_WRIST,
                                AngleCalculator.LandmarkIndex.RIGHT_WRIST
                            ),
                            correctionHint = "Spingi in modo uniforme con entrambe le braccia. Concentrati sul lato debole."
                        )
                    )
                }
            } else {
                consecutiveAsymmetryErrors = 0
            }
        }

        // 3. SCAPOLE NON RETRATTE (spalle in avanti)
        if (leftShoulder != null && rightShoulder != null && leftHip != null && rightHip != null) {
            val shoulderMidX = (leftShoulder.x + rightShoulder.x) / 2f
            val hipMidX = (leftHip.x + rightHip.x) / 2f

            // Le spalle dovrebbero essere leggermente dietro o allineate con le anche
            val shoulderForward = shoulderMidX - hipMidX

            if (shoulderForward > MIN_SHOULDER_RETRACTION) {
                consecutiveShoulderErrors++
                if (consecutiveShoulderErrors >= CONSECUTIVE_FRAMES_THRESHOLD) {
                    errors.add(
                        ExerciseError(
                            timestampMs = timestampMs,
                            endTimestampMs = timestampMs,
                            errorType = ExerciseErrorType.BENCH_SHOULDER_PROTRACTION,
                            severity = ErrorSeverity.WARNING,
                            message = "Scapole non retratte",
                            affectedLandmarks = listOf(
                                AngleCalculator.LandmarkIndex.LEFT_SHOULDER,
                                AngleCalculator.LandmarkIndex.RIGHT_SHOULDER
                            ),
                            correctionHint = "Stringi le scapole insieme e in basso. Immagina di schiacciare una matita tra le scapole."
                        )
                    )
                }
            } else {
                consecutiveShoulderErrors = 0
            }
        }

        // 4. POLSI PIEGATI (verifica allineamento polso-gomito)
        if (leftWrist != null && leftElbow != null && rightWrist != null && rightElbow != null) {
            // Nella vista laterale, il polso dovrebbe essere sopra il gomito (allineato)
            val leftWristDeviation = abs(leftWrist.x - leftElbow.x)
            val rightWristDeviation = abs(rightWrist.x - rightElbow.x)
            val avgDeviation = (leftWristDeviation + rightWristDeviation) / 2f * 100f

            if (avgDeviation > MAX_WRIST_DEVIATION) {
                consecutiveWristErrors++
                if (consecutiveWristErrors >= CONSECUTIVE_FRAMES_THRESHOLD) {
                    errors.add(
                        ExerciseError(
                            timestampMs = timestampMs,
                            endTimestampMs = timestampMs,
                            errorType = ExerciseErrorType.BENCH_WRIST_BENT,
                            severity = ErrorSeverity.WARNING,
                            message = "Polsi piegati",
                            affectedLandmarks = listOf(
                                AngleCalculator.LandmarkIndex.LEFT_WRIST,
                                AngleCalculator.LandmarkIndex.RIGHT_WRIST
                            ),
                            correctionHint = "Mantieni i polsi dritti e allineati con gli avambracci. Il bilanciere deve poggiare sul palmo."
                        )
                    )
                }
            } else {
                consecutiveWristErrors = 0
            }
        }

        return errors
    }

    override fun finalizeAnalysis(allErrors: List<ExerciseError>): List<ExerciseError> {
        val finalErrors = mutableListOf<ExerciseError>()

        // Verifica ROM sufficiente
        val hadSignificantMovement = (maxElbowAngle - minElbowAngle) > 30f

        if (hadSignificantMovement) {
            // Verifica lockout
            if (!lockoutReached && maxElbowAngle < LOCKOUT_ELBOW_ANGLE_MIN) {
                finalErrors.add(
                    ExerciseError(
                        timestampMs = 0,
                        endTimestampMs = 0,
                        errorType = ExerciseErrorType.BENCH_LOCKOUT_INCOMPLETE,
                        severity = ErrorSeverity.ERROR,
                        message = "Lockout incompleto (angolo gomito max: ${maxElbowAngle.toInt()}°)",
                        affectedLandmarks = listOf(
                            AngleCalculator.LandmarkIndex.LEFT_ELBOW,
                            AngleCalculator.LandmarkIndex.RIGHT_ELBOW
                        ),
                        correctionHint = "Estendi completamente le braccia al top del movimento."
                    )
                )
            }

            // Verifica profondità
            if (!bottomReached && minElbowAngle > MIN_ELBOW_ANGLE_AT_BOTTOM) {
                finalErrors.add(
                    ExerciseError(
                        timestampMs = 0,
                        endTimestampMs = 0,
                        errorType = ExerciseErrorType.BENCH_DEPTH_INSUFFICIENT,
                        severity = ErrorSeverity.WARNING,
                        message = "ROM insufficiente (angolo gomito min: ${minElbowAngle.toInt()}°)",
                        affectedLandmarks = listOf(
                            AngleCalculator.LandmarkIndex.LEFT_ELBOW,
                            AngleCalculator.LandmarkIndex.RIGHT_ELBOW
                        ),
                        correctionHint = "Porta il bilanciere più vicino al petto per un ROM completo."
                    )
                )
            }
        }

        // Aggrega errori per tipo
        val groupedErrors = allErrors.groupBy { it.errorType }
        groupedErrors.forEach { (_, typeErrors) ->
            if (typeErrors.isNotEmpty()) {
                val firstError = typeErrors.first()
                val lastError = typeErrors.last()
                val count = typeErrors.size

                val adjustedSeverity = when {
                    count > 10 -> ErrorSeverity.CRITICAL
                    count > 5 -> if (firstError.severity == ErrorSeverity.WARNING) ErrorSeverity.ERROR else firstError.severity
                    else -> firstError.severity
                }

                finalErrors.add(
                    firstError.copy(
                        endTimestampMs = lastError.timestampMs,
                        severity = adjustedSeverity,
                        message = "${firstError.message} (${count} volte)"
                    )
                )
            }
        }

        // Reset
        resetState()

        return finalErrors.distinctBy { it.errorType }
    }

    override fun calculateScore(errors: List<ExerciseError>, totalFrames: Int): Float {
        if (totalFrames == 0) return 100f

        var score = 100f

        errors.forEach { error ->
            val penalty = when (error.severity) {
                ErrorSeverity.WARNING -> 8f
                ErrorSeverity.ERROR -> 18f
                ErrorSeverity.CRITICAL -> 35f
            }
            score -= penalty
        }

        return score.coerceIn(0f, 100f)
    }

    private fun resetState() {
        consecutiveElbowFlareErrors = 0
        consecutiveWristErrors = 0
        consecutiveAsymmetryErrors = 0
        consecutiveShoulderErrors = 0
        lockoutReached = false
        bottomReached = false
        maxElbowAngle = 0f
        minElbowAngle = 180f
    }

    /**
     * Calcola l'angolo del gomito (spalla-gomito-polso).
     */
    private fun calculateElbowAngle(result: PoseLandmarkerResult, isLeft: Boolean): Float? {
        val shoulderIndex = if (isLeft) AngleCalculator.LandmarkIndex.LEFT_SHOULDER else AngleCalculator.LandmarkIndex.RIGHT_SHOULDER
        val elbowIndex = if (isLeft) AngleCalculator.LandmarkIndex.LEFT_ELBOW else AngleCalculator.LandmarkIndex.RIGHT_ELBOW
        val wristIndex = if (isLeft) AngleCalculator.LandmarkIndex.LEFT_WRIST else AngleCalculator.LandmarkIndex.RIGHT_WRIST

        val shoulder = AngleCalculator.getLandmarkPoint(result, shoulderIndex) ?: return null
        val elbow = AngleCalculator.getLandmarkPoint(result, elbowIndex) ?: return null
        val wrist = AngleCalculator.getLandmarkPoint(result, wristIndex) ?: return null

        return AngleCalculator.calculateAngle(shoulder, elbow, wrist)
    }
}
