package com.app.fityo.ui.musclecompare.compose

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Size
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size as ComposeSize
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.app.fityo.R
import com.app.fityo.tutor.analysis.TutorPoseLandmarkerHelper
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlinx.coroutines.delay
import java.io.File
import java.util.concurrent.Executors

private const val RECORDING_DURATION_MS = 12000L // 12 secondi per rotazione 360°
private const val COUNTDOWN_SECONDS = 3

/**
 * Screen per registrazione video 360° per Avatar 3D.
 * L'utente ruota su se stesso mentre registra per catturare tutte le angolazioni.
 */
@Composable
fun Video360RecordingScreen(
    onRecordingComplete: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Recording states
    var recordingState by remember { mutableStateOf<Recording360State>(Recording360State.Instructions) }
    var countdown by remember { mutableIntStateOf(COUNTDOWN_SECONDS) }
    var recordingProgress by remember { mutableFloatStateOf(0f) }
    var recordingDuration by remember { mutableLongStateOf(0L) }
    var currentRecording by remember { mutableStateOf<Recording?>(null) }
    var outputFilePath by remember { mutableStateOf<String?>(null) }

    // Pose detection state
    var poseResult by remember { mutableStateOf<PoseLandmarkerResult?>(null) }
    var isPoseValid by remember { mutableStateOf(false) }

    // Camera setup
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

    // Animated progress for smooth ring animation
    val animatedProgress by animateFloatAsState(
        targetValue = recordingProgress,
        animationSpec = tween(100),
        label = "progress"
    )

    // Countdown timer
    LaunchedEffect(recordingState) {
        if (recordingState is Recording360State.Countdown) {
            countdown = COUNTDOWN_SECONDS
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            // Start recording after countdown
            startVideo360Recording(
                context = context,
                videoCapture = videoCapture,
                onRecordingStarted = { recording, path ->
                    currentRecording = recording
                    outputFilePath = path
                    recordingState = Recording360State.Recording
                },
                onError = { error ->
                    recordingState = Recording360State.Instructions
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // Recording timer and auto-stop
    LaunchedEffect(recordingState) {
        if (recordingState is Recording360State.Recording) {
            val startTime = System.currentTimeMillis()
            while (recordingState is Recording360State.Recording) {
                recordingDuration = System.currentTimeMillis() - startTime
                recordingProgress = (recordingDuration.toFloat() / RECORDING_DURATION_MS).coerceIn(0f, 1f)

                // Auto-stop after duration
                if (recordingDuration >= RECORDING_DURATION_MS) {
                    currentRecording?.stop()
                    recordingState = Recording360State.Processing
                    delay(500) // Brief delay before callback
                    outputFilePath?.let { onRecordingComplete(it) }
                    break
                }
                delay(50)
            }
        } else {
            recordingDuration = 0L
            recordingProgress = 0f
        }
    }

    // Camera setup - always use front camera for self-recording
    LaunchedEffect(Unit) {
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

            // Image analysis for pose detection
            @Suppress("DEPRECATION")
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        val bitmap = imageProxy.toBitmap()
                        val result = poseLandmarker?.detectImage(bitmap)
                        result?.let {
                            poseResult = it.result
                            isPoseValid = poseLandmarker?.hasValidPose(it.result) == true
                        }
                        imageProxy.close()
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
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

        // Semi-transparent overlay during instructions
        if (recordingState is Recording360State.Instructions) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground.copy(alpha = 0.7f))
            )
        }

        // Skeleton overlay during recording
        if (recordingState is Recording360State.Recording && poseResult != null) {
            Skeleton360Overlay(
                poseResult = poseResult!!,
                isValid = isPoseValid,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top bar with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.button_back),
                    tint = Color.White
                )
            }

            // Title
            Text(
                text = stringResource(R.string.avatar_3d_title),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            // Placeholder for symmetry
            Spacer(modifier = Modifier.size(48.dp))
        }

        // Main content based on state
        when (val state = recordingState) {
            is Recording360State.Instructions -> {
                InstructionsOverlay(
                    isPoseValid = isPoseValid,
                    onStartRecording = {
                        recordingState = Recording360State.Countdown
                    }
                )
            }

            is Recording360State.Countdown -> {
                CountdownOverlay(countdown = countdown)
            }

            is Recording360State.Recording -> {
                RecordingOverlay(
                    progress = animatedProgress,
                    durationMs = recordingDuration,
                    onStopRecording = {
                        currentRecording?.stop()
                        outputFilePath?.let { onRecordingComplete(it) }
                    }
                )
            }

            is Recording360State.Processing -> {
                ProcessingOverlay()
            }
        }
    }
}

/**
 * Stati della registrazione 360°.
 */
private sealed class Recording360State {
    object Instructions : Recording360State()
    object Countdown : Recording360State()
    object Recording : Recording360State()
    object Processing : Recording360State()
}

