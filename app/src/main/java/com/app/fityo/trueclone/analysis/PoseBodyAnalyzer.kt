package com.app.fityo.trueclone.analysis

import android.content.Context
import android.graphics.Bitmap
import com.app.fityo.mediapipe.PoseLandmarkerHelper
import com.app.fityo.trueclone.mesh.ShapeParameters
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Analizza le foto dell'utente usando MediaPipe Pose per estrarre
 * MISURAZIONI REALI del corpo dai 33 landmark.
 *
 * Landmark principali usati:
 * - 11, 12: Spalle (LEFT_SHOULDER, RIGHT_SHOULDER)
 * - 13, 14: Gomiti (LEFT_ELBOW, RIGHT_ELBOW)
 * - 15, 16: Polsi (LEFT_WRIST, RIGHT_WRIST)
 * - 23, 24: Fianchi (LEFT_HIP, RIGHT_HIP)
 * - 25, 26: Ginocchia (LEFT_KNEE, RIGHT_KNEE)
 * - 27, 28: Caviglie (LEFT_ANKLE, RIGHT_ANKLE)
 */
class PoseBodyAnalyzer(context: Context) {

    private val poseLandmarker = PoseLandmarkerHelper(context)

    companion object {
        // Landmark indices
        const val LEFT_SHOULDER = 11
        const val RIGHT_SHOULDER = 12
        const val LEFT_ELBOW = 13
        const val RIGHT_ELBOW = 14
        const val LEFT_WRIST = 15
        const val RIGHT_WRIST = 16
        const val LEFT_HIP = 23
        const val RIGHT_HIP = 24
        const val LEFT_KNEE = 25
        const val RIGHT_KNEE = 26
        const val LEFT_ANKLE = 27
        const val RIGHT_ANKLE = 28
        const val NOSE = 0
        const val LEFT_EAR = 7
        const val RIGHT_EAR = 8
    }

    /**
     * Risultato dell'analisi pose con misurazioni estratte.
     */
    data class BodyMeasurements(
        val shoulderWidthRatio: Float,      // Rapporto spalle/altezza
        val hipWidthRatio: Float,           // Rapporto fianchi/altezza
        val shoulderToHipRatio: Float,      // Spalle/Fianchi
        val torsoLengthRatio: Float,        // Lunghezza torso/altezza
        val armLengthRatio: Float,          // Lunghezza braccio/altezza
        val legLengthRatio: Float,          // Lunghezza gamba/altezza
        val armSpanRatio: Float,            // Apertura braccia/altezza
        val upperBodyRatio: Float,          // Parte superiore/inferiore
        val confidence: Float,              // Confidenza rilevamento
        val landmarksDetected: Int          // Numero landmark rilevati
    )

    /**
     * Analizza una foto e estrae le misurazioni del corpo.
     */
    fun analyzePhoto(bitmap: Bitmap): BodyMeasurements? {
        val result = poseLandmarker.detectImage(bitmap) ?: return null

        if (!poseLandmarker.hasValidPose(result.result)) {
            return null
        }

        return extractMeasurements(result.result, bitmap.height)
    }

    /**
     * Analizza multiple foto e combina i risultati.
     */
    fun analyzeMultiplePhotos(photos: List<Bitmap>): BodyMeasurements? {
        val measurements = photos.mapNotNull { analyzePhoto(it) }

        if (measurements.isEmpty()) return null

        // Media pesata per confidenza
        var totalWeight = 0f
        var shoulderWidth = 0f
        var hipWidth = 0f
        var shoulderToHip = 0f
        var torsoLength = 0f
        var armLength = 0f
        var legLength = 0f
        var armSpan = 0f
        var upperBody = 0f
        var totalLandmarks = 0

        for (m in measurements) {
            val weight = m.confidence
            totalWeight += weight
            shoulderWidth += m.shoulderWidthRatio * weight
            hipWidth += m.hipWidthRatio * weight
            shoulderToHip += m.shoulderToHipRatio * weight
            torsoLength += m.torsoLengthRatio * weight
            armLength += m.armLengthRatio * weight
            legLength += m.legLengthRatio * weight
            armSpan += m.armSpanRatio * weight
            upperBody += m.upperBodyRatio * weight
            totalLandmarks += m.landmarksDetected
        }

        if (totalWeight <= 0) return null

        return BodyMeasurements(
            shoulderWidthRatio = shoulderWidth / totalWeight,
            hipWidthRatio = hipWidth / totalWeight,
            shoulderToHipRatio = shoulderToHip / totalWeight,
            torsoLengthRatio = torsoLength / totalWeight,
            armLengthRatio = armLength / totalWeight,
            legLengthRatio = legLength / totalWeight,
            armSpanRatio = armSpan / totalWeight,
            upperBodyRatio = upperBody / totalWeight,
            confidence = totalWeight / measurements.size,
            landmarksDetected = totalLandmarks / measurements.size
        )
    }

