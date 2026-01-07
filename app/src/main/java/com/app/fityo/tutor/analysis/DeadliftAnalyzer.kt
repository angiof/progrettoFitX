package com.app.fityo.tutor.analysis

import com.app.fityo.dominio.ErrorSeverity
import com.app.fityo.dominio.ExerciseError
import com.app.fityo.dominio.ExerciseErrorType
import com.app.fityo.dominio.ExerciseType
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.abs

/**
 * Analizzatore per l'esercizio Stacco da Terra (Deadlift).
 *
 * Errori rilevati:
 * - Schiena arrotondata (curvatura eccessiva)
 * - Bilanciere non verticale (drift orizzontale)
 * - Hip hinge errato (ginocchia flettono troppo)
 * - Lockout incompleto
 * - Spalle avanti rispetto alla barra
 */
class DeadliftAnalyzer : ExerciseAnalyzer {

    override val exerciseType = ExerciseType.DEADLIFT

    companion object {
        // Soglie più sensibili per rilevare errori reali
        const val MAX_BACK_ANGLE_THRESHOLD = 25f       // Schiena troppo inclinata in avanti
        const val MAX_BACK_CURVATURE = 15f             // Curvatura della schiena (arrotondamento)
        const val MAX_BAR_DRIFT_PERCENT = 5f           // Drift orizzontale del bilanciere
        const val MIN_HIP_KNEE_RATIO = 0.5f            // L'anca deve flettersi più del ginocchio
        const val LOCKOUT_HIP_ANGLE_MIN = 165f         // Angolo minimo per lockout completo
        const val MAX_SHOULDER_FORWARD = 0.05f         // Spalle non devono essere troppo avanti
        const val CONSECUTIVE_FRAMES_THRESHOLD = 2     // Ridotto per più sensibilità
    }

    private var consecutiveBackErrors = 0
    private var consecutiveBarDriftErrors = 0
    private var consecutiveHipHingeErrors = 0
    private var consecutiveShoulderErrors = 0
    private var lockoutReached = false
    private var maxHipAngle = 0f
    private var minHipAngle = 180f

    // Per tracking del movimento del bilanciere
    private var initialWristX: Float? = null
    private var isDescendingPhase = true

