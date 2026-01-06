package com.app.fityo.tutor.analysis

import android.content.Context
import android.graphics.Bitmap
import com.app.fityo.biometrics.MoveNetPoseEstimator
import com.app.fityo.biometrics.PoseEstimate
import com.app.fityo.dominio.ExerciseError
import com.app.fityo.dominio.ExerciseType
import com.google.mediapipe.tasks.vision.core.RunningMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Classe principale per l'analisi video degli esercizi.
 * Coordina l'estrazione dei frame, il rilevamento pose e l'analisi degli errori.
 */
class VideoAnalyzer(private val context: Context) {

    private var poseLandmarker: TutorPoseLandmarkerHelper? = null
    private var frameExtractor: VideoFrameExtractor? = null
    private var moveNetPoseEstimator: MoveNetPoseEstimator? = null

    /**
     * Risultato dell'analisi completa di un video.
     */
    data class AnalysisResult(
        val exerciseType: ExerciseType,
        val errors: List<ExerciseError>,
        val score: Float,
        val durationMs: Long,
        val totalFrames: Int,
        val analyzedFrames: Int,
        val framesWithPose: Int
    )

    /**
     * Callback per il progresso dell'analisi.
     */
    interface AnalysisListener {
        fun onProgress(progress: Float, currentFrame: Int, totalFrames: Int)
        fun onComplete(result: AnalysisResult)
        fun onError(error: String)
    }

    /**
     * Inizializza gli helper necessari.
     */
    suspend fun initialize() = withContext(Dispatchers.Default) {
        try {
            poseLandmarker = TutorPoseLandmarkerHelper(
                context = context,
                runningMode = RunningMode.VIDEO,
                minPoseDetectionConfidence = 0.5f,
                minPosePresenceConfidence = 0.5f,
                minTrackingConfidence = 0.5f
            )
            frameExtractor = VideoFrameExtractor(context)
            try {
                moveNetPoseEstimator = MoveNetPoseEstimator(context)
            } catch (e: Exception) {
                android.util.Log.e("VideoAnalyzer", "MoveNet init failed: ${e.message}", e)
                moveNetPoseEstimator = null
            }
        } catch (e: Exception) {
            throw Exception("Inizializzazione fallita: ${e.message}")
        }
    }

    /**
     * Analizza un video per un tipo di esercizio specifico.
     *
     * @param videoPath Percorso del file video
     * @param exerciseType Tipo di esercizio da analizzare
     * @param listener Callback per progresso e risultati
     */
    suspend fun analyzeVideo(
        videoPath: String,
        exerciseType: ExerciseType,
        listener: AnalysisListener
    ) = withContext(Dispatchers.Default) {

        // Verifica inizializzazione
        val landmarker = poseLandmarker ?: run {
            listener.onError("Analizzatore non inizializzato")
            return@withContext
        }
        val extractor = frameExtractor ?: run {
            listener.onError("Estrattore frame non inizializzato")
            return@withContext
        }

        // Reset il landmarker per il nuovo video
        landmarker.reset()

        // Crea l'analizzatore specifico per l'esercizio
        val exerciseAnalyzer = ExerciseAnalyzerFactory.create(exerciseType)

        // Ottieni info video
        val videoInfo = extractor.getVideoInfo(videoPath)
        if (videoInfo == null) {
            withContext(Dispatchers.Main) {
                listener.onError("Impossibile leggere il video")
            }
            return@withContext
        }

        val allErrors = mutableListOf<ExerciseError>()
        val frameResults = mutableListOf<FrameAnalysisResult>()
        var analyzedFrames = 0
        var framesWithPose = 0

        // Estrai e analizza i frame
        extractor.extractFrames(
            videoPath = videoPath,
            frameRateTarget = 10, // 10 FPS per analisi
            listener = object : VideoFrameExtractor.ExtractionListener {
                override fun onProgress(currentFrame: Int, totalFrames: Int) {
                    // Progress già gestito in onFrameExtracted
                }

                override fun onFrameExtracted(frame: VideoFrameExtractor.ExtractedFrame) {
                    // Analizza il frame
                    if (!hasMoveNetPose(frame.bitmap)) {
                        analyzedFrames++
                        frame.bitmap.recycle()
                        return
                    }
                    val poseResult = landmarker.detectVideoFrame(frame.bitmap, frame.timestampMs)

                    if (poseResult != null && landmarker.hasValidPose(poseResult.result)) {
                        framesWithPose++

                        // Estrai metriche
                        val metrics = extractMetrics(poseResult.result)

                        // Analizza errori
                        val frameErrors = exerciseAnalyzer.analyzeFrame(
                            result = poseResult.result,
                            timestampMs = frame.timestampMs,
                            previousResults = frameResults
                        )

                        allErrors.addAll(frameErrors)

                        frameResults.add(
                            FrameAnalysisResult(
                                timestampMs = frame.timestampMs,
                                result = poseResult.result,
                                errors = frameErrors,
                                metrics = metrics
                            )
                        )
                    }

                    analyzedFrames++

                    // Libera memoria del bitmap
                    frame.bitmap.recycle()
                }

                override fun onComplete(frames: List<VideoFrameExtractor.ExtractedFrame>) {
                    // Finalizza l'analisi
                    val finalErrors = exerciseAnalyzer.finalizeAnalysis(allErrors)
                    val score = exerciseAnalyzer.calculateScore(finalErrors, analyzedFrames)

                    val result = AnalysisResult(
                        exerciseType = exerciseType,
                        errors = finalErrors,
                        score = score,
                        durationMs = videoInfo.durationMs,
                        totalFrames = frames.size,
                        analyzedFrames = analyzedFrames,
                        framesWithPose = framesWithPose
                    )

                    listener.onComplete(result)
                }

                override fun onError(error: String) {
                    listener.onError(error)
                }
            }
        )
    }