@Composable
private fun InstructionsOverlay(
    isPoseValid: Boolean,
    onStartRecording: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 360 icon
        Canvas(modifier = Modifier.size(120.dp)) {
            val strokeWidth = 8.dp.toPx()
            drawArc(
                color = AccentBlue,
                startAngle = 0f,
                sweepAngle = 330f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = ComposeSize(size.width - strokeWidth, size.height - strokeWidth)
            )
            // Arrow at end
            val arrowSize = 16.dp.toPx()
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = (size.width - strokeWidth) / 2
            // Draw arrow pointing right at 330 degrees
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.avatar_360_instruction_title),
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                InstructionItem(
                    number = "1",
                    text = stringResource(R.string.avatar_360_instruction_step1)
                )
                Spacer(modifier = Modifier.height(12.dp))
                InstructionItem(
                    number = "2",
                    text = stringResource(R.string.avatar_360_instruction_step2)
                )
                Spacer(modifier = Modifier.height(12.dp))
                InstructionItem(
                    number = "3",
                    text = stringResource(R.string.avatar_360_instruction_step3)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Pose status
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isPoseValid) AccentGreen.copy(alpha = 0.2f)
                    else AccentOrange.copy(alpha = 0.2f)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (isPoseValid) AccentGreen else AccentOrange)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isPoseValid)
                    stringResource(R.string.avatar_pose_ready)
                else
                    stringResource(R.string.avatar_pose_not_ready),
                color = if (isPoseValid) AccentGreen else AccentOrange,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Start button
        Button(
            onClick = onStartRecording,
            enabled = isPoseValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentBlue,
                disabledContainerColor = DarkSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                Icons.Default.FiberManualRecord,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.avatar_start_recording),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun InstructionItem(number: String, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(AccentBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CountdownOverlay(countdown: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.avatar_get_ready),
                color = TextSecondary,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = countdown.toString(),
                color = AccentBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 96.sp
            )
        }
    }
}

@Composable
private fun RecordingOverlay(
    progress: Float,
    durationMs: Long,
    onStopRecording: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 360° progress ring at center
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(200.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 12.dp.toPx()
                // Background ring
                drawArc(
                    color = DarkSurface,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                    size = ComposeSize(size.width - strokeWidth, size.height - strokeWidth)
                )
                // Progress ring
                drawArc(
                    color = AccentBlue,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                    size = ComposeSize(size.width - strokeWidth, size.height - strokeWidth)
                )
            }

            // Rotation degree indicator
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${(progress * 360).toInt()}°",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp
                )
                Text(
                    text = stringResource(R.string.avatar_rotation),
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        // Recording indicator
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
                text = formatDuration(durationMs),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        // Instructions during recording
        Text(
            text = stringResource(R.string.avatar_rotate_slowly),
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
        )

        // Stop button
        FloatingActionButton(
            onClick = onStopRecording,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(72.dp),
            containerColor = AccentRed,
            contentColor = Color.White
        ) {
            Icon(
                Icons.Default.Stop,
                contentDescription = stringResource(R.string.button_stop),
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
private fun ProcessingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.avatar_processing),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun Skeleton360Overlay(
    poseResult: PoseLandmarkerResult,
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    if (poseResult.landmarks().isEmpty()) return

    val landmarks = poseResult.landmarks()[0]
    val color = if (isValid) AccentGreen else AccentOrange

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val imageAspectRatio = 640f / 480f
        val canvasAspectRatio = canvasWidth / canvasHeight

        fun landmarkToOffset(index: Int): Offset? {
            if (index >= landmarks.size) return null
            val landmark = landmarks[index]

            val visibility = landmark.visibility()
            if (visibility.isPresent && visibility.get() < 0.5f) return null

            // Mirror for front camera
            val normX = 1f - landmark.x()
            val normY = landmark.y()

            val x: Float
            val y: Float

            if (canvasAspectRatio > imageAspectRatio) {
                val scaledWidth = canvasHeight * imageAspectRatio
                val offsetAmount = (scaledWidth - canvasWidth) / 2f
                x = normX * scaledWidth - offsetAmount
                y = normY * canvasHeight
            } else {
                val scaledHeight = canvasWidth / imageAspectRatio
                val offsetAmount = (scaledHeight - canvasHeight) / 2f
                x = normX * canvasWidth
                y = normY * scaledHeight - offsetAmount
            }

            return Offset(x, y)
        }

        // Skeleton connections
        val connections = listOf(
            Pair(11, 12), Pair(11, 23), Pair(12, 24), Pair(23, 24),
            Pair(11, 13), Pair(13, 15), Pair(12, 14), Pair(14, 16),
            Pair(23, 25), Pair(25, 27), Pair(24, 26), Pair(26, 28)
        )

        connections.forEach { (start, end) ->
            val startOffset = landmarkToOffset(start)
            val endOffset = landmarkToOffset(end)

            if (startOffset != null && endOffset != null) {
                drawLine(
                    color = color.copy(alpha = 0.7f),
                    start = startOffset,
                    end = endOffset,
                    strokeWidth = 4f
                )
            }
        }

        // Landmark points
        val keyPoints = listOf(11, 12, 13, 14, 15, 16, 23, 24, 25, 26, 27, 28)
        keyPoints.forEach { index ->
            val offset = landmarkToOffset(index) ?: return@forEach
            drawCircle(color = color, radius = 8f, center = offset)
            drawCircle(color = Color.White, radius = 4f, center = offset)
        }
    }
}

private fun startVideo360Recording(
    context: Context,
    videoCapture: VideoCapture<Recorder>?,
    onRecordingStarted: (Recording, String) -> Unit,
    onError: (String) -> Unit
) {
    val vc = videoCapture ?: run {
        onError("Video capture non disponibile")
        return
    }

    val fileName = "avatar360_${System.currentTimeMillis()}.mp4"
    val outputFile = File(context.filesDir, fileName)
    val outputOptions = FileOutputOptions.Builder(outputFile).build()

    val hasAudioPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    try {
        val recording = vc.output
            .prepareRecording(context, outputOptions)
            .apply {
                if (hasAudioPermission) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(context)) { event ->
                when (event) {
                    is VideoRecordEvent.Finalize -> {
                        if (event.hasError()) {
                            onError("Errore registrazione: ${event.error}")
                        }
                    }
                }
            }

        onRecordingStarted(recording, outputFile.absolutePath)

    } catch (e: Exception) {
        onError("Errore avvio registrazione: ${e.message}")
    }
}

private fun formatDuration(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = (ms / 1000) / 60
    return String.format("%02d:%02d", minutes, seconds)
}