    override fun analyzeFrame(
        result: PoseLandmarkerResult,
        timestampMs: Long,
        previousResults: List<FrameAnalysisResult>
    ): List<ExerciseError> {
        val errors = mutableListOf<ExerciseError>()

        // Calcola metriche
        val leftHipAngle = AngleCalculator.calculateHipAngle(result, isLeft = true)
        val rightHipAngle = AngleCalculator.calculateHipAngle(result, isLeft = false)
        val leftKneeAngle = AngleCalculator.calculateKneeAngle(result, isLeft = true)
        val rightKneeAngle = AngleCalculator.calculateKneeAngle(result, isLeft = false)
        val backAngle = AngleCalculator.calculateBackAngle(result)

        // Posizioni chiave
        val leftWrist = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_WRIST)
        val rightWrist = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_WRIST)
        val leftShoulder = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_SHOULDER)
        val rightShoulder = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_SHOULDER)
        val leftHip = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_HIP)
        val rightHip = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_HIP)

        val avgHipAngle = listOfNotNull(leftHipAngle, rightHipAngle)
            .takeIf { it.isNotEmpty() }
            ?.average()?.toFloat()

        val avgKneeAngle = listOfNotNull(leftKneeAngle, rightKneeAngle)
            .takeIf { it.isNotEmpty() }
            ?.average()?.toFloat()

        // Traccia angoli per lockout e fase del movimento
        avgHipAngle?.let {
            if (it > maxHipAngle) {
                maxHipAngle = it
                if (it >= LOCKOUT_HIP_ANGLE_MIN) {
                    lockoutReached = true
                }
            }
            if (it < minHipAngle) {
                minHipAngle = it
            }
            // Determina fase del movimento
            if (previousResults.isNotEmpty()) {
                val prevMetrics = previousResults.last().metrics
                prevMetrics.leftHipAngle?.let { prevHip ->
                    isDescendingPhase = it < prevHip
                }
            }
        }

        // 1. SCHIENA ARROTONDATA - verifica inclinazione eccessiva
        backAngle?.let { angle ->
            // Nel deadlift la schiena si inclina naturalmente, ma non deve arrotondarsi
            // Verifichiamo l'inclinazione e la curvatura
            val curvature = calculateBackCurvature(result)

            if (curvature != null && curvature > MAX_BACK_CURVATURE) {
                consecutiveBackErrors++
                if (consecutiveBackErrors >= CONSECUTIVE_FRAMES_THRESHOLD) {
                    val severity = when {
                        curvature > 25f -> ErrorSeverity.CRITICAL
                        curvature > 18f -> ErrorSeverity.ERROR
                        else -> ErrorSeverity.WARNING
                    }
                    errors.add(
                        ExerciseError(
                            timestampMs = timestampMs,
                            endTimestampMs = timestampMs,
                            errorType = ExerciseErrorType.DEADLIFT_BACK_ROUNDED,
                            severity = severity,
                            message = "Schiena arrotondata (${curvature.toInt()}° di curvatura)",
                            affectedLandmarks = listOf(
                                AngleCalculator.LandmarkIndex.LEFT_SHOULDER,
                                AngleCalculator.LandmarkIndex.RIGHT_SHOULDER,
                                AngleCalculator.LandmarkIndex.LEFT_HIP,
                                AngleCalculator.LandmarkIndex.RIGHT_HIP
                            ),
                            correctionHint = "Mantieni la schiena neutra. Petto in fuori, spalle indietro, core attivato."
                        )
                    )
                }
            } else {
                consecutiveBackErrors = 0
            }
        }

        // 2. SPALLE TROPPO AVANTI rispetto alla barra
        if (leftShoulder != null && rightShoulder != null && leftWrist != null && rightWrist != null) {
            val shoulderMidX = (leftShoulder.x + rightShoulder.x) / 2f
            val wristMidX = (leftWrist.x + rightWrist.x) / 2f

            // Le spalle non devono essere molto davanti alla barra
            val shoulderForward = shoulderMidX - wristMidX

            if (shoulderForward > MAX_SHOULDER_FORWARD) {
                consecutiveShoulderErrors++
                if (consecutiveShoulderErrors >= CONSECUTIVE_FRAMES_THRESHOLD) {
                    errors.add(
                        ExerciseError(
                            timestampMs = timestampMs,
                            endTimestampMs = timestampMs,
                            errorType = ExerciseErrorType.DEADLIFT_BAR_DRIFT,
                            severity = ErrorSeverity.WARNING,
                            message = "Spalle troppo avanti rispetto alla barra",
                            affectedLandmarks = listOf(
                                AngleCalculator.LandmarkIndex.LEFT_SHOULDER,
                                AngleCalculator.LandmarkIndex.RIGHT_SHOULDER
                            ),
                            correctionHint = "Mantieni le spalle sopra o leggermente dietro la barra."
                        )
                    )
                }
            } else {
                consecutiveShoulderErrors = 0
            }
        }

        // 3. DRIFT ORIZZONTALE della barra
        if (leftWrist != null && rightWrist != null) {
            val wristMidX = (leftWrist.x + rightWrist.x) / 2f

            if (initialWristX == null) {
                initialWristX = wristMidX
            } else {
                val drift = abs(wristMidX - initialWristX!!) * 100f

                if (drift > MAX_BAR_DRIFT_PERCENT) {
                    consecutiveBarDriftErrors++
                    if (consecutiveBarDriftErrors >= CONSECUTIVE_FRAMES_THRESHOLD) {
                        errors.add(
                            ExerciseError(
                                timestampMs = timestampMs,
                                endTimestampMs = timestampMs,
                                errorType = ExerciseErrorType.DEADLIFT_BAR_DRIFT,
                                severity = ErrorSeverity.WARNING,
                                message = "Bilanciere non verticale (${drift.toInt()}% di drift)",
                                affectedLandmarks = listOf(
                                    AngleCalculator.LandmarkIndex.LEFT_WRIST,
                                    AngleCalculator.LandmarkIndex.RIGHT_WRIST
                                ),
                                correctionHint = "Mantieni il bilanciere vicino al corpo, a contatto con le gambe."
                            )
                        )
                    }
                } else {
                    consecutiveBarDriftErrors = 0
                }
            }
        }

        // 4. HIP HINGE ERRATO - verifica che le anche si flettano più delle ginocchia
        if (avgHipAngle != null && avgKneeAngle != null) {
            val hipFlexion = 180f - avgHipAngle
            val kneeFlexion = 180f - avgKneeAngle

            // Se il ginocchio flette troppo rispetto all'anca = squat, non deadlift
            if (kneeFlexion > 20f && hipFlexion > 0) {
                val ratio = if (kneeFlexion > 0) hipFlexion / kneeFlexion else 1f

                if (ratio < MIN_HIP_KNEE_RATIO) {
                    consecutiveHipHingeErrors++
                    if (consecutiveHipHingeErrors >= CONSECUTIVE_FRAMES_THRESHOLD) {
                        errors.add(
                            ExerciseError(
                                timestampMs = timestampMs,
                                endTimestampMs = timestampMs,
                                errorType = ExerciseErrorType.DEADLIFT_HIP_HINGE_WRONG,
                                severity = ErrorSeverity.ERROR,
                                message = "Hip hinge insufficiente - stai facendo uno squat!",
                                affectedLandmarks = listOf(
                                    AngleCalculator.LandmarkIndex.LEFT_HIP,
                                    AngleCalculator.LandmarkIndex.RIGHT_HIP,
                                    AngleCalculator.LandmarkIndex.LEFT_KNEE,
                                    AngleCalculator.LandmarkIndex.RIGHT_KNEE
                                ),
                                correctionHint = "Il deadlift parte dalle anche! Porta i fianchi indietro, piega meno le ginocchia."
                            )
                        )
                    }
                } else {
                    consecutiveHipHingeErrors = 0
                }
            }
        }

        return errors
    }

    override fun finalizeAnalysis(allErrors: List<ExerciseError>): List<ExerciseError> {
        val finalErrors = mutableListOf<ExerciseError>()

        // Verifica lockout completo solo se c'è stato movimento significativo
        val hadSignificantMovement = (maxHipAngle - minHipAngle) > 20f

        if (hadSignificantMovement && !lockoutReached && maxHipAngle > 0 && maxHipAngle < LOCKOUT_HIP_ANGLE_MIN) {
            finalErrors.add(
                ExerciseError(
                    timestampMs = 0,
                    endTimestampMs = 0,
                    errorType = ExerciseErrorType.DEADLIFT_LOCKOUT_INCOMPLETE,
                    severity = ErrorSeverity.ERROR,
                    message = "Lockout incompleto (angolo anca max: ${maxHipAngle.toInt()}°, richiesto: ${LOCKOUT_HIP_ANGLE_MIN.toInt()}°)",
                    affectedLandmarks = listOf(
                        AngleCalculator.LandmarkIndex.LEFT_HIP,
                        AngleCalculator.LandmarkIndex.RIGHT_HIP
                    ),
                    correctionHint = "Completa il movimento: stringi i glutei e porta i fianchi completamente in avanti al top."
                )
            )
        }

        // Aggrega errori per tipo
        val groupedErrors = allErrors.groupBy { it.errorType }
        groupedErrors.forEach { (_, typeErrors) ->
            if (typeErrors.isNotEmpty()) {
                val firstError = typeErrors.first()
                val lastError = typeErrors.last()
                val count = typeErrors.size

                // Aggiusta severità in base alla frequenza
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

        // Reset stato
        resetState()

        return finalErrors.distinctBy { it.errorType }
    }

    override fun calculateScore(errors: List<ExerciseError>, totalFrames: Int): Float {
        if (totalFrames == 0) return 100f

        var score = 100f

        errors.forEach { error ->
            val penalty = when (error.severity) {
                ErrorSeverity.SUGGESTION -> 2f
                ErrorSeverity.WARNING -> 8f
                ErrorSeverity.ERROR -> 18f
                ErrorSeverity.CRITICAL -> 35f
            }
            score -= penalty
        }

        return score.coerceIn(0f, 100f)
    }

    private fun resetState() {
        consecutiveBackErrors = 0
        consecutiveBarDriftErrors = 0
        consecutiveHipHingeErrors = 0
        consecutiveShoulderErrors = 0
        lockoutReached = false
        maxHipAngle = 0f
        minHipAngle = 180f
        initialWristX = null
        isDescendingPhase = true
    }

    /**
     * Calcola la curvatura della schiena usando la deviazione dalla linea retta.
     * Verifica se la schiena è "arrotondata" controllando la posizione delle spalle
     * rispetto alle anche e la posizione della testa.
     */
    private fun calculateBackCurvature(result: PoseLandmarkerResult): Float? {
        val leftShoulder = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_SHOULDER)
        val rightShoulder = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_SHOULDER)
        val leftHip = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_HIP)
        val rightHip = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_HIP)
        val nose = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.NOSE)

        if (leftShoulder == null || rightShoulder == null || leftHip == null || rightHip == null) {
            return null
        }

        val midShoulder = AngleCalculator.Point2D(
            (leftShoulder.x + rightShoulder.x) / 2f,
            (leftShoulder.y + rightShoulder.y) / 2f
        )
        val midHip = AngleCalculator.Point2D(
            (leftHip.x + rightHip.x) / 2f,
            (leftHip.y + rightHip.y) / 2f
        )

        // Calcola l'angolo del torso dalla verticale
        val torsoAngle = AngleCalculator.calculateAngleFromVertical(midShoulder, midHip)

        // Se la testa è disponibile, verifica anche la posizione della testa
        // Una schiena arrotondata spesso ha la testa che "cade" in avanti
        val headForwardPenalty = nose?.let { n ->
            val headForward = n.x - midShoulder.x
            if (headForward > 0.08f) 10f else 0f  // Testa troppo avanti
        } ?: 0f

        // L'inclinazione del torso è normale nel deadlift, ma penalizziamo
        // inclinazioni eccessive che indicano arrotondamento
        return when {
            torsoAngle > 60f -> torsoAngle - 40f + headForwardPenalty  // Molto inclinato = problema
            torsoAngle > 45f -> (torsoAngle - 45f) * 0.8f + headForwardPenalty  // Moderatamente inclinato
            else -> headForwardPenalty  // OK, solo eventuale penalità testa
        }
    }
}
