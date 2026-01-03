package com.app.fityo.tutor.analysis

import com.app.fityo.dominio.ExerciseCategory
import com.app.fityo.dominio.ExerciseType
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.abs

/**
 * Valida se il video contiene l'esercizio corretto basandosi sulla postura.
 * Rileva quando l'utente sta facendo un esercizio diverso da quello selezionato.
 */
object ExerciseValidator {

    /**
     * Risultato della validazione del video.
     */
    data class ValidationResult(
        val isValid: Boolean,
        val detectedCategory: ExerciseCategory?,
        val confidence: Float,
        val message: String
    )

    /**
     * Valida una serie di frame per determinare se l'esercizio è corretto.
     *
     * @param results Lista di risultati pose (almeno 5-10 frame)
     * @param expectedExercise Tipo di esercizio che l'utente ha selezionato
     * @return Risultato della validazione
     */
    fun validateExercise(
        results: List<PoseLandmarkerResult>,
        expectedExercise: ExerciseType
    ): ValidationResult {
        if (results.isEmpty()) {
            return ValidationResult(
                isValid = false,
                detectedCategory = null,
                confidence = 0f,
                message = "Nessuna posa rilevata nel video"
            )
        }

        // Analizza la postura prevalente
        val postureAnalysis = analyzePosture(results)

        // Determina la categoria rilevata
        val detectedCategory = detectExerciseCategory(postureAnalysis)

        // Verifica se la categoria corrisponde
        val isValid = when {
            detectedCategory == null -> false
            detectedCategory == expectedExercise.category -> true
            // Permetti alcune eccezioni (es. deadlift vs squat hanno posture simili in certi momenti)
            areCompatibleCategories(detectedCategory, expectedExercise.category) -> true
            else -> false
        }

        val message = when {
            detectedCategory == null -> "Impossibile determinare l'esercizio. Assicurati che il corpo sia visibile."
            isValid -> "Esercizio corretto rilevato"
            else -> getWrongExerciseMessage(detectedCategory, expectedExercise)
        }

        return ValidationResult(
            isValid = isValid,
            detectedCategory = detectedCategory,
            confidence = postureAnalysis.confidence,
            message = message
        )
    }

    /**
     * Analizza rapidamente i primi frame per una validazione veloce.
     */
    fun quickValidate(
        results: List<PoseLandmarkerResult>,
        expectedExercise: ExerciseType
    ): Boolean {
        if (results.size < 3) return true // Non abbastanza dati per invalidare

        val postureAnalysis = analyzePosture(results.take(10))
        val detectedCategory = detectExerciseCategory(postureAnalysis)

        return detectedCategory == null ||
                detectedCategory == expectedExercise.category ||
                areCompatibleCategories(detectedCategory, expectedExercise.category)
    }

    private data class PostureAnalysis(
        val isLyingDown: Boolean,       // Persona sdraiata (panca)
        val isStanding: Boolean,        // Persona in piedi
        val hasArmMovement: Boolean,    // Movimento significativo delle braccia
        val hasLegMovement: Boolean,    // Movimento significativo delle gambe
        val avgTorsoAngle: Float,       // Angolo medio del torso
        val avgKneeFlexion: Float,      // Flessione media ginocchio
        val avgHipFlexion: Float,       // Flessione media anca
        val confidence: Float
    )