    /**
     * Estrae misurazioni dai landmark MediaPipe.
     */
    private fun extractMeasurements(
        result: PoseLandmarkerResult,
        imageHeight: Int
    ): BodyMeasurements? {
        if (result.landmarks().isEmpty()) return null

        val landmarks = result.landmarks()[0]
        if (landmarks.size < 33) return null

        // Calcola altezza totale (dalla testa ai piedi)
        val headY = landmarks[NOSE].y()
        val leftAnkleY = landmarks[LEFT_ANKLE].y()
        val rightAnkleY = landmarks[RIGHT_ANKLE].y()
        val feetY = maxOf(leftAnkleY, rightAnkleY)
        val bodyHeight = abs(feetY - headY)

        if (bodyHeight < 0.1f) return null // Corpo troppo piccolo nell'immagine

        // Larghezza spalle
        val shoulderWidth = distance2D(
            landmarks[LEFT_SHOULDER].x(), landmarks[LEFT_SHOULDER].y(),
            landmarks[RIGHT_SHOULDER].x(), landmarks[RIGHT_SHOULDER].y()
        )

        // Larghezza fianchi
        val hipWidth = distance2D(
            landmarks[LEFT_HIP].x(), landmarks[LEFT_HIP].y(),
            landmarks[RIGHT_HIP].x(), landmarks[RIGHT_HIP].y()
        )

        // Lunghezza torso (spalle -> fianchi)
        val leftShoulderY = landmarks[LEFT_SHOULDER].y()
        val rightShoulderY = landmarks[RIGHT_SHOULDER].y()
        val leftHipY = landmarks[LEFT_HIP].y()
        val rightHipY = landmarks[RIGHT_HIP].y()
        val shoulderMidY = (leftShoulderY + rightShoulderY) / 2
        val hipMidY = (leftHipY + rightHipY) / 2
        val torsoLength = abs(hipMidY - shoulderMidY)

        // Lunghezza braccio (spalla -> gomito -> polso)
        val leftArmUpper = distance2D(
            landmarks[LEFT_SHOULDER].x(), landmarks[LEFT_SHOULDER].y(),
            landmarks[LEFT_ELBOW].x(), landmarks[LEFT_ELBOW].y()
        )
        val leftArmLower = distance2D(
            landmarks[LEFT_ELBOW].x(), landmarks[LEFT_ELBOW].y(),
            landmarks[LEFT_WRIST].x(), landmarks[LEFT_WRIST].y()
        )
        val rightArmUpper = distance2D(
            landmarks[RIGHT_SHOULDER].x(), landmarks[RIGHT_SHOULDER].y(),
            landmarks[RIGHT_ELBOW].x(), landmarks[RIGHT_ELBOW].y()
        )
        val rightArmLower = distance2D(
            landmarks[RIGHT_ELBOW].x(), landmarks[RIGHT_ELBOW].y(),
            landmarks[RIGHT_WRIST].x(), landmarks[RIGHT_WRIST].y()
        )
        val armLength = ((leftArmUpper + leftArmLower) + (rightArmUpper + rightArmLower)) / 2

        // Lunghezza gamba (anca -> ginocchio -> caviglia)
        val leftLegUpper = distance2D(
            landmarks[LEFT_HIP].x(), landmarks[LEFT_HIP].y(),
            landmarks[LEFT_KNEE].x(), landmarks[LEFT_KNEE].y()
        )
        val leftLegLower = distance2D(
            landmarks[LEFT_KNEE].x(), landmarks[LEFT_KNEE].y(),
            landmarks[LEFT_ANKLE].x(), landmarks[LEFT_ANKLE].y()
        )
        val rightLegUpper = distance2D(
            landmarks[RIGHT_HIP].x(), landmarks[RIGHT_HIP].y(),
            landmarks[RIGHT_KNEE].x(), landmarks[RIGHT_KNEE].y()
        )
        val rightLegLower = distance2D(
            landmarks[RIGHT_KNEE].x(), landmarks[RIGHT_KNEE].y(),
            landmarks[RIGHT_ANKLE].x(), landmarks[RIGHT_ANKLE].y()
        )
        val legLength = ((leftLegUpper + leftLegLower) + (rightLegUpper + rightLegLower)) / 2

        // Apertura braccia (da polso a polso)
        val armSpan = distance2D(
            landmarks[LEFT_WRIST].x(), landmarks[LEFT_WRIST].y(),
            landmarks[RIGHT_WRIST].x(), landmarks[RIGHT_WRIST].y()
        )

        // Calcola confidenza media
        val keyLandmarks = listOf(
            LEFT_SHOULDER, RIGHT_SHOULDER, LEFT_HIP, RIGHT_HIP,
            LEFT_ELBOW, RIGHT_ELBOW, LEFT_KNEE, RIGHT_KNEE
        )
        val avgConfidence = keyLandmarks.mapNotNull { idx ->
            landmarks[idx].visibility().orElse(null)
        }.average().toFloat()

        return BodyMeasurements(
            shoulderWidthRatio = shoulderWidth / bodyHeight,
            hipWidthRatio = hipWidth / bodyHeight,
            shoulderToHipRatio = if (hipWidth > 0.01f) shoulderWidth / hipWidth else 1.3f,
            torsoLengthRatio = torsoLength / bodyHeight,
            armLengthRatio = armLength / bodyHeight,
            legLengthRatio = legLength / bodyHeight,
            armSpanRatio = armSpan / bodyHeight,
            upperBodyRatio = torsoLength / (torsoLength + legLength),
            confidence = avgConfidence,
            landmarksDetected = landmarks.size
        )
    }

