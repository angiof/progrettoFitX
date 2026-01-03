package com.app.fityo.mediapipe

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import com.app.fityo.dominio.CompareResult
import com.app.fityo.dominio.DistrictResult
import com.app.fityo.dominio.MuscleDistrict
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Analizza le differenze muscolari tra due foto.
 * Calcola le variazioni per ogni distretto muscolare.
 */
class MuscleAnalyzer {

    /**
     * Analizza le differenze tra due foto per tutti i distretti muscolari.
     */
    fun analyze(
        bitmapA: Bitmap,
        bitmapB: Bitmap,
        maskA: Bitmap?,
        maskB: Bitmap?,
        landmarksA: PoseLandmarkerResult,
        landmarksB: PoseLandmarkerResult,
        scaleFactorA: Float,
        scaleFactorB: Float
    ): CompareResult? {
        if (landmarksA.landmarks().isEmpty() || landmarksB.landmarks().isEmpty()) {
            return null
        }

        val poseLandmarksA = landmarksA.landmarks()[0]
        val poseLandmarksB = landmarksB.landmarks()[0]

        // Analizza ogni distretto
        val armsResult = analyzeDistrict(
            bitmapA, bitmapB, maskA, maskB,
            poseLandmarksA, poseLandmarksB,
            MuscleDistrict.ARMS, bitmapA.width, bitmapA.height
        )

        val absResult = analyzeDistrict(
            bitmapA, bitmapB, maskA, maskB,
            poseLandmarksA, poseLandmarksB,
            MuscleDistrict.ABS, bitmapA.width, bitmapA.height
        )

        val legsResult = analyzeDistrict(
            bitmapA, bitmapB, maskA, maskB,
            poseLandmarksA, poseLandmarksB,
            MuscleDistrict.LEGS, bitmapA.width, bitmapA.height
        )

        val glutesResult = analyzeDistrict(
            bitmapA, bitmapB, maskA, maskB,
            poseLandmarksA, poseLandmarksB,
            MuscleDistrict.GLUTES, bitmapA.width, bitmapA.height
        )

        return CompareResult(
            armsResult = armsResult,
            absResult = absResult,
            legsResult = legsResult,
            glutesResult = glutesResult,
            scaleFactorA = scaleFactorA,
            scaleFactorB = scaleFactorB
        )
    }

    /**
     * Analizza un singolo distretto muscolare.
     */
    private fun analyzeDistrict(
        bitmapA: Bitmap,
        bitmapB: Bitmap,
        maskA: Bitmap?,
        maskB: Bitmap?,
        landmarksA: List<com.google.mediapipe.tasks.components.containers.NormalizedLandmark>,
        landmarksB: List<com.google.mediapipe.tasks.components.containers.NormalizedLandmark>,
        district: MuscleDistrict,
        width: Int,
        height: Int
    ): DistrictResult {
        // Estrai le ROI (Region of Interest) per il distretto
        val roiA = extractROI(landmarksA, district, width, height)
        val roiB = extractROI(landmarksB, district, width, height)

        // Calcola i pixel del muscolo nella ROI
        val pixelsA = countMusclePixels(bitmapA, maskA, roiA)
        val pixelsB = countMusclePixels(bitmapB, maskB, roiB)

        // Calcola la variazione percentuale
        val variation = if (pixelsA > 0) {
            ((pixelsB - pixelsA).toFloat() / pixelsA) * 100f
        } else {
            0f
        }

        return DistrictResult(
            district = district,
            variationPercent = variation,
            pixelsA = pixelsA,
            pixelsB = pixelsB
        )
    }

    /**
     * Estrae la Region of Interest per un distretto muscolare.
     */
    private fun extractROI(
        landmarks: List<com.google.mediapipe.tasks.components.containers.NormalizedLandmark>,
        district: MuscleDistrict,
        width: Int,
        height: Int
    ): Rect {
        val points = district.landmarks.map { index ->
            val landmark = landmarks[index]
            Pair((landmark.x() * width).toInt(), (landmark.y() * height).toInt())
        }

        // Trova bounding box dei punti
        val minX = points.minOf { it.first }
        val maxX = points.maxOf { it.first }
        val minY = points.minOf { it.second }
        val maxY = points.maxOf { it.second }

        // Aggiungi padding
        val paddingX = ((maxX - minX) * ROI_PADDING_FACTOR).toInt()
        val paddingY = ((maxY - minY) * ROI_PADDING_FACTOR).toInt()

        return Rect(
            max(0, minX - paddingX),
            max(0, minY - paddingY),
            min(width, maxX + paddingX),
            min(height, maxY + paddingY)
        )
    }

