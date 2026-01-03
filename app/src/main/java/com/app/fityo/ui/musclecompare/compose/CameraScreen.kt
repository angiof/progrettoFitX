package com.app.fityo.ui.musclecompare.compose

import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    title: String,
    subtitle: String,
    ghostOverlay: Bitmap?,
    poseDetected: Boolean,
    alignmentPercent: Float,
    showAlignmentIndicator: Boolean,
    showPreview: Boolean = false,
    previewPath: String? = null,
    useFrontCamera: Boolean = false,
    liveLandmarks: PoseLandmarkerResult? = null,
    onCapture: (Bitmap) -> Unit,
    onGallerySelect: () -> Unit,
    onBack: () -> Unit,
    onPoseUpdate: (Bitmap) -> Unit,
    onProceed: (() -> Unit)? = null,
    onToggleCamera: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    var currentCameraSelector by remember(useFrontCamera) {
        mutableStateOf(
            if (useFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA
        )
    }
    var lastAnalysisTime by remember { mutableStateOf(0L) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Subtitle
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp)
            )

            // Camera preview o anteprima
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                if (showPreview && previewPath != null) {
                    // Mostra anteprima foto catturata
                    PreviewImage(previewPath)
                } else {
                    // Camera preview
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        update = { previewView ->
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()

                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }

                                imageCapture = ImageCapture.Builder()
                                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                    .build()

                                // Image analysis for live pose detection
                                val imageAnalysis = ImageAnalysis.Builder()
                                    .setTargetResolution(android.util.Size(640, 480))
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                    .also { analysis ->
                                        analysis.setAnalyzer(executor) { imageProxy ->
                                            val currentTime = System.currentTimeMillis()
                                            // Analyze every 200ms to avoid overload
                                            if (currentTime - lastAnalysisTime > 200) {
                                                lastAnalysisTime = currentTime
                                                val bitmap = imageProxy.toBitmapForAnalysis(
                                                    currentCameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA
                                                )
                                                if (bitmap != null) {
                                                    onPoseUpdate(bitmap)
                                                }
                                            }
                                            imageProxy.close()
                                        }
                                    }

                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        currentCameraSelector,
                                        preview,
                                        imageCapture,
                                        imageAnalysis
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }, ContextCompat.getMainExecutor(context))
                        }
                    )
                }

                // Ghost overlay
                if (ghostOverlay != null) {
                    Image(
                        bitmap = ghostOverlay.asImageBitmap(),
                        contentDescription = "Ghost overlay",
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.5f)
                    )
                }

                // Skeleton overlay - shows pose landmarks in real-time
                if (!showPreview) {
                    SkeletonOverlay(
                        landmarks = liveLandmarks,
                        modifier = Modifier.fillMaxSize(),
                        mirrorHorizontally = currentCameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA,
                        imageAspectRatio = 640f / 480f // Matching ImageAnalysis target resolution
                    )
                }

                // Griglia guida
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 1.dp.toPx()
                    val color = Color.White.copy(alpha = 0.3f)

                    // Linee verticali
                    drawLine(
                        color = color,
                        start = Offset(size.width / 3, 0f),
                        end = Offset(size.width / 3, size.height),
                        strokeWidth = strokeWidth
                    )
                    drawLine(
                        color = color,
                        start = Offset(size.width * 2 / 3, 0f),
                        end = Offset(size.width * 2 / 3, size.height),
                        strokeWidth = strokeWidth
                    )

                    // Linee orizzontali
                    drawLine(
                        color = color,
                        start = Offset(0f, size.height / 3),
                        end = Offset(size.width, size.height / 3),
                        strokeWidth = strokeWidth
                    )
                    drawLine(
                        color = color,
                        start = Offset(0f, size.height * 2 / 3),
                        end = Offset(size.width, size.height * 2 / 3),
                        strokeWidth = strokeWidth
                    )
                }

                // Indicatore pose
                PoseIndicator(
                    poseDetected = poseDetected,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }

            // Indicatore allineamento
            if (showAlignmentIndicator) {
                AlignmentIndicator(
                    percent = alignmentPercent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottoni
            if (showPreview && onProceed != null) {
                Button(
                    onClick = onProceed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGreen
                    )
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                    Text("  Procedi alla seconda foto", fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Galleria
                    IconButton(
                        onClick = onGallerySelect,
                        modifier = Modifier
                            .size(64.dp)
                            .background(DarkCard, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "Galleria",
                            tint = TextPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Scatto
                    IconButton(
                        onClick = {
                            imageCapture?.takePicture(
                                executor,
                                object : ImageCapture.OnImageCapturedCallback() {
                                    override fun onCaptureSuccess(image: ImageProxy) {
                                        val bitmap = image.toBitmap()
                                        onCapture(bitmap)
                                        image.close()
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        exception.printStackTrace()
                                    }
                                }
                            )
                        },
                        enabled = poseDetected || !showAlignmentIndicator,
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                if (poseDetected || !showAlignmentIndicator) AccentBlue else Color.Gray,
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Scatta",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    // Camera toggle
                    IconButton(
                        onClick = {
                            onToggleCamera?.invoke()
                            currentCameraSelector = if (currentCameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .background(DarkCard, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Cameraswitch,
                            contentDescription = "Cambia camera",
                            tint = TextPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
        }
    }
}

@Composable
private fun PreviewImage(path: String) {
    // Placeholder per l'anteprima dell'immagine
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCard),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Foto catturata con successo",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PoseIndicator(
    poseDetected: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (poseDetected) AccentGreen.copy(alpha = 0.8f) else AccentRed.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (poseDetected) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = if (poseDetected) " Pose OK" else " Pose non rilevata",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun AlignmentIndicator(
    percent: Float,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Allineamento",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Text(
                "${percent.toInt()}%",
                color = when {
                    percent >= 80 -> AccentGreen
                    percent >= 50 -> AccentOrange
                    else -> AccentRed
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = when {
                percent >= 80 -> AccentGreen
                percent >= 50 -> AccentOrange
                else -> AccentRed
            },
            trackColor = DarkCard
        )
        if (percent < 80) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Allinea la posa alla sagoma (min 80%)",
                color = TextSecondary,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Extension function to convert ImageProxy to Bitmap for analysis.
 * Handles rotation and mirroring for front camera.
 */
private fun ImageProxy.toBitmapForAnalysis(isFrontCamera: Boolean): Bitmap? {
    return try {
        val bitmap = this.toBitmap()
        val matrix = Matrix()

        // Apply rotation based on image rotation
        matrix.postRotate(imageInfo.rotationDegrees.toFloat())

        // Mirror horizontally for front camera
        if (isFrontCamera) {
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        }

        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } catch (e: Exception) {
        null
    }
}
