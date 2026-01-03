package com.app.fityo.mediapipe

import android.graphics.Bitmap
import android.graphics.Matrix
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.sqrt

/**
 * Normalizza le foto per il confronto muscolare.
 * Usa la distanza tra le spalle come riferimento per la scala.
 */
class PhotoNormalizer {

    /**
     * Risultato della normalizzazione.
     */
    data class NormalizationResult(
        val normalizedBitmapA: Bitmap,
        val normalizedBitmapB: Bitmap,
        val scaleFactorA: Float,
        val scaleFactorB: Float
    )

    /**
     * Normalizza due foto basandosi sulla distanza tra le spalle.
     * La foto A viene usata come riferimento, la foto B viene scalata di conseguenza.
     */
    fun normalizePhotos(
        bitmapA: Bitmap,
        bitmapB: Bitmap,
        landmarksA: PoseLandmarkerResult,
        landmarksB: PoseLandmarkerResult
    ): NormalizationResult? {
        // Verifica che entrambe le foto abbiano landmarks validi
        if (landmarksA.landmarks().isEmpty() || landmarksB.landmarks().isEmpty()) {
            return null
        }

        val poseLandmarksA = landmarksA.landmarks()[0]
        val poseLandmarksB = landmarksB.landmarks()[0]

        // Calcola distanza spalle (punti 11 e 12) per entrambe le foto
        val shoulderDistA = calculateDistance(
            poseLandmarksA[LEFT_SHOULDER].x(),
            poseLandmarksA[LEFT_SHOULDER].y(),
            poseLandmarksA[RIGHT_SHOULDER].x(),
            poseLandmarksA[RIGHT_SHOULDER].y()
        )

        val shoulderDistB = calculateDistance(
            poseLandmarksB[LEFT_SHOULDER].x(),
            poseLandmarksB[LEFT_SHOULDER].y(),
            poseLandmarksB[RIGHT_SHOULDER].x(),
            poseLandmarksB[RIGHT_SHOULDER].y()
        )

        // Evita divisione per zero
        if (shoulderDistA == 0f || shoulderDistB == 0f) {
            return null
        }

        // Calcola il fattore di scala
        // Se B ha spalle più larghe (persona più vicina), scala verso il basso
        val scaleFactor = shoulderDistA / shoulderDistB

        // Scala la foto B per matchare le proporzioni della foto A
        val scaledBitmapB = if (scaleFactor != 1f) {
            scaleBitmap(bitmapB, scaleFactor)
        } else {
            bitmapB
        }

        return NormalizationResult(
            normalizedBitmapA = bitmapA,
            normalizedBitmapB = scaledBitmapB,
            scaleFactorA = 1f,
            scaleFactorB = scaleFactor
        )
    }

    /**
     * Scala una bitmap di un fattore specifico.
     */
    private fun scaleBitmap(bitmap: Bitmap, scaleFactor: Float): Bitmap {
        val matrix = Matrix().apply {
            postScale(scaleFactor, scaleFactor)
        }

        return Bitmap.createBitmap(
            bitmap,
            0, 0,
            bitmap.width, bitmap.height,
            matrix,
            true
        )
    }

    /**
     * Calcola la distanza euclidea tra due punti normalizzati (0-1).
     */
    private fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        return sqrt(dx * dx + dy * dy)
    }

    /**
     * Calcola l'overlap percentuale tra due pose.
     * Usato per verificare che l'utente sia allineato con il ghost overlay.
     */
    fun calculatePoseOverlap(
        landmarksA: PoseLandmarkerResult,
        landmarksB: PoseLandmarkerResult,
        threshold: Float = DEFAULT_OVERLAP_THRESHOLD
    ): Float {
        if (landmarksA.landmarks().isEmpty() || landmarksB.landmarks().isEmpty()) {
            return 0f
        }

        val poseLandmarksA = landmarksA.landmarks()[0]
        val poseLandmarksB = landmarksB.landmarks()[0]

        // Verifica punti chiave (spalle, anche, ginocchia)
        val keyPoints = listOf(
            LEFT_SHOULDER, RIGHT_SHOULDER,
            LEFT_HIP, RIGHT_HIP,
            LEFT_KNEE, RIGHT_KNEE
        )

        var matchingPoints = 0
        for (index in keyPoints) {
            val dist = calculateDistance(
                poseLandmarksA[index].x(),
                poseLandmarksA[index].y(),
                poseLandmarksB[index].x(),
                poseLandmarksB[index].y()
            )
            if (dist < threshold) {
                matchingPoints++
            }
        }

        return matchingPoints.toFloat() / keyPoints.size * 100f
    }

    /**
     * Verifica se l'allineamento è sufficiente per procedere.
     */
    fun isAlignmentSufficient(
        landmarksA: PoseLandmarkerResult,
        landmarksB: PoseLandmarkerResult,
        minOverlapPercent: Float = MIN_OVERLAP_PERCENT
    ): Boolean {
        return calculatePoseOverlap(landmarksA, landmarksB) >= minOverlapPercent
    }

    companion object {
        // Indici dei punti MediaPipe
        private const val LEFT_SHOULDER = 11
        private const val RIGHT_SHOULDER = 12
        private const val LEFT_HIP = 23
        private const val RIGHT_HIP = 24
        private const val LEFT_KNEE = 25
        private const val RIGHT_KNEE = 26

        // Soglia per considerare un punto "allineato" (in coordinate normalizzate 0-1)
        private const val DEFAULT_OVERLAP_THRESHOLD = 0.1f

        // Percentuale minima di overlap per procedere
        private const val MIN_OVERLAP_PERCENT = 80f
    }
}
