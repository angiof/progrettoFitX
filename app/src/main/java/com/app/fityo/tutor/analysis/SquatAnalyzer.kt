package com.app.fityo.tutor.analysis

import com.app.fityo.dominio.ErrorSeverity
import com.app.fityo.dominio.ExerciseError
import com.app.fityo.dominio.ExerciseErrorType
import com.app.fityo.dominio.ExerciseType
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

/**
 * Analizzatore per l'esercizio Squat.
 *
 * Errori rilevati:
 * - Profondità insufficiente (ginocchio > 100° al punto più basso)
 * - Schiena curva (torso > 45° dalla verticale)
 * - Talloni sollevati
 * - Ginocchia verso l'interno (knee valgus)
 */
class SquatAnalyzer : ExerciseAnalyzer {

    override val exerciseType = ExerciseType.SQUAT

    // Configurazione soglie graduali
    companion object {
        // Profondità squat - soglie graduali
        const val KNEE_ANGLE_PERFECT = 90f         // Angolo ideale (parallelo)
        const val KNEE_ANGLE_GOOD = 100f           // Buono ma migliorabile
        const val KNEE_ANGLE_ACCEPTABLE = 110f     // Accettabile, suggerimento
        const val KNEE_ANGLE_INSUFFICIENT = 120f   // Insufficiente, errore

        // Inclinazione schiena - soglie graduali
        const val BACK_ANGLE_PERFECT = 30f         // Ideale
        const val BACK_ANGLE_GOOD = 40f            // Buono
        const val BACK_ANGLE_ACCEPTABLE = 50f      // Accettabile, suggerimento
        const val BACK_ANGLE_EXCESSIVE = 60f       // Eccessivo, errore

        const val CONSECUTIVE_FRAMES_THRESHOLD = 3  // Frame consecutivi per confermare
    }

    // Tracking per debouncing
    private var consecutiveDepthErrors = 0
    private var consecutiveBackErrors = 0
    private var consecutiveHeelErrors = 0
    private var minKneeAngleReached = 180f

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
        val leftHeelRaised = AngleCalculator.isHeelRaised(result, isLeft = true)
        val rightHeelRaised = AngleCalculator.isHeelRaised(result, isLeft = false)

        // Calcola angolo medio ginocchio
        val avgKneeAngle = listOfNotNull(leftKneeAngle, rightKneeAngle)
            .takeIf { it.isNotEmpty() }
            ?.average()?.toFloat()

        // Traccia il minimo angolo raggiunto (punto più basso dello squat)
        avgKneeAngle?.let {
            if (it < minKneeAngleReached) {
                minKneeAngleReached = it
            }
        }

