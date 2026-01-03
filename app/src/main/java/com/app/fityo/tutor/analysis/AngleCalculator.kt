package com.app.fityo.tutor.analysis

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Calcola angoli articolari dai landmark MediaPipe.
 * Utilizza geometria 2D per determinare angoli tra segmenti corporei.
 */
object AngleCalculator {

    /**
     * Rappresenta un punto 2D normalizzato (0-1).
     */
    data class Point2D(val x: Float, val y: Float)

    /**
     * Estrae un punto landmark come Point2D.
     * @param result Il risultato del PoseLandmarker
     * @param landmarkIndex L'indice del landmark (0-32)
     * @return Point2D o null se non disponibile
     */
    fun getLandmarkPoint(result: PoseLandmarkerResult, landmarkIndex: Int): Point2D? {
        if (result.landmarks().isEmpty()) return null
        val landmarks = result.landmarks()[0]
        if (landmarkIndex >= landmarks.size) return null

        val landmark = landmarks[landmarkIndex]
        return Point2D(landmark.x(), landmark.y())
    }

    /**
     * Calcola l'angolo formato da tre punti (A-B-C) al vertice B.
     *
     * @param a Primo punto
     * @param b Vertice (punto centrale)
     * @param c Terzo punto
     * @return Angolo in gradi (0-180)
     */
    fun calculateAngle(a: Point2D, b: Point2D, c: Point2D): Float {
        val radians = atan2(c.y - b.y, c.x - b.x) - atan2(a.y - b.y, a.x - b.x)
        var degrees = Math.toDegrees(radians.toDouble()).toFloat()
        degrees = abs(degrees)
        if (degrees > 180f) {
            degrees = 360f - degrees
        }
        return degrees
    }

    /**
     * Calcola l'angolo di un segmento rispetto alla verticale.
     * 0° = perfettamente verticale, 90° = orizzontale
     *
     * @param top Punto superiore
     * @param bottom Punto inferiore
     * @return Angolo in gradi dalla verticale
     */
    fun calculateAngleFromVertical(top: Point2D, bottom: Point2D): Float {
        val dx = bottom.x - top.x
        val dy = bottom.y - top.y
        val radians = atan2(dx, dy) // nota: dx, dy invece di dy, dx per angolo da verticale
        return abs(Math.toDegrees(radians.toDouble()).toFloat())
    }

    /**
     * Calcola l'angolo di inclinazione laterale del busto.
     *
     * @param result PoseLandmarkerResult
     * @return Angolo di inclinazione laterale (0 = dritto)
     */
    fun calculateTorsoLateralTilt(result: PoseLandmarkerResult): Float? {
        val leftShoulder = getLandmarkPoint(result, LandmarkIndex.LEFT_SHOULDER) ?: return null
        val rightShoulder = getLandmarkPoint(result, LandmarkIndex.RIGHT_SHOULDER) ?: return null

        val dy = rightShoulder.y - leftShoulder.y
        val dx = rightShoulder.x - leftShoulder.x

        return Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()
    }

    /**
     * Calcola l'angolo del ginocchio (flessione).
     *
     * @param result PoseLandmarkerResult
     * @param isLeft true per ginocchio sinistro, false per destro
     * @return Angolo del ginocchio in gradi (180 = gamba dritta, <90 = squat profondo)
     */
    fun calculateKneeAngle(result: PoseLandmarkerResult, isLeft: Boolean): Float? {
        val hipIndex = if (isLeft) LandmarkIndex.LEFT_HIP else LandmarkIndex.RIGHT_HIP
        val kneeIndex = if (isLeft) LandmarkIndex.LEFT_KNEE else LandmarkIndex.RIGHT_KNEE
        val ankleIndex = if (isLeft) LandmarkIndex.LEFT_ANKLE else LandmarkIndex.RIGHT_ANKLE

        val hip = getLandmarkPoint(result, hipIndex) ?: return null
        val knee = getLandmarkPoint(result, kneeIndex) ?: return null
        val ankle = getLandmarkPoint(result, ankleIndex) ?: return null

        return calculateAngle(hip, knee, ankle)
    }

    /**
     * Calcola l'angolo dell'anca (hip hinge).
     *
     * @param result PoseLandmarkerResult
     * @param isLeft true per anca sinistra, false per destra
     * @return Angolo dell'anca in gradi
     */
    fun calculateHipAngle(result: PoseLandmarkerResult, isLeft: Boolean): Float? {
        val shoulderIndex = if (isLeft) LandmarkIndex.LEFT_SHOULDER else LandmarkIndex.RIGHT_SHOULDER
        val hipIndex = if (isLeft) LandmarkIndex.LEFT_HIP else LandmarkIndex.RIGHT_HIP
        val kneeIndex = if (isLeft) LandmarkIndex.LEFT_KNEE else LandmarkIndex.RIGHT_KNEE

        val shoulder = getLandmarkPoint(result, shoulderIndex) ?: return null
        val hip = getLandmarkPoint(result, hipIndex) ?: return null
        val knee = getLandmarkPoint(result, kneeIndex) ?: return null

        return calculateAngle(shoulder, hip, knee)
    }