    /**
     * Analizza un video in modo sincrono (più semplice per l'uso con coroutines).
     */
    suspend fun analyzeVideoSync(
        videoPath: String,
        exerciseType: ExerciseType,
        onProgress: (Float, Int, Int) -> Unit
    ): AnalysisResult = withContext(Dispatchers.Default) {

        // Verifica inizializzazione
        val landmarker = poseLandmarker
            ?: throw Exception("Analizzatore non inizializzato")
        val extractor = frameExtractor
            ?: throw Exception("Estrattore frame non inizializzato")

        // Reset il landmarker
        landmarker.reset()

        // Crea l'analizzatore specifico
        val exerciseAnalyzer = ExerciseAnalyzerFactory.create(exerciseType)

        // Ottieni info video
        val videoInfo = extractor.getVideoInfo(videoPath)
            ?: throw Exception("Impossibile leggere il video")

        val allErrors = mutableListOf<ExerciseError>()
        val frameResults = mutableListOf<FrameAnalysisResult>()
        val poseResultsForValidation = mutableListOf<com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult>()
        var framesWithPose = 0

        // Calcola parametri estrazione
        val frameRateTarget = 10
        val intervalMs = (1000L / frameRateTarget).coerceAtLeast(100L)
        val totalFrames = (videoInfo.durationMs / intervalMs).toInt()

        var currentTimeMs = 0L
        var frameIndex = 0
        var validationDone = false
        var validationPassed = true

        val retriever = android.media.MediaMetadataRetriever()
        retriever.setDataSource(videoPath)

        try {
            while (currentTimeMs < videoInfo.durationMs) {
                // Estrai frame
                val bitmap = retriever.getFrameAtTime(
                    currentTimeMs * 1000,
                    android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )

                if (bitmap != null) {
                    if (!hasMoveNetPose(bitmap)) {
                        bitmap.recycle()
                        frameIndex++
                        currentTimeMs += intervalMs
                        val progress = frameIndex.toFloat() / totalFrames.coerceAtLeast(1)
                        withContext(Dispatchers.Main) {
                            onProgress(progress, frameIndex, totalFrames)
                        }
                        continue
                    }
                    // Analizza il frame
                    val poseResult = landmarker.detectVideoFrame(bitmap, currentTimeMs)

                    if (poseResult != null && landmarker.hasValidPose(poseResult.result)) {
                        framesWithPose++

                        // Raccogli risultati per validazione (primi 15 frame)
                        if (poseResultsForValidation.size < 15) {
                            poseResultsForValidation.add(poseResult.result)
                        }

                        // Esegui validazione dopo aver raccolto abbastanza frame
                        if (!validationDone && poseResultsForValidation.size >= 10) {
                            val validation = ExerciseValidator.validateExercise(
                                poseResultsForValidation,
                                exerciseType
                            )
                            validationDone = true
                            validationPassed = validation.isValid

                            // Se non valido, aggiungi errore di validazione
                            if (!validation.isValid) {
                                allErrors.add(
                                    ExerciseError(
                                        timestampMs = 0,
                                        endTimestampMs = videoInfo.durationMs,
                                        errorType = com.app.fityo.dominio.ExerciseErrorType.VIDEO_WRONG_EXERCISE,
                                        severity = com.app.fityo.dominio.ErrorSeverity.CRITICAL,
                                        message = validation.message,
                                        affectedLandmarks = emptyList(),
                                        correctionHint = "Assicurati di caricare un video del giusto esercizio o seleziona l'esercizio corretto."
                                    )
                                )
                            }
                        }

                        val metrics = extractMetrics(poseResult.result)

                        val frameErrors = exerciseAnalyzer.analyzeFrame(
                            result = poseResult.result,
                            timestampMs = currentTimeMs,
                            previousResults = frameResults
                        )

                        allErrors.addAll(frameErrors)

                        frameResults.add(
                            FrameAnalysisResult(
                                timestampMs = currentTimeMs,
                                result = poseResult.result,
                                errors = frameErrors,
                                metrics = metrics
                            )
                        )
                    }

                    bitmap.recycle()
                }

                frameIndex++
                currentTimeMs += intervalMs

                // Report progress
                val progress = frameIndex.toFloat() / totalFrames.coerceAtLeast(1)
                withContext(Dispatchers.Main) {
                    onProgress(progress, frameIndex, totalFrames)
                }
            }
        } finally {
            retriever.release()
        }

        // Finalizza analisi
        val finalErrors = exerciseAnalyzer.finalizeAnalysis(allErrors)

        // Calcola score (penalizza se esercizio sbagliato)
        val baseScore = exerciseAnalyzer.calculateScore(finalErrors, frameIndex)
        val score = if (!validationPassed) {
            (baseScore * 0.3f).coerceAtMost(30f) // Max 30% se esercizio sbagliato
        } else {
            baseScore
        }

        AnalysisResult(
            exerciseType = exerciseType,
            errors = finalErrors,
            score = score,
            durationMs = videoInfo.durationMs,
            totalFrames = totalFrames,
            analyzedFrames = frameIndex,
            framesWithPose = framesWithPose
        )
    }

