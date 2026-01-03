package com.app.fityo.avatar3d.processing

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import com.app.fityo.tutor.analysis.TutorPoseLandmarkerHelper
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.sqrt

/**
 * Processor per video 360° che estrae frame e analizza pose da multiple angolazioni.
 */
class Video360Processor(private val context: Context) {

    companion object {
        private const val TARGET_FRAMES = 30      // Frame da estrarre
        private const val MIN_VALID_FRAMES = 24   // Minimo 80% frame validi
        private const val CONFIDENCE_THRESHOLD = 0.5f
    }

    /**
     * Risultato dell'elaborazione video 360°.
     */
    data class ProcessingResult(
        val success: Boolean,
        val framesAnalyzed: Int,
        val validFrames: Int,
        val aggregatedMeasurements: AggregatedMeasurements?,
        val frameResults: List<FrameAnalysisResult>,
        val errorMessage: String? = null,
        val processingTimeMs: Long
    )

    /**
     * Risultato analisi di un singolo frame.
     */
    data class FrameAnalysisResult(
        val frameIndex: Int,
        val timestampMs: Long,
        val rotationDegrees: Float,
        val landmarks: List<NormalizedPoint>?,
        val isValid: Boolean
    )

    /**
     * Punto normalizzato 3D.
     */
    data class NormalizedPoint(
        val x: Float,
        val y: Float,
        val z: Float,
        val visibility: Float
    )

    /**
     * Misurazioni aggregate da tutte le viste.
     */
    data class AggregatedMeasurements(
        val shoulderWidthAvg: Float,
        val hipWidthAvg: Float,
        val torsoHeightAvg: Float,
        val armLengthAvg: Float,
        val legLengthAvg: Float,
        val shoulderToHipRatio: Float,
        val shoulderToWaistRatio: Float,
        val armToTorsoRatio: Float,
        val legToTorsoRatio: Float,
        val overallConfidence: Float
    )

    private var poseLandmarker: TutorPoseLandmarkerHelper? = null

    /**
     * Elabora un video 360° ed estrae le misurazioni del corpo.
     */
    suspend fun processVideo(
        videoPath: String,
        onProgress: (Float, String) -> Unit
    ): ProcessingResult = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()
        val frameResults = mutableListOf<FrameAnalysisResult>()

        try {
            // Inizializza pose landmarker
            onProgress(0.05f, "Inizializzazione analisi...")
            initializePoseLandmarker()

            // Estrai durata video
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoPath)
            val durationMs = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLongOrNull() ?: 0L

            if (durationMs < 5000) {
                return@withContext ProcessingResult(
                    success = false,
                    framesAnalyzed = 0,
                    validFrames = 0,
                    aggregatedMeasurements = null,
                    frameResults = emptyList(),
                    errorMessage = "Video troppo corto (minimo 5 secondi)",
                    processingTimeMs = System.currentTimeMillis() - startTime
                )
            }

            // Calcola timestamp per ogni frame
            val frameInterval = durationMs / TARGET_FRAMES
            val degreesPerFrame = 360f / TARGET_FRAMES

            // Estrai e analizza frame
            onProgress(0.1f, "Estrazione frame...")
            for (i in 0 until TARGET_FRAMES) {
                val timestampMs = i * frameInterval
                val rotationDegrees = i * degreesPerFrame

                val progress = 0.1f + (0.8f * i / TARGET_FRAMES)
                onProgress(progress, "Analisi frame ${i + 1}/$TARGET_FRAMES...")

                // Estrai frame
                val bitmap = retriever.getFrameAtTime(
                    timestampMs * 1000, // Converti in microsecondi
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )

                if (bitmap != null) {
                    // Analizza pose
                    val poseResult = poseLandmarker?.detectImage(bitmap)
                    val landmarks = extractLandmarks(poseResult?.result)
                    val isValid = landmarks != null && landmarks.size >= 33

                    frameResults.add(
                        FrameAnalysisResult(
                            frameIndex = i,
                            timestampMs = timestampMs,
                            rotationDegrees = rotationDegrees,
                            landmarks = landmarks,
                            isValid = isValid
                        )
                    )

                    bitmap.recycle()
                } else {
                    frameResults.add(
                        FrameAnalysisResult(
                            frameIndex = i,
                            timestampMs = timestampMs,
                            rotationDegrees = rotationDegrees,
                            landmarks = null,
                            isValid = false
                        )
                    )
                }
            }

            retriever.release()

            // Verifica frame validi
            val validFrames = frameResults.count { it.isValid }
            if (validFrames < MIN_VALID_FRAMES) {
                return@withContext ProcessingResult(
                    success = false,
                    framesAnalyzed = TARGET_FRAMES,
                    validFrames = validFrames,
                    aggregatedMeasurements = null,
                    frameResults = frameResults,
                    errorMessage = "Troppi frame non validi ($validFrames/$TARGET_FRAMES). Riprova con più luce.",
                    processingTimeMs = System.currentTimeMillis() - startTime
                )
            }

            // Aggrega misurazioni
            onProgress(0.95f, "Calcolo misurazioni finali...")
            val aggregated = aggregateMeasurements(frameResults.filter { it.isValid })

            val processingTime = System.currentTimeMillis() - startTime