        // 1. Verifica schiena - con feedback graduato
        backAngle?.let {
            when {
                it > BACK_ANGLE_EXCESSIVE -> {
                    consecutiveBackErrors++
                    if (consecutiveBackErrors >= CONSECUTIVE_FRAMES_THRESHOLD) {
                        errors.add(
                            ExerciseError(
                                timestampMs = timestampMs,
                                endTimestampMs = timestampMs,
                                errorType = ExerciseErrorType.SQUAT_BACK_CURVED,
                                severity = ErrorSeverity.CRITICAL,
                                message = "Schiena troppo inclinata (${it.toInt()}°)",
                                affectedLandmarks = listOf(
                                    AngleCalculator.LandmarkIndex.LEFT_SHOULDER,
                                    AngleCalculator.LandmarkIndex.RIGHT_SHOULDER,
                                    AngleCalculator.LandmarkIndex.LEFT_HIP,
                                    AngleCalculator.LandmarkIndex.RIGHT_HIP
                                ),
                                correctionHint = "Attenzione alla schiena! Mantieni il petto alto e guarda avanti."
                            )
                        )
                    }
                }
                it > BACK_ANGLE_ACCEPTABLE -> {
                    consecutiveBackErrors++
                    if (consecutiveBackErrors >= CONSECUTIVE_FRAMES_THRESHOLD) {
                        errors.add(
                            ExerciseError(
                                timestampMs = timestampMs,
                                endTimestampMs = timestampMs,
                                errorType = ExerciseErrorType.SQUAT_BACK_CURVED,
                                severity = ErrorSeverity.ERROR,
                                message = "Schiena inclinata (${it.toInt()}°)",
                                affectedLandmarks = listOf(
                                    AngleCalculator.LandmarkIndex.LEFT_SHOULDER,
                                    AngleCalculator.LandmarkIndex.RIGHT_SHOULDER,
                                    AngleCalculator.LandmarkIndex.LEFT_HIP,
                                    AngleCalculator.LandmarkIndex.RIGHT_HIP
                                ),
                                correctionHint = "Cerca di mantenere la schiena piu dritta. Petto in fuori!"
                            )
                        )
                    }
                }
                it > BACK_ANGLE_GOOD -> {
                    consecutiveBackErrors++
                    if (consecutiveBackErrors >= CONSECUTIVE_FRAMES_THRESHOLD) {
                        errors.add(
                            ExerciseError(
                                timestampMs = timestampMs,
                                endTimestampMs = timestampMs,
                                errorType = ExerciseErrorType.SQUAT_BACK_CURVED,
                                severity = ErrorSeverity.SUGGESTION,
                                message = "Quasi perfetto! Schiena a ${it.toInt()}°",
                                affectedLandmarks = listOf(
                                    AngleCalculator.LandmarkIndex.LEFT_SHOULDER,
                                    AngleCalculator.LandmarkIndex.RIGHT_SHOULDER,
                                    AngleCalculator.LandmarkIndex.LEFT_HIP,
                                    AngleCalculator.LandmarkIndex.RIGHT_HIP
                                ),
                                correctionHint = "Ottimo lavoro! Prova a tenere il petto ancora piu alto per migliorare."
                            )
                        )
                    }
                }
                else -> {
                    consecutiveBackErrors = 0
                }
            }
        }

        // 2. Verifica talloni sollevati
        if (leftHeelRaised || rightHeelRaised) {
            consecutiveHeelErrors++
            if (consecutiveHeelErrors >= CONSECUTIVE_FRAMES_THRESHOLD) {
                errors.add(
                    ExerciseError(
                        timestampMs = timestampMs,
                        endTimestampMs = timestampMs,
                        errorType = ExerciseErrorType.SQUAT_HEELS_RAISED,
                        severity = ErrorSeverity.WARNING,
                        message = "Talloni sollevati dal suolo",
                        affectedLandmarks = listOf(
                            AngleCalculator.LandmarkIndex.LEFT_HEEL,
                            AngleCalculator.LandmarkIndex.RIGHT_HEEL
                        ),
                        correctionHint = "Tieni i talloni ben piantati a terra. Sposta il peso sui talloni."
                    )
                )
            }
        } else {
            consecutiveHeelErrors = 0
        }

