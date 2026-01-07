package com.app.fityo.trueclone.capture

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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Colori
private val DarkBackground = Color(0xFF0D0D0D)
private val DarkCard = Color(0xFF1A1A1A)
private val AccentBlue = Color(0xFF00B4D8)
private val AccentGreen = Color(0xFF00F5A0)
private val AccentRed = Color(0xFFFF6B6B)
private val TextPrimary = Color(0xFFE8E8E8)
private val TextSecondary = Color(0xFFA0A0A0)

private const val COUNTDOWN_SECONDS = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrueCloneCaptureScreen(
    title: String = "TrueClone 3D",
    onComplete: (List<CapturedPhoto>) -> Unit,
    onCancel: () -> Unit,
    onGallerySelect: (() -> Unit)? = null,
    galleryPhoto: Bitmap? = null,
    onGalleryPhotoConsumed: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val capturedPhotos = remember { mutableStateListOf<CapturedPhoto>() }
    var currentView by remember { mutableStateOf(PhotoView.FRONT) }
    var countdownSeconds by remember { mutableStateOf<Int?>(null) }
    var poseValid by remember { mutableStateOf(true) } // Sempre valido per semplicità
    var isCapturing by remember { mutableStateOf(false) }

    // Camera
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var useFrontCamera by remember { mutableStateOf(true) }
    var currentCameraSelector by remember(useFrontCamera) {
        mutableStateOf(
            if (useFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA
        )
    }
    val executor = remember { Executors.newSingleThreadExecutor() }

    // Funzione per scattare la foto
    fun triggerCapture() {
        val capture = imageCapture ?: run {
            countdownSeconds = null
            return
        }

        isCapturing = true
        countdownSeconds = null

        capture.takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    var bitmap = image.toBitmap()

                    // Se camera frontale, specchia l'immagine
                    if (currentCameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                        val matrix = Matrix().apply {
                            postRotate(image.imageInfo.rotationDegrees.toFloat())
                            postScale(-1f, 1f)
                        }
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    }

                    image.close()

                    val photo = CapturedPhoto(
                        view = currentView,
                        bitmap = bitmap,
                        isValid = true
                    )
                    capturedPhotos.add(photo)

                    // Controlla se completato
                    val nextView = PhotoCaptureGuide.getNextView(capturedPhotos)
                    if (nextView != null) {
                        currentView = nextView
                    }

                    isCapturing = false
                }

                override fun onError(exception: ImageCaptureException) {
                    android.util.Log.e("TrueCloneCapture", "Capture error", exception)
                    isCapturing = false
                    countdownSeconds = null
                }
            }
        )
    }

    // Handle gallery photo when received
    LaunchedEffect(galleryPhoto) {
        if (galleryPhoto != null) {
            val photo = CapturedPhoto(
                view = currentView,
                bitmap = galleryPhoto,
                isValid = true
            )
            capturedPhotos.add(photo)
            onGalleryPhotoConsumed?.invoke()

            val nextView = PhotoCaptureGuide.getNextView(capturedPhotos)
            if (nextView != null) {
                currentView = nextView
            }
        }
    }

    // Gestione countdown
    LaunchedEffect(countdownSeconds) {
        val remaining = countdownSeconds ?: return@LaunchedEffect
        if (remaining <= 0) return@LaunchedEffect

        delay(1000)
        if (remaining == 1) {
            triggerCapture()
        } else {
            countdownSeconds = remaining - 1
        }
    }

    // Controlla completamento
    LaunchedEffect(capturedPhotos.size) {
        if (PhotoCaptureGuide.isComplete(capturedPhotos)) {
            delay(500) // Breve delay per feedback visivo
            onComplete(capturedPhotos.toList())
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, "Indietro", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
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
            // Progress indicators (foto catturate)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                PhotoCaptureGuide.captureSequence.forEachIndexed { index, view ->
                    val isCaptured = capturedPhotos.any { it.view == view }
                    val isCurrent = view == currentView

                    PhotoProgressIndicator(
                        view = view,
                        isCaptured = isCaptured,
                        isCurrent = isCurrent
                    )

                    if (index < PhotoCaptureGuide.captureSequence.size - 1) {
                        Spacer(modifier = Modifier.width(24.dp))
                    }
                }
            }

            // Camera Preview
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
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

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    currentCameraSelector,
                                    preview,
                                    imageCapture
                                )
                            } catch (e: Exception) {
                                android.util.Log.e("TrueCloneCapture", "Camera bind failed", e)
                            }
                        }, ContextCompat.getMainExecutor(context))
                    }
                )

                // Overlay guida silhouette
                SilhouetteGuideOverlay(
                    view = currentView,
                    poseValid = poseValid,
                    modifier = Modifier.fillMaxSize()
                )

                // Countdown overlay grande
                if (countdownSeconds != null && countdownSeconds!! > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(AccentBlue.copy(alpha = 0.9f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = countdownSeconds.toString(),
                            color = Color.White,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Indicatore processing
                if (isCapturing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AccentBlue)
                    }
                }
            }

            // Istruzioni
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Foto ${currentView.displayName}",
                        color = AccentBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentView.instruction,
                        color = TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    if (countdownSeconds != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Scatto tra ${countdownSeconds} secondi...",
                            color = AccentBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Bottoni controllo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery picker button
                IconButton(
                    onClick = { onGallerySelect?.invoke() },
                    enabled = countdownSeconds == null && !isCapturing,
                    modifier = Modifier
                        .size(56.dp)
                        .background(DarkCard, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = "Galleria",
                        tint = TextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Pulsante cattura
                IconButton(
                    onClick = {
                        if (countdownSeconds == null && !isCapturing) {
                            countdownSeconds = COUNTDOWN_SECONDS
                        }
                    },
                    enabled = countdownSeconds == null && !isCapturing,
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            if (countdownSeconds == null && !isCapturing) AccentBlue
                            else Color.Gray.copy(alpha = 0.5f),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Scatta foto",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Toggle camera
                IconButton(
                    onClick = {
                        useFrontCamera = !useFrontCamera
                        currentCameraSelector = if (useFrontCamera) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }
                    },
                    enabled = countdownSeconds == null && !isCapturing,
                    modifier = Modifier
                        .size(56.dp)
                        .background(DarkCard, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Cameraswitch,
                        contentDescription = "Cambia camera",
                        tint = TextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PhotoProgressIndicator(
    view: PhotoView,
    isCaptured: Boolean,
    isCurrent: Boolean
) {
    val animatedSize by animateFloatAsState(
        targetValue = if (isCurrent) 1.2f else 1f,
        animationSpec = tween(300),
        label = "size"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size((48 * animatedSize).dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCaptured -> AccentGreen.copy(alpha = 0.2f)
                        isCurrent -> AccentBlue.copy(alpha = 0.2f)
                        else -> DarkCard
                    }
                )
                .border(
                    width = 2.dp,
                    color = when {
                        isCaptured -> AccentGreen
                        isCurrent -> AccentBlue
                        else -> TextSecondary.copy(alpha = 0.3f)
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCaptured) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Completato",
                    tint = AccentGreen,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    Icons.Default.Person,
                    contentDescription = view.displayName,
                    tint = if (isCurrent) AccentBlue else TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = view.displayName,
            color = when {
                isCaptured -> AccentGreen
                isCurrent -> AccentBlue
                else -> TextSecondary
            },
            fontSize = 11.sp,
            fontWeight = if (isCurrent) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun SilhouetteGuideOverlay(
    view: PhotoView,
    poseValid: Boolean,
    modifier: Modifier = Modifier
) {
    val guideColor = if (poseValid) AccentGreen.copy(alpha = 0.6f) else AccentBlue.copy(alpha = 0.4f)

    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val bodyHeight = size.height * 0.7f
        val bodyTop = size.height * 0.1f
        val bodyWidth = size.width * 0.35f

        // Disegna sagoma guida
        when (view) {
            PhotoView.FRONT, PhotoView.BACK -> {
                drawFrontSilhouette(centerX, bodyTop, bodyWidth, bodyHeight, guideColor)
            }
            PhotoView.SIDE -> {
                drawSideSilhouette(centerX, bodyTop, bodyWidth * 0.5f, bodyHeight, guideColor)
            }
        }

        // Linea centrale guida
        drawLine(
            color = guideColor.copy(alpha = 0.3f),
            start = Offset(centerX, 0f),
            end = Offset(centerX, size.height),
            strokeWidth = 1.dp.toPx(),
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                floatArrayOf(10f, 10f)
            )
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFrontSilhouette(
    centerX: Float,
    top: Float,
    width: Float,
    height: Float,
    color: Color
) {
    val path = Path().apply {
        val headRadius = width * 0.25f
        val headCenterY = top + headRadius
        val neckTop = headCenterY + headRadius
        val neckBottom = neckTop + height * 0.05f
        val neckWidth = width * 0.15f
        val shoulderY = neckBottom
        val shoulderWidth = width
        val waistY = shoulderY + height * 0.35f
        val waistWidth = width * 0.7f
        val hipY = waistY + height * 0.1f
        val hipWidth = width * 0.8f
        val legBottom = top + height

        moveTo(centerX - neckWidth, neckTop)
        lineTo(centerX - shoulderWidth / 2, shoulderY)
        lineTo(centerX - shoulderWidth / 2, shoulderY + height * 0.1f)
        lineTo(centerX - waistWidth / 2, waistY)
        lineTo(centerX - hipWidth / 2, hipY)
        lineTo(centerX - hipWidth / 2 * 0.4f, legBottom)
        lineTo(centerX, hipY + height * 0.1f)
        lineTo(centerX + hipWidth / 2 * 0.4f, legBottom)
        lineTo(centerX + hipWidth / 2, hipY)
        lineTo(centerX + waistWidth / 2, waistY)
        lineTo(centerX + shoulderWidth / 2, shoulderY + height * 0.1f)
        lineTo(centerX + shoulderWidth / 2, shoulderY)
        lineTo(centerX + neckWidth, neckTop)
        close()
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
    )

    val headRadius = width * 0.25f
    drawCircle(
        color = color,
        radius = headRadius,
        center = Offset(centerX, top + headRadius),
        style = Stroke(width = 3.dp.toPx())
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSideSilhouette(
    centerX: Float,
    top: Float,
    depth: Float,
    height: Float,
    color: Color
) {
    val path = Path().apply {
        val headRadius = depth * 0.4f
        val headCenterY = top + headRadius

        moveTo(centerX - depth * 0.3f, headCenterY + headRadius)
        lineTo(centerX - depth * 0.5f, headCenterY + headRadius + height * 0.05f)
        lineTo(centerX - depth * 0.6f, top + height * 0.3f)
        lineTo(centerX - depth * 0.5f, top + height * 0.5f)
        lineTo(centerX - depth * 0.6f, top + height * 0.6f)
        lineTo(centerX - depth * 0.4f, top + height)
        lineTo(centerX + depth * 0.3f, top + height)
        lineTo(centerX + depth * 0.5f, top + height * 0.6f)
        lineTo(centerX + depth * 0.6f, top + height * 0.5f)
        lineTo(centerX + depth * 0.7f, top + height * 0.35f)
        lineTo(centerX + depth * 0.5f, top + height * 0.2f)
        lineTo(centerX + depth * 0.3f, headCenterY + headRadius)
        close()
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
    )

    val headRadius = depth * 0.4f
    drawCircle(
        color = color,
        radius = headRadius,
        center = Offset(centerX, top + headRadius),
        style = Stroke(width = 3.dp.toPx())
    )
}
