package com.app.fityo.tutor.analysis

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
 * Helper per MediaPipe Pose Landmarker ottimizzato per l'analisi video del Tutor.
 * Supporta sia RunningMode.IMAGE che RunningMode.VIDEO.
 */
class TutorPoseLandmarkerHelper(
    private val context: Context,
    private val runningMode: RunningMode = RunningMode.IMAGE,
    private val minPoseDetectionConfidence: Float = 0.5f,
    private val minPosePresenceConfidence: Float = 0.5f,
    private val minTrackingConfidence: Float = 0.5f,
    private val currentDelegate: Delegate = Delegate.CPU,
    private val listener: TutorLandmarkerListener? = null
) {
    private var poseLandmarker: PoseLandmarker? = null
    private var lastTimestampMs: Long = -1

    init {
        setupPoseLandmarker()
    }

    private fun setupPoseLandmarker() {
        try {
            val baseOptions = BaseOptions.builder()
                .setDelegate(currentDelegate)
                .setModelAssetPath(MODEL_POSE_LANDMARKER)
                .build()

            val optionsBuilder = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(runningMode)
                .setMinPoseDetectionConfidence(minPoseDetectionConfidence)
                .setMinPosePresenceConfidence(minPosePresenceConfidence)
                .setNumPoses(1)

            // Tracking confidence solo per VIDEO e LIVE_STREAM
            if (runningMode != RunningMode.IMAGE) {
                optionsBuilder.setMinTrackingConfidence(minTrackingConfidence)
            }

            // Result listener per LIVE_STREAM
            if (runningMode == RunningMode.LIVE_STREAM) {
                optionsBuilder.setResultListener { result, input ->
                    val finishTimeMs = SystemClock.uptimeMillis()
                    listener?.onResults(ResultBundle(result, finishTimeMs))
                }
                optionsBuilder.setErrorListener { error ->
                    listener?.onError("Pose detection error: ${error.message}")
                }
            }

            poseLandmarker = PoseLandmarker.createFromOptions(context, optionsBuilder.build())
        } catch (e: Exception) {
            listener?.onError("Pose landmarker initialization failed: ${e.message}")
        }
    }

    /**
     * Rileva i punti di riferimento in un'immagine statica.
     * Usare con RunningMode.IMAGE.
     */
    fun detectImage(bitmap: Bitmap): ResultBundle? {
        if (runningMode != RunningMode.IMAGE) {
            listener?.onError("detectImage richiede RunningMode.IMAGE")
            return null
        }

        val startTime = SystemClock.uptimeMillis()
        val mpImage = BitmapImageBuilder(bitmap).build()

        return try {
            val result = poseLandmarker?.detect(mpImage)
            val inferenceTime = SystemClock.uptimeMillis() - startTime

            result?.let {
                ResultBundle(it, inferenceTime)
            }
        } catch (e: Exception) {
            listener?.onError("Detection failed: ${e.message}")
            null
        }
    }

    /**
     * Rileva i punti di riferimento in un frame video con timestamp.
     * Usare con RunningMode.VIDEO.
     *
     * @param bitmap Il frame da analizzare
     * @param frameTimestampMs Il timestamp del frame in millisecondi
     * @return ResultBundle con il risultato o null in caso di errore
     */
    fun detectVideoFrame(bitmap: Bitmap, frameTimestampMs: Long): ResultBundle? {
        if (runningMode != RunningMode.VIDEO) {
            listener?.onError("detectVideoFrame richiede RunningMode.VIDEO")
            return null
        }

        // Verifica che i timestamp siano crescenti
        if (frameTimestampMs <= lastTimestampMs) {
            // Skip frame con timestamp non valido
            return null
        }
        lastTimestampMs = frameTimestampMs

        val startTime = SystemClock.uptimeMillis()
        val mpImage = BitmapImageBuilder(bitmap).build()

        return try {
            val result = poseLandmarker?.detectForVideo(mpImage, frameTimestampMs)
            val inferenceTime = SystemClock.uptimeMillis() - startTime

            result?.let {
                ResultBundle(it, inferenceTime)
            }
        } catch (e: Exception) {
            listener?.onError("Video detection failed: ${e.message}")
            null
        }
    }

    /**
     * Rileva in modalità live stream (asincrono).
     * Usare con RunningMode.LIVE_STREAM.
     */
    fun detectLiveStream(bitmap: Bitmap, frameTimestampMs: Long) {
        if (runningMode != RunningMode.LIVE_STREAM) {
            listener?.onError("detectLiveStream richiede RunningMode.LIVE_STREAM")
            return
        }

        if (frameTimestampMs <= lastTimestampMs) {
            return
        }
        lastTimestampMs = frameTimestampMs

        val mpImage = BitmapImageBuilder(bitmap).build()

        try {
            poseLandmarker?.detectAsync(mpImage, frameTimestampMs)
        } catch (e: Exception) {
            listener?.onError("Live stream detection failed: ${e.message}")
        }
    }

    /**
     * Verifica se sono stati rilevati punti sufficienti per l'analisi.
     */
    fun hasValidPose(result: PoseLandmarkerResult): Boolean {
        if (result.landmarks().isEmpty()) return false

        val landmarks = result.landmarks()[0]
        if (landmarks.size < 33) return false

        // Verifica che i punti chiave siano visibili
        val keyPoints = listOf(
            AngleCalculator.LandmarkIndex.LEFT_SHOULDER,
            AngleCalculator.LandmarkIndex.RIGHT_SHOULDER,
            AngleCalculator.LandmarkIndex.LEFT_HIP,
            AngleCalculator.LandmarkIndex.RIGHT_HIP,
            AngleCalculator.LandmarkIndex.LEFT_KNEE,
            AngleCalculator.LandmarkIndex.RIGHT_KNEE
        )

        return keyPoints.all { index ->
            val visibility = landmarks[index].visibility()
            visibility.isPresent && visibility.get() > MIN_VISIBILITY_THRESHOLD
        }
    }

    /**
     * Resetta il tracker (utile tra video diversi).
     */
    fun reset() {
        lastTimestampMs = -1
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

    interface TutorLandmarkerListener {
        fun onResults(resultBundle: ResultBundle)
        fun onError(error: String)
    }

    companion object {
        private const val MODEL_POSE_LANDMARKER = "pose_landmarker_full.task"
        private const val MIN_VISIBILITY_THRESHOLD = 0.5f
    }
}