        // 3. Verifica ginocchia verso l'interno (knee valgus)
        // Confronta posizione X delle ginocchia con le caviglie
        val leftKnee = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_KNEE)
        val rightKnee = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_KNEE)
        val leftAnkle = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_ANKLE)
        val rightAnkle = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_ANKLE)

        if (leftKnee != null && rightKnee != null && leftAnkle != null && rightAnkle != null) {
            val kneeWidth = rightKnee.x - leftKnee.x
            val ankleWidth = rightAnkle.x - leftAnkle.x

            // Se le ginocchia sono più vicine delle caviglie = valgus
            if (kneeWidth < ankleWidth * 0.85f && avgKneeAngle != null && avgKneeAngle < 140f) {
                errors.add(
                    ExerciseError(
                        timestampMs = timestampMs,
                        endTimestampMs = timestampMs,
                        errorType = ExerciseErrorType.SQUAT_KNEES_CAVE_IN,
                        severity = ErrorSeverity.ERROR,
                        message = "Ginocchia che cedono verso l'interno",
                        affectedLandmarks = listOf(
                            AngleCalculator.LandmarkIndex.LEFT_KNEE,
                            AngleCalculator.LandmarkIndex.RIGHT_KNEE
                        ),
                        correctionHint = "Spingi le ginocchia verso l'esterno, in linea con le punte dei piedi."
                    )
                )
            }
        }

        return errors
    }

    override fun finalizeAnalysis(allErrors: List<ExerciseError>): List<ExerciseError> {
        val finalErrors = mutableListOf<ExerciseError>()

        // Aggiungi feedback profondità con soglie graduali
        when {
            minKneeAngleReached > KNEE_ANGLE_INSUFFICIENT -> {
                finalErrors.add(
                    ExerciseError(
                        timestampMs = 0,
                        endTimestampMs = 0,
                        errorType = ExerciseErrorType.SQUAT_DEPTH_INSUFFICIENT,
                        severity = ErrorSeverity.CRITICAL,
                        message = "Profondita da migliorare (angolo: ${minKneeAngleReached.toInt()}°)",
                        affectedLandmarks = listOf(
                            AngleCalculator.LandmarkIndex.LEFT_HIP,
                            AngleCalculator.LandmarkIndex.LEFT_KNEE,
                            AngleCalculator.LandmarkIndex.RIGHT_HIP,
                            AngleCalculator.LandmarkIndex.RIGHT_KNEE
                        ),
                        correctionHint = "Prova a scendere di piu! L'obiettivo e portare le cosce parallele al suolo."
                    )
                )
            }
            minKneeAngleReached > KNEE_ANGLE_ACCEPTABLE -> {
                finalErrors.add(
                    ExerciseError(
                        timestampMs = 0,
                        endTimestampMs = 0,
                        errorType = ExerciseErrorType.SQUAT_DEPTH_INSUFFICIENT,
                        severity = ErrorSeverity.ERROR,
                        message = "Profondita insufficiente (angolo: ${minKneeAngleReached.toInt()}°)",
                        affectedLandmarks = listOf(
                            AngleCalculator.LandmarkIndex.LEFT_HIP,
                            AngleCalculator.LandmarkIndex.LEFT_KNEE,
                            AngleCalculator.LandmarkIndex.RIGHT_HIP,
                            AngleCalculator.LandmarkIndex.RIGHT_KNEE
                        ),
                        correctionHint = "Buon inizio! Scendi ancora un po' per raggiungere il parallelo."
                    )
                )
            }
            minKneeAngleReached > KNEE_ANGLE_GOOD -> {
                finalErrors.add(
                    ExerciseError(
                        timestampMs = 0,
                        endTimestampMs = 0,
                        errorType = ExerciseErrorType.SQUAT_DEPTH_INSUFFICIENT,
                        severity = ErrorSeverity.SUGGESTION,
                        message = "Quasi al parallelo! (angolo: ${minKneeAngleReached.toInt()}°)",
                        affectedLandmarks = listOf(
                            AngleCalculator.LandmarkIndex.LEFT_HIP,
                            AngleCalculator.LandmarkIndex.LEFT_KNEE,
                            AngleCalculator.LandmarkIndex.RIGHT_HIP,
                            AngleCalculator.LandmarkIndex.RIGHT_KNEE
                        ),
                        correctionHint = "Ottimo! Sei molto vicino. Solo qualche centimetro in piu per la perfezione!"
                    )
                )
            }
            // Se minKneeAngleReached <= KNEE_ANGLE_GOOD -> Profondità perfetta, nessun errore
        }

        // Aggrega errori dello stesso tipo (raggruppa per tipo, prendi il primo timestamp)
        val groupedErrors = allErrors.groupBy { it.errorType }
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

        // Reset tracking
        minKneeAngleReached = 180f
        consecutiveDepthErrors = 0
        consecutiveBackErrors = 0
        consecutiveHeelErrors = 0

        return finalErrors.distinctBy { it.errorType }
    }

    override fun calculateScore(errors: List<ExerciseError>, totalFrames: Int): Float {
        var score = 100f

        errors.forEach { error ->
            val penalty = when (error.severity) {
                ErrorSeverity.SUGGESTION -> 2f  // Penalità minima per suggerimenti
                ErrorSeverity.WARNING -> 5f
                ErrorSeverity.ERROR -> 15f
                ErrorSeverity.CRITICAL -> 25f
            }
            score -= penalty
        }

        return score.coerceIn(0f, 100f)
    }
}
