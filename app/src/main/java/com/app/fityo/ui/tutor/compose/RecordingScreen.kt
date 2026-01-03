package com.app.fityo.ui.tutor.compose

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.app.fityo.dominio.ExerciseType
import com.app.fityo.tutor.analysis.TutorPoseLandmarkerHelper
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlinx.coroutines.delay
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun RecordingScreen(
    exerciseType: ExerciseType,
    onRecordingComplete: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isRecording by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableLongStateOf(0L) }
    var showSkeleton by remember { mutableStateOf(true) }
    var useFrontCamera by remember { mutableStateOf(false) }
    var currentRecording by remember { mutableStateOf<Recording?>(null) }
    var outputFilePath by remember { mutableStateOf<String?>(null) }

    // Pose detection state
    var poseResult by remember { mutableStateOf<PoseLandmarkerResult?>(null) }
    var isPoseValid by remember { mutableStateOf(false) }

    // Camera and recording setup
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { PreviewView(context) }
    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }

    // Pose landmarker for live preview
    val poseLandmarker = remember {
        try {
            TutorPoseLandmarkerHelper(
                context = context,
                runningMode = RunningMode.IMAGE,
                minPoseDetectionConfidence = 0.5f,
                minPosePresenceConfidence = 0.5f,
                minTrackingConfidence = 0.5f
            )
        } catch (e: Exception) {
            null
        }
    }

    // Timer for recording duration
    LaunchedEffect(isRecording) {
        if (isRecording) {
            val startTime = System.currentTimeMillis()
            while (isRecording) {
                recordingDuration = System.currentTimeMillis() - startTime
                delay(100)
            }
        } else {
            recordingDuration = 0L
        }
    }

    // Camera setup
    LaunchedEffect(useFrontCamera) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val recorder = Recorder.Builder()
                .setQualitySelector(
                    QualitySelector.from(
                        Quality.HD,
                        FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
                    )
                )
                .build()

            videoCapture = VideoCapture.withOutput(recorder)

            // Image analysis for pose detection (reduced resolution for performance)
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        if (showSkeleton) {
                            // Convert to bitmap and analyze
                            val bitmap = imageProxy.toBitmap()
                            val result = poseLandmarker?.detectImage(bitmap)
                            result?.let {
                                poseResult = it.result
                                isPoseValid = poseLandmarker?.hasValidPose(it.result) == true
                            }
                        }
                        imageProxy.close()
                    }
                }

            val cameraSelector = if (useFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    videoCapture,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Toast.makeText(context, "Errore camera: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(context))
    }

    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            poseLandmarker?.close()
            currentRecording?.stop()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera preview
        AndroidView(
            factory = {
                previewView.apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Skeleton overlay
        if (showSkeleton && poseResult != null) {
            SkeletonOverlay(
                poseResult = poseResult!!,
                isValid = isPoseValid,
                modifier = Modifier.fillMaxSize(),
                mirrorHorizontally = useFrontCamera,
                imageAspectRatio = 640f / 480f // Aspect ratio dell'analisi immagine
            )
        }

        // Top bar with back button and info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(
                onClick = {
                    currentRecording?.stop()
                    onBack()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(DarkCard.copy(alpha = 0.7f))
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Indietro",
                    tint = Color.White
                )
            }

            // Exercise info card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = DarkCard.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = exerciseType.displayName,
                        color = getExerciseColor(exerciseType),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Vista: ${exerciseType.recommendedView}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Toggle skeleton button
            IconButton(
                onClick = { showSkeleton = !showSkeleton },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(DarkCard.copy(alpha = 0.7f))
            ) {
                Icon(
                    if (showSkeleton) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Skeleton",
                    tint = if (showSkeleton) AccentGreen else TextSecondary
                )
            }
        }

        // Recording indicator
        if (isRecording) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(AccentRed.copy(alpha = 0.8f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatDuration(recordingDuration),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        // Pose status indicator
        if (!isRecording && showSkeleton) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isPoseValid) AccentGreen.copy(alpha = 0.8f)
                        else AccentOrange.copy(alpha = 0.8f)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isPoseValid) "Posizione OK" else "Posizionati meglio",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(DarkCard.copy(alpha = 0.8f))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tips
            if (!isRecording) {
                Text(
                    text = "Assicurati che il corpo sia completamente visibile",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Switch camera button
                if (!isRecording) {
                    IconButton(
                        onClick = { useFrontCamera = !useFrontCamera },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(DarkSurface)
                    ) {
                        Icon(
                            Icons.Default.Cameraswitch,
                            contentDescription = "Cambia Camera",
                            tint = TextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Record/Stop button
                FloatingActionButton(
                    onClick = {
                        if (isRecording) {
                            // Stop recording
                            currentRecording?.stop()
                        } else {
                            // Start recording
                            startRecording(
                                context = context,
                                videoCapture = videoCapture,
                                cameraExecutor = cameraExecutor,
                                exerciseType = exerciseType,
                                onRecordingStarted = { recording, path ->
                                    currentRecording = recording
                                    outputFilePath = path
                                    isRecording = true
                                },
                                onRecordingStopped = { success ->
                                    isRecording = false
                                    if (success && outputFilePath != null) {
                                        onRecordingComplete(outputFilePath!!)
                                    }
                                },
                                onError = { error ->
                                    isRecording = false
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    modifier = Modifier.size(72.dp),
                    containerColor = if (isRecording) AccentRed else AccentGreen,
                    contentColor = Color.White
                ) {
                    Icon(
                        if (isRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord,
                        contentDescription = if (isRecording) "Stop" else "Registra",
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Placeholder for symmetry
                if (!isRecording) {
                    Spacer(modifier = Modifier.size(56.dp))
                }
            }

            if (isRecording) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tocca per fermare la registrazione",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun SkeletonOverlay(
    poseResult: PoseLandmarkerResult,
    isValid: Boolean,
    modifier: Modifier = Modifier,
    mirrorHorizontally: Boolean = false,
    imageAspectRatio: Float = 640f / 480f // Aspect ratio dell'immagine analizzata
) {
    if (poseResult.landmarks().isEmpty()) return

    val landmarks = poseResult.landmarks()[0]
    val color = if (isValid) AccentGreen else AccentOrange

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val canvasAspectRatio = canvasWidth / canvasHeight

        // Funzione per convertire coordinate normalizzate in pixel
        // Gestisce FILL_CENTER (crop to fill) scaling
        fun landmarkToOffset(index: Int): Offset? {
            if (index >= landmarks.size) return null
            val landmark = landmarks[index]

            // Verifica visibilità
            val visibility = landmark.visibility()
            if (visibility.isPresent && visibility.get() < 0.5f) return null

            var normX = landmark.x()
            var normY = landmark.y()

            // Mirror per front camera (il preview è già mirrored, ma le coordinate no)
            if (mirrorHorizontally) {
                normX = 1f - normX
            }

            // Applica scaling e offset per FILL_CENTER
            val x: Float
            val y: Float

            if (canvasAspectRatio > imageAspectRatio) {
                // Canvas più largo dell'immagine -> immagine scalata per altezza, croppata ai lati
                val scaledWidth = canvasHeight * imageAspectRatio
                val offsetAmount = (scaledWidth - canvasWidth) / 2f
                x = normX * scaledWidth - offsetAmount
                y = normY * canvasHeight
            } else {
                // Canvas più alto dell'immagine -> immagine scalata per larghezza, croppata sopra/sotto
                val scaledHeight = canvasWidth / imageAspectRatio
                val offsetAmount = (scaledHeight - canvasHeight) / 2f
                x = normX * canvasWidth
                y = normY * scaledHeight - offsetAmount
            }

            return Offset(x, y)
        }

        // Connections between landmarks (simplified skeleton)
        val connections = listOf(
            // Torso
            Pair(11, 12), // shoulders
            Pair(11, 23), // left shoulder to hip
            Pair(12, 24), // right shoulder to hip
            Pair(23, 24), // hips
            // Left arm
            Pair(11, 13), Pair(13, 15),
            // Right arm
            Pair(12, 14), Pair(14, 16),
            // Left leg
            Pair(23, 25), Pair(25, 27),
            // Right leg
            Pair(24, 26), Pair(26, 28)
        )

        // Draw connections
        connections.forEach { (start, end) ->
            val startOffset = landmarkToOffset(start)
            val endOffset = landmarkToOffset(end)

            if (startOffset != null && endOffset != null) {
                drawLine(
                    color = color,
                    start = startOffset,
                    end = endOffset,
                    strokeWidth = 6f
                )
            }
        }

        // Draw landmark points
        val keyPoints = listOf(11, 12, 13, 14, 15, 16, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32)
        keyPoints.forEach { index ->
            val offset = landmarkToOffset(index) ?: return@forEach

            drawCircle(
                color = color,
                radius = 10f,
                center = offset
            )
            drawCircle(
                color = Color.White,
                radius = 6f,
                center = offset
            )
        }
    }
}

private fun startRecording(
    context: Context,
    videoCapture: VideoCapture<Recorder>?,
    cameraExecutor: ExecutorService,
    exerciseType: ExerciseType,
    onRecordingStarted: (Recording, String) -> Unit,
    onRecordingStopped: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    val vc = videoCapture ?: run {
        onError("Video capture non disponibile")
        return
    }

    val fileName = "tutor_${exerciseType.name}_${System.currentTimeMillis()}.mp4"

    // Use app-specific storage for better compatibility
    val outputFile = File(context.filesDir, fileName)
    val outputOptions = androidx.camera.video.FileOutputOptions.Builder(outputFile).build()

    // Check audio permission
    val hasAudioPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    try {
        val recording = if (hasAudioPermission) {
            vc.output
                .prepareRecording(context, outputOptions)
                .withAudioEnabled()
                .start(ContextCompat.getMainExecutor(context)) { event ->
                    handleRecordingEvent(event, outputFile.absolutePath, onRecordingStopped, onError)
                }
        } else {
            vc.output
                .prepareRecording(context, outputOptions)
                .start(ContextCompat.getMainExecutor(context)) { event ->
                    handleRecordingEvent(event, outputFile.absolutePath, onRecordingStopped, onError)
                }
        }

        onRecordingStarted(recording, outputFile.absolutePath)

    } catch (e: Exception) {
        onError("Errore avvio registrazione: ${e.message}")
    }
}

private fun handleRecordingEvent(
    event: VideoRecordEvent,
    outputPath: String,
    onRecordingStopped: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    when (event) {
        is VideoRecordEvent.Finalize -> {
            if (event.hasError()) {
                onError("Errore registrazione: ${event.error}")
                onRecordingStopped(false)
            } else {
                onRecordingStopped(true)
            }
        }
    }
}

private fun formatDuration(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = (ms / 1000) / 60
    return String.format("%02d:%02d", minutes, seconds)
}