    private fun analyzePosture(results: List<PoseLandmarkerResult>): PostureAnalysis {
        var lyingDownCount = 0
        var standingCount = 0
        var armMovementCount = 0
        var legMovementCount = 0
        var totalTorsoAngle = 0f
        var totalKneeFlexion = 0f
        var totalHipFlexion = 0f
        var validFrames = 0

        var prevElbowAngle: Float? = null
        var prevKneeAngle: Float? = null

        for (result in results) {
            if (result.landmarks().isEmpty()) continue
            validFrames++

            val landmarks = result.landmarks()[0]

            // Verifica se sdraiato (confronta Y di spalle e anche)
            val leftShoulder = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_SHOULDER)
            val rightShoulder = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_SHOULDER)
            val leftHip = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_HIP)
            val rightHip = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_HIP)

            if (leftShoulder != null && leftHip != null) {
                val shoulderHipDiff = abs(leftShoulder.y - leftHip.y)
                if (shoulderHipDiff < 0.15f) {
                    // Spalle e anche quasi alla stessa altezza = sdraiato
                    lyingDownCount++
                } else {
                    standingCount++
                }
            }

            // Calcola angoli
            val torsoAngle = AngleCalculator.calculateBackAngle(result)
            val leftKnee = AngleCalculator.calculateKneeAngle(result, isLeft = true)
            val rightKnee = AngleCalculator.calculateKneeAngle(result, isLeft = false)
            val leftHipAngle = AngleCalculator.calculateHipAngle(result, isLeft = true)
            val rightHipAngle = AngleCalculator.calculateHipAngle(result, isLeft = false)

            torsoAngle?.let { totalTorsoAngle += it }

            val avgKnee = listOfNotNull(leftKnee, rightKnee).average().toFloat()
            val avgHip = listOfNotNull(leftHipAngle, rightHipAngle).average().toFloat()

            if (!avgKnee.isNaN()) {
                totalKneeFlexion += 180f - avgKnee
            }
            if (!avgHip.isNaN()) {
                totalHipFlexion += 180f - avgHip
            }

            // Rileva movimento braccia (confronta con frame precedente)
            val leftElbow = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_ELBOW)
            val rightElbow = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_ELBOW)
            val leftWrist = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.LEFT_WRIST)
            val rightWrist = AngleCalculator.getLandmarkPoint(result, AngleCalculator.LandmarkIndex.RIGHT_WRIST)

            if (leftElbow != null && leftWrist != null) {
                val currentElbowAngle = AngleCalculator.calculateAngle(
                    leftShoulder ?: AngleCalculator.Point2D(0f, 0f),
                    leftElbow,
                    leftWrist
                )

                prevElbowAngle?.let { prev ->
                    if (abs(currentElbowAngle - prev) > 15f) {
                        armMovementCount++
                    }
                }
                prevElbowAngle = currentElbowAngle
            }

            // Rileva movimento gambe
            val currentKneeAngle = avgKnee
            prevKneeAngle?.let { prev ->
                if (!currentKneeAngle.isNaN() && abs(currentKneeAngle - prev) > 10f) {
                    legMovementCount++
                }
            }
            if (!currentKneeAngle.isNaN()) {
                prevKneeAngle = currentKneeAngle
            }
        }

        val frameCount = validFrames.coerceAtLeast(1)

        return PostureAnalysis(
            isLyingDown = lyingDownCount > standingCount,
            isStanding = standingCount >= lyingDownCount,
            hasArmMovement = armMovementCount > frameCount * 0.3,
            hasLegMovement = legMovementCount > frameCount * 0.3,
            avgTorsoAngle = totalTorsoAngle / frameCount,
            avgKneeFlexion = totalKneeFlexion / frameCount,
            avgHipFlexion = totalHipFlexion / frameCount,
            confidence = validFrames.toFloat() / results.size.coerceAtLeast(1)
        )
    }

    private fun detectExerciseCategory(analysis: PostureAnalysis): ExerciseCategory? {
        return when {
            // Sdraiato con movimento braccia = Petto
            analysis.isLyingDown && analysis.hasArmMovement -> ExerciseCategory.CHEST

            // In piedi con alta flessione ginocchio e anca = Gambe
            analysis.isStanding && analysis.avgKneeFlexion > 20f && analysis.avgHipFlexion > 20f -> ExerciseCategory.LOWER_BODY

            // In piedi con torso molto inclinato e flessione anca > ginocchio = Deadlift (ancora gambe)
            analysis.isStanding && analysis.avgTorsoAngle > 30f && analysis.avgHipFlexion > analysis.avgKneeFlexion -> ExerciseCategory.LOWER_BODY

            // In piedi con principalmente movimento braccia = potrebbe essere spalle o braccia
            analysis.isStanding && analysis.hasArmMovement && !analysis.hasLegMovement -> ExerciseCategory.SHOULDERS

            // Default se ci sono abbastanza dati
            analysis.confidence > 0.5f -> ExerciseCategory.LOWER_BODY

            else -> null
        }
    }

    private fun areCompatibleCategories(detected: ExerciseCategory, expected: ExerciseCategory): Boolean {
        // Alcune categorie possono avere posture simili in certi momenti
        return when {
            // Deadlift e Squat sono entrambi lower body
            detected == ExerciseCategory.LOWER_BODY && expected == ExerciseCategory.LOWER_BODY -> true
            else -> false
        }
    }

    private fun getWrongExerciseMessage(detected: ExerciseCategory, expected: ExerciseType): String {
        val detectedName = when (detected) {
            ExerciseCategory.LOWER_BODY -> "un esercizio per le gambe"
            ExerciseCategory.CHEST -> "un esercizio per il petto (panca)"
            ExerciseCategory.BACK -> "un esercizio per la schiena"
            ExerciseCategory.SHOULDERS -> "un esercizio per le spalle"
            ExerciseCategory.ARMS -> "un esercizio per le braccia"
        }

        return "Sembra che tu stia facendo $detectedName, non ${expected.displayName}. " +
                "Carica il video corretto o seleziona l'esercizio giusto."
    }
}