            ProcessingResult(
                success = true,
                framesAnalyzed = TARGET_FRAMES,
                validFrames = validFrames,
                aggregatedMeasurements = aggregated,
                frameResults = frameResults,
                processingTimeMs = processingTime
            )

        } catch (e: Exception) {
            ProcessingResult(
                success = false,
                framesAnalyzed = frameResults.size,
                validFrames = frameResults.count { it.isValid },
                aggregatedMeasurements = null,
                frameResults = frameResults,
                errorMessage = "Errore elaborazione: ${e.message}",
                processingTimeMs = System.currentTimeMillis() - startTime
            )
        } finally {
            cleanup()
        }
    }

    private fun initializePoseLandmarker() {
        poseLandmarker = TutorPoseLandmarkerHelper(
            context = context,
            runningMode = RunningMode.IMAGE,
            minPoseDetectionConfidence = CONFIDENCE_THRESHOLD,
            minPosePresenceConfidence = CONFIDENCE_THRESHOLD,
            minTrackingConfidence = CONFIDENCE_THRESHOLD
        )
    }

    private fun extractLandmarks(result: PoseLandmarkerResult?): List<NormalizedPoint>? {
        if (result == null || result.landmarks().isEmpty()) return null

        val landmarks = result.landmarks()[0]
        return landmarks.map { landmark ->
            NormalizedPoint(
                x = landmark.x(),
                y = landmark.y(),
                z = landmark.z(),
                visibility = landmark.visibility().orElse(0f)
            )
        }
    }

    private fun aggregateMeasurements(validFrames: List<FrameAnalysisResult>): AggregatedMeasurements {
        // Raccogli misurazioni da ogni frame
        val shoulderWidths = mutableListOf<Float>()
        val hipWidths = mutableListOf<Float>()
        val torsoHeights = mutableListOf<Float>()
        val armLengths = mutableListOf<Float>()
        val legLengths = mutableListOf<Float>()
        val confidences = mutableListOf<Float>()

        for (frame in validFrames) {
            val landmarks = frame.landmarks ?: continue

            // Calcola misurazioni per questo frame
            val shoulderWidth = distance2D(landmarks[11], landmarks[12])
            val hipWidth = distance2D(landmarks[23], landmarks[24])
            val torsoHeight = distance2D(landmarks[11], landmarks[23])

            // Braccia (media sinistra + destra)
            val leftArm = distance2D(landmarks[11], landmarks[13]) +
                    distance2D(landmarks[13], landmarks[15])
            val rightArm = distance2D(landmarks[12], landmarks[14]) +
                    distance2D(landmarks[14], landmarks[16])
            val armLength = (leftArm + rightArm) / 2

            // Gambe (media sinistra + destra)
            val leftLeg = distance2D(landmarks[23], landmarks[25]) +
                    distance2D(landmarks[25], landmarks[27])
            val rightLeg = distance2D(landmarks[24], landmarks[26]) +
                    distance2D(landmarks[26], landmarks[28])
            val legLength = (leftLeg + rightLeg) / 2

            // Confidenza media dei landmark chiave
            val keyLandmarks = listOf(11, 12, 23, 24, 13, 14, 25, 26)
            val avgConfidence = keyLandmarks
                .filter { it < landmarks.size }
                .map { landmarks[it].visibility }
                .average()
                .toFloat()

            // Aggiungi solo se misurazioni valide
            if (shoulderWidth > 0.01f && hipWidth > 0.01f && torsoHeight > 0.01f) {
                shoulderWidths.add(shoulderWidth)
                hipWidths.add(hipWidth)
                torsoHeights.add(torsoHeight)
                armLengths.add(armLength)
                legLengths.add(legLength)
                confidences.add(avgConfidence)
            }
        }

        // Calcola medie (rimuovendo outlier)
        val shoulderWidthAvg = trimmedMean(shoulderWidths)
        val hipWidthAvg = trimmedMean(hipWidths)
        val torsoHeightAvg = trimmedMean(torsoHeights)
        val armLengthAvg = trimmedMean(armLengths)
        val legLengthAvg = trimmedMean(legLengths)
        val overallConfidence = trimmedMean(confidences)

        // Calcola rapporti
        val shoulderToHipRatio = if (hipWidthAvg > 0) shoulderWidthAvg / hipWidthAvg else 1f
        val shoulderToWaistRatio = if (hipWidthAvg > 0) shoulderWidthAvg / hipWidthAvg else 1f
        val armToTorsoRatio = if (torsoHeightAvg > 0) armLengthAvg / torsoHeightAvg else 0.5f
        val legToTorsoRatio = if (torsoHeightAvg > 0) legLengthAvg / torsoHeightAvg else 1f

        return AggregatedMeasurements(
            shoulderWidthAvg = shoulderWidthAvg,
            hipWidthAvg = hipWidthAvg,
            torsoHeightAvg = torsoHeightAvg,
            armLengthAvg = armLengthAvg,
            legLengthAvg = legLengthAvg,
            shoulderToHipRatio = shoulderToHipRatio,
            shoulderToWaistRatio = shoulderToWaistRatio,
            armToTorsoRatio = armToTorsoRatio,
            legToTorsoRatio = legToTorsoRatio,
            overallConfidence = overallConfidence
        )
    }

    private fun distance2D(p1: NormalizedPoint, p2: NormalizedPoint): Float {
        val dx = p1.x - p2.x
        val dy = p1.y - p2.y
        return sqrt(dx * dx + dy * dy)
    }

    /**
     * Calcola la media troncata (rimuove 10% valori estremi).
     */
    private fun trimmedMean(values: List<Float>, trimPercent: Float = 0.1f): Float {
        if (values.isEmpty()) return 0f
        if (values.size < 4) return values.average().toFloat()

        val sorted = values.sorted()
        val trimCount = (values.size * trimPercent).toInt().coerceAtLeast(1)
        val trimmed = sorted.subList(trimCount, sorted.size - trimCount)

        return if (trimmed.isNotEmpty()) {
            trimmed.average().toFloat()
        } else {
            values.average().toFloat()
        }
    }

    private fun cleanup() {
        poseLandmarker?.close()
        poseLandmarker = null
    }
}
