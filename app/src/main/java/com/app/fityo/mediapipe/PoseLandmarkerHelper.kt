package com.app.fityo.mediapipe

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

/**
 * Helper per MediaPipe Pose Landmarker.
 * Rileva i 33 punti di riferimento del corpo umano in un'immagine.
 */
class PoseLandmarkerHelper(
    private val context: Context,
    private val minPoseDetectionConfidence: Float = 0.5f,
    private val minPosePresenceConfidence: Float = 0.5f,
    private val currentDelegate: Delegate = Delegate.CPU,
    private val poseLandmarkerListener: LandmarkerListener? = null
) {
    private var poseLandmarker: PoseLandmarker? = null

    init {
        setupPoseLandmarker()
    }

    private fun setupPoseLandmarker() {
        try {
            val baseOptions = BaseOptions.builder()
                .setDelegate(currentDelegate)
                .setModelAssetPath(MODEL_POSE_LANDMARKER)
                .build()

            val options = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.IMAGE)
                .setMinPoseDetectionConfidence(minPoseDetectionConfidence)
                .setMinPosePresenceConfidence(minPosePresenceConfidence)
                .setNumPoses(1)
                .build()

            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
        } catch (e: Exception) {
            poseLandmarkerListener?.onError("Pose landmarker initialization failed: ${e.message}")
        }
    }

    /**
     * Rileva i punti di riferimento in un'immagine statica.
     */
    fun detectImage(bitmap: Bitmap): ResultBundle? {
        val startTime = SystemClock.uptimeMillis()
        val mpImage = BitmapImageBuilder(bitmap).build()

        return try {
            val result = poseLandmarker?.detect(mpImage)
            val inferenceTime = SystemClock.uptimeMillis() - startTime

            result?.let {
                ResultBundle(it, inferenceTime)
            }
        } catch (e: Exception) {
            poseLandmarkerListener?.onError("Detection failed: ${e.message}")
            null
        }
    }

    /**
     * Verifica se sono stati rilevati punti sufficienti per l'analisi.
     */
    fun hasValidPose(result: PoseLandmarkerResult): Boolean {
        if (result.landmarks().isEmpty()) return false

        val landmarks = result.landmarks()[0]
        if (landmarks.size < 33) return false

        // Verifica che i punti chiave siano visibili (confidence > threshold)
        val keyPoints = listOf(11, 12, 23, 24) // Spalle e anche
        return keyPoints.all { index ->
            val visibility = landmarks[index].visibility()
            visibility.isPresent && visibility.get() > MIN_VISIBILITY_THRESHOLD
        }
    }

    /**
     * Calcola la distanza tra due punti di riferimento.
     */
    fun calculateDistance(
        result: PoseLandmarkerResult,
        point1Index: Int,
        point2Index: Int
    ): Float {
        if (result.landmarks().isEmpty()) return 0f

        val landmarks = result.landmarks()[0]
        val p1 = landmarks[point1Index]
        val p2 = landmarks[point2Index]

        val dx = p2.x() - p1.x()
        val dy = p2.y() - p1.y()

        return kotlin.math.sqrt(dx * dx + dy * dy)
    }

    fun close() {
        poseLandmarker?.close()
        poseLandmarker = null
    }

    /**
     * Bundle per i risultati del rilevamento.
     */
    data class ResultBundle(
        val result: PoseLandmarkerResult,
        val inferenceTime: Long
    )

    interface LandmarkerListener {
        fun onResults(resultBundle: ResultBundle)
        fun onError(error: String)
    }

    companion object {
        // Usa pose_landmarker_full.task per maggiore precisione
        // Alternative: pose_landmarker_lite.task (veloce) o pose_landmarker_heavy.task (massima precisione)
        private const val MODEL_POSE_LANDMARKER = "pose_landmarker_full.task"
        private const val MIN_VISIBILITY_THRESHOLD = 0.5f
    }
}