    /**
     * Calcola l'angolo della schiena rispetto alla verticale.
     * Usa la linea dalle anche alle spalle.
     *
     * @param result PoseLandmarkerResult
     * @return Angolo dalla verticale (0 = perfettamente dritto)
     */
    fun calculateBackAngle(result: PoseLandmarkerResult): Float? {
        val leftShoulder = getLandmarkPoint(result, LandmarkIndex.LEFT_SHOULDER) ?: return null
        val rightShoulder = getLandmarkPoint(result, LandmarkIndex.RIGHT_SHOULDER) ?: return null
        val leftHip = getLandmarkPoint(result, LandmarkIndex.LEFT_HIP) ?: return null
        val rightHip = getLandmarkPoint(result, LandmarkIndex.RIGHT_HIP) ?: return null

        // Punto medio spalle
        val midShoulder = Point2D(
            (leftShoulder.x + rightShoulder.x) / 2f,
            (leftShoulder.y + rightShoulder.y) / 2f
        )

        // Punto medio anche
        val midHip = Point2D(
            (leftHip.x + rightHip.x) / 2f,
            (leftHip.y + rightHip.y) / 2f
        )

        return calculateAngleFromVertical(midShoulder, midHip)
    }

    /**
     * Calcola la differenza di altezza tra due punti (asimmetria).
     *
     * @param result PoseLandmarkerResult
     * @param leftIndex Indice landmark sinistro
     * @param rightIndex Indice landmark destro
     * @return Differenza percentuale (positivo = sinistro più alto)
     */
    fun calculateHeightDifference(result: PoseLandmarkerResult, leftIndex: Int, rightIndex: Int): Float? {
        val left = getLandmarkPoint(result, leftIndex) ?: return null
        val right = getLandmarkPoint(result, rightIndex) ?: return null

        return (right.y - left.y) * 100f // Percentuale della dimensione normalizzata
    }

    /**
     * Verifica se i talloni sono sollevati.
     * Confronta la posizione Y del tallone con la punta del piede.
     *
     * @param result PoseLandmarkerResult
     * @param isLeft true per piede sinistro
     * @return true se il tallone è sollevato
     */
    fun isHeelRaised(result: PoseLandmarkerResult, isLeft: Boolean): Boolean {
        val heelIndex = if (isLeft) LandmarkIndex.LEFT_HEEL else LandmarkIndex.RIGHT_HEEL
        val footIndex = if (isLeft) LandmarkIndex.LEFT_FOOT_INDEX else LandmarkIndex.RIGHT_FOOT_INDEX

        val heel = getLandmarkPoint(result, heelIndex) ?: return false
        val foot = getLandmarkPoint(result, footIndex) ?: return false

        // Se il tallone è significativamente più alto della punta (Y più piccolo = più alto)
        return heel.y < foot.y - 0.02f // 2% di tolleranza
    }

    /**
     * Calcola la distanza orizzontale tra due punti.
     */
    fun calculateHorizontalDistance(p1: Point2D, p2: Point2D): Float {
        return abs(p2.x - p1.x)
    }

    /**
     * Calcola la distanza tra due punti.
     */
    fun calculateDistance(p1: Point2D, p2: Point2D): Float {
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        return sqrt(dx * dx + dy * dy)
    }

    /**
     * Indici dei landmark MediaPipe Pose.
     */
    object LandmarkIndex {
        const val NOSE = 0
        const val LEFT_EYE_INNER = 1
        const val LEFT_EYE = 2
        const val LEFT_EYE_OUTER = 3
        const val RIGHT_EYE_INNER = 4
        const val RIGHT_EYE = 5
        const val RIGHT_EYE_OUTER = 6
        const val LEFT_EAR = 7
        const val RIGHT_EAR = 8
        const val MOUTH_LEFT = 9
        const val MOUTH_RIGHT = 10
        const val LEFT_SHOULDER = 11
        const val RIGHT_SHOULDER = 12
        const val LEFT_ELBOW = 13
        const val RIGHT_ELBOW = 14
        const val LEFT_WRIST = 15
        const val RIGHT_WRIST = 16
        const val LEFT_PINKY = 17
        const val RIGHT_PINKY = 18
        const val LEFT_INDEX = 19
        const val RIGHT_INDEX = 20
        const val LEFT_THUMB = 21
        const val RIGHT_THUMB = 22
        const val LEFT_HIP = 23
        const val RIGHT_HIP = 24
        const val LEFT_KNEE = 25
        const val RIGHT_KNEE = 26
        const val LEFT_ANKLE = 27
        const val RIGHT_ANKLE = 28
        const val LEFT_HEEL = 29
        const val RIGHT_HEEL = 30
        const val LEFT_FOOT_INDEX = 31
        const val RIGHT_FOOT_INDEX = 32
    }
}