    /**
     * Converte le misurazioni in ShapeParameters per la mesh.
     */
    fun measurementsToShapeParameters(
        measurements: BodyMeasurements,
        userHeightCm: Float,
        userWeightKg: Float
    ): ShapeParameters {
        // Converti rapporti in misure assolute
        val shoulderWidthCm = measurements.shoulderWidthRatio * userHeightCm
        val hipWidthCm = measurements.hipWidthRatio * userHeightCm
        val torsoLengthCm = measurements.torsoLengthRatio * userHeightCm
        val armLengthCm = measurements.armLengthRatio * userHeightCm
        val legLengthCm = measurements.legLengthRatio * userHeightCm

        // Stima circonferenze da larghezze (approssimazione ellittica)
        val chestCircumference = shoulderWidthCm * 2.2f  // Ellisse approssimata
        val waistCircumference = hipWidthCm * 2.0f

        // Calcola BMI per stimare massa muscolare
        val bmi = userWeightKg / ((userHeightCm / 100f) * (userHeightCm / 100f))

        // Stima sviluppo muscolare basato su:
        // - Rapporto spalle/fianchi (più alto = più sviluppo upper body)
        // - BMI (nel range 22-28 con basso BF = muscolare)
        val shoulderDevelopment = ((measurements.shoulderToHipRatio - 1.0f) / 0.5f).coerceIn(0f, 1f)
        val bmiMuscle = if (bmi > 22f && bmi < 30f) {
            ((bmi - 22f) / 8f).coerceIn(0f, 1f) * 0.7f
        } else {
            0.3f
        }

        // Calcola muscle scores per zona basandosi sulle proporzioni
        val upperBodyScore = (shoulderDevelopment * 0.6f + bmiMuscle * 0.4f).coerceIn(0.2f, 0.9f)
        val lowerBodyScore = (measurements.legLengthRatio / 0.5f).coerceIn(0.3f, 0.8f)

        return ShapeParameters(
            heightCm = userHeightCm,
            weightKg = userWeightKg,
            shoulderWidthCm = shoulderWidthCm.coerceIn(35f, 55f),
            hipWidthCm = hipWidthCm.coerceIn(28f, 45f),
            chestCircumferenceCm = chestCircumference.coerceIn(80f, 130f),
            waistCircumferenceCm = waistCircumference.coerceIn(60f, 110f),
            armLengthCm = armLengthCm.coerceIn(50f, 75f),
            legLengthCm = legLengthCm.coerceIn(70f, 100f),
            shoulderToHipRatio = measurements.shoulderToHipRatio.coerceIn(0.9f, 1.6f),
            waistToHipRatio = (waistCircumference / chestCircumference).coerceIn(0.7f, 1.0f),
            // Muscle scores derivati dall'analisi
            chestMuscle = upperBodyScore,
            backMuscle = upperBodyScore * 0.95f,
            shoulderMuscle = upperBodyScore * 1.05f,
            armMuscle = upperBodyScore * 0.9f,
            absMuscle = ((upperBodyScore + lowerBodyScore) / 2f) * 0.85f,
            glutesMuscle = lowerBodyScore,
            quadsMuscle = lowerBodyScore * 1.05f,
            hamstringsMuscle = lowerBodyScore * 0.95f,
            calvesMuscle = lowerBodyScore * 0.85f
        ).calculateBetaFromMeasurements()
    }

    private fun distance2D(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        return sqrt(dx * dx + dy * dy)
    }

    fun close() {
        poseLandmarker.close()
    }
}