    /**
     * Conta i pixel del muscolo (non trasparenti) nella ROI.
     */
    private fun countMusclePixels(
        bitmap: Bitmap,
        mask: Bitmap?,
        roi: Rect
    ): Int {
        var count = 0

        // Assicurati che la ROI sia dentro i bounds della bitmap
        val safeRoi = Rect(
            max(0, roi.left),
            max(0, roi.top),
            min(bitmap.width, roi.right),
            min(bitmap.height, roi.bottom)
        )

        if (safeRoi.isEmpty) return 0

        for (y in safeRoi.top until safeRoi.bottom) {
            for (x in safeRoi.left until safeRoi.right) {
                // Se abbiamo una maschera, usa quella per determinare se è muscolo
                if (mask != null) {
                    val maskX = (x.toFloat() / bitmap.width * mask.width).toInt()
                        .coerceIn(0, mask.width - 1)
                    val maskY = (y.toFloat() / bitmap.height * mask.height).toInt()
                        .coerceIn(0, mask.height - 1)

                    val maskPixel = mask.getPixel(maskX, maskY)
                    if (Color.alpha(maskPixel) > ALPHA_THRESHOLD) {
                        count++
                    }
                } else {
                    // Senza maschera, conta tutti i pixel non trasparenti
                    val pixel = bitmap.getPixel(x, y)
                    if (Color.alpha(pixel) > ALPHA_THRESHOLD) {
                        count++
                    }
                }
            }
        }

        return count
    }

    /**
     * Calcola il diametro trasversale per le braccia.
     * Usato per analisi più accurata di bicipiti/tricipiti.
     */
    fun calculateArmDiameter(
        bitmap: Bitmap,
        mask: Bitmap?,
        landmarks: List<com.google.mediapipe.tasks.components.containers.NormalizedLandmark>,
        width: Int,
        height: Int
    ): Int {
        // Trova il punto medio tra spalla e gomito
        val shoulderX = (landmarks[LEFT_SHOULDER].x() + landmarks[RIGHT_SHOULDER].x()) / 2 * width
        val shoulderY = (landmarks[LEFT_SHOULDER].y() + landmarks[RIGHT_SHOULDER].y()) / 2 * height
        val elbowX = (landmarks[LEFT_ELBOW].x() + landmarks[RIGHT_ELBOW].x()) / 2 * width
        val elbowY = (landmarks[LEFT_ELBOW].y() + landmarks[RIGHT_ELBOW].y()) / 2 * height

        val midX = ((shoulderX + elbowX) / 2).toInt()
        val midY = ((shoulderY + elbowY) / 2).toInt()

        // Conta i pixel lungo la linea orizzontale passante per il punto medio
        var diameter = 0
        for (x in 0 until width) {
            val isMuscle = if (mask != null) {
                val maskX = (x.toFloat() / bitmap.width * mask.width).toInt()
                    .coerceIn(0, mask.width - 1)
                val maskY = (midY.toFloat() / bitmap.height * mask.height).toInt()
                    .coerceIn(0, mask.height - 1)
                Color.alpha(mask.getPixel(maskX, maskY)) > ALPHA_THRESHOLD
            } else {
                Color.alpha(bitmap.getPixel(x, midY)) > ALPHA_THRESHOLD
            }

            if (isMuscle) {
                diameter++
            }
        }

        return diameter
    }

    companion object {
        private const val ROI_PADDING_FACTOR = 0.2f
        private const val ALPHA_THRESHOLD = 128

        // Indici dei punti MediaPipe
        private const val LEFT_SHOULDER = 11
        private const val RIGHT_SHOULDER = 12
        private const val LEFT_ELBOW = 13
        private const val RIGHT_ELBOW = 14
    }
}