    /**
     * Estrae metriche dal risultato pose.
     */
    private fun extractMetrics(result: com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult): FrameMetrics {
        return FrameMetrics(
            leftKneeAngle = AngleCalculator.calculateKneeAngle(result, isLeft = true),
            rightKneeAngle = AngleCalculator.calculateKneeAngle(result, isLeft = false),
            leftHipAngle = AngleCalculator.calculateHipAngle(result, isLeft = true),
            rightHipAngle = AngleCalculator.calculateHipAngle(result, isLeft = false),
            backAngle = AngleCalculator.calculateBackAngle(result),
            hipAsymmetry = AngleCalculator.calculateHeightDifference(
                result,
                AngleCalculator.LandmarkIndex.LEFT_HIP,
                AngleCalculator.LandmarkIndex.RIGHT_HIP
            ),
            leftHeelRaised = AngleCalculator.isHeelRaised(result, isLeft = true),
            rightHeelRaised = AngleCalculator.isHeelRaised(result, isLeft = false)
        )
    }

    /**
     * Rilascia le risorse.
     */
    fun close() {
        poseLandmarker?.close()
        poseLandmarker = null
        frameExtractor = null
        moveNetPoseEstimator?.close()
        moveNetPoseEstimator = null
    }

    private fun hasMoveNetPose(bitmap: Bitmap): Boolean {
        // Temporaneamente disabilitato MoveNet pre-filter per debug
        // TODO: Riattivare dopo aver risolto il problema di validazione
        val estimator = moveNetPoseEstimator ?: return true
        val pose = estimator.estimatePose(bitmap) ?: return true

        // Prima verifica: keypoints principali con score alto
        if (isPoseConfident(pose, MOVENET_REQUIRED, MOVENET_MIN_SCORE)) {
            return true
        }

        // Seconda verifica più permissiva: almeno alcuni punti visibili
        val presentPoints = pose.keypoints.count { it.score >= MOVENET_PRESENCE_SCORE }
        if (presentPoints >= MOVENET_MIN_POINTS) {
            return true
        }

        // Fallback: se MediaPipe è disponibile, lascia passare comunque
        // per permettere a MediaPipe di tentare l'analisi
        android.util.Log.w("VideoAnalyzer", "MoveNet low confidence ($presentPoints pts), allowing MediaPipe fallback")
        return true
    }

    private fun isPoseConfident(pose: PoseEstimate, indices: List<Int>, minScore: Float): Boolean {
        return indices.all { index ->
            pose.keypoints.getOrNull(index)?.score?.let { it >= minScore } == true
        }
    }

    companion object {
        private const val MOVENET_MIN_SCORE = 0.3f
        private const val MOVENET_PRESENCE_SCORE = 0.05f
        private const val MOVENET_MIN_POINTS = 3
        private val MOVENET_REQUIRED = listOf(5, 6, 11, 12)
    }
}
