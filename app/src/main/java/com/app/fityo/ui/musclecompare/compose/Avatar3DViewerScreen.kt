package com.app.fityo.ui.musclecompare.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.R
import com.app.fityo.avatar3d.processing.Video360Processor
import com.app.fityo.mediapipe.BodyIntelligenceAnalyzer
// TODO: Future 3D imports - uncomment when implementing SceneView
// import androidx.compose.ui.platform.LocalContext
// import androidx.compose.ui.viewinterop.AndroidView
// import io.github.sceneview.SceneView
// import io.github.sceneview.math.Position
// import io.github.sceneview.math.Rotation
// import io.github.sceneview.node.ModelNode
// import io.github.sceneview.rememberEngine
// import io.github.sceneview.rememberModelLoader
import kotlin.math.cos
import kotlin.math.sin

/**
 * Screen per visualizzare l'avatar 3D generato.
 * Permette rotazione/zoom con touch e toggle colori zone.
 */
@Composable
fun Avatar3DViewerScreen(
    measurements: Video360Processor.AggregatedMeasurements?,
    zoneAnalysis: List<BodyIntelligenceAnalyzer.BodyZoneAnalysis>?,
    isProcessing: Boolean = false,
    processingProgress: Float = 0f,
    processingStep: String = "",
    framesProcessed: Int = 0,
    totalFrames: Int = 30,
    validFrames: Int = 0,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    var showZoneColors by remember { mutableStateOf(true) }
    var showInfo by remember { mutableStateOf(false) }

    // Rotation angle for Canvas visualization
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var scale by remember { mutableFloatStateOf(1f) }

    // TODO: Future 3D implementation with SceneView
    // val hasGlbModel = remember { ... }
    // if (hasGlbModel) { Avatar3DSceneView(...) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Body visualization background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
        ) {
            if (!isProcessing && measurements != null) {
                // 2D Canvas body visualization with zone colors
                BodyVisualization(
                    measurements = measurements,
                    zoneAnalysis = zoneAnalysis,
                    showZoneColors = showZoneColors,
                    rotationAngle = rotationAngle,
                    scale = scale,
                    onGesture = { rotation, newScale ->
                        rotationAngle += rotation
                        scale = (scale * newScale).coerceIn(0.5f, 2f)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Placeholder gradient background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBackground)
                )
            }
        }

        // Processing overlay
        if (isProcessing) {
            ProcessingOverlay(
                progress = processingProgress,
                step = processingStep,
                framesProcessed = framesProcessed,
                totalFrames = totalFrames,
                validFrames = validFrames
            )
        }

        // Top bar
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
                onClick = onBack,
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkCard.copy(alpha = 0.7f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Default.ViewInAr,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.avatar_3d_title),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Info button
            IconButton(
                onClick = { showInfo = !showInfo },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(DarkCard.copy(alpha = 0.7f))
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Info",
                    tint = if (showInfo) AccentBlue else Color.White
                )
            }
        }

        // Info panel
        AnimatedVisibility(
            visible = showInfo && measurements != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 80.dp, end = 16.dp)
        ) {
            MeasurementsInfoCard(measurements = measurements!!)
        }

        // Bottom controls
        if (!isProcessing) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(DarkCard.copy(alpha = 0.9f))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Zone colors toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ColorLens,
                            contentDescription = null,
                            tint = if (showZoneColors) AccentGreen else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mostra zone muscolari",
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                    }

                    Switch(
                        checked = showZoneColors,
                        onCheckedChange = { showZoneColors = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AccentGreen,
                            checkedTrackColor = AccentGreen.copy(alpha = 0.5f),
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = DarkSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Gesture hint
                Text(
                    text = "Trascina per ruotare - Pizzica per zoom",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Save button
                FloatingActionButton(
                    onClick = onSave,
                    containerColor = AccentBlue,
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.Save,
                        contentDescription = "Salva Avatar",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProcessingOverlay(
    progress: Float,
    step: String,
    framesProcessed: Int = 0,
    totalFrames: Int = 30,
    validFrames: Int = 0
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Rotating 3D icon with progress
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(140.dp),
                    color = AccentBlue,
                    strokeWidth = 10.dp,
                    trackColor = DarkSurface,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ViewInAr,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Generazione Avatar 3D",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = step,
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Frame progress details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Frames analyzed
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$framesProcessed / $totalFrames",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Frame analizzati",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                // Valid frames
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$validFrames",
                        color = if (validFrames >= 24) AccentGreen else AccentOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Frame validi",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar for frames
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                        .background(DarkSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (totalFrames > 0) framesProcessed.toFloat() / totalFrames else 0f)
                            .height(8.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                            .background(AccentBlue)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "0°",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "${(framesProcessed.toFloat() / totalFrames * 360).toInt()}°",
                        color = AccentBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "360°",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MeasurementsInfoCard(
    measurements: Video360Processor.AggregatedMeasurements
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(200.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Proporzioni",
                color = AccentBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            MeasurementRow(
                label = "Spalle/Fianchi",
                value = String.format("%.2f", measurements.shoulderToHipRatio)
            )
            MeasurementRow(
                label = "Braccia/Torso",
                value = String.format("%.2f", measurements.armToTorsoRatio)
            )
            MeasurementRow(
                label = "Gambe/Torso",
                value = String.format("%.2f", measurements.legToTorsoRatio)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Confidenza",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "${(measurements.overallConfidence * 100).toInt()}%",
                    color = when {
                        measurements.overallConfidence >= 0.8f -> AccentGreen
                        measurements.overallConfidence >= 0.6f -> AccentYellow
                        else -> AccentOrange
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun MeasurementRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = TextPrimary,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
    }
}

/*
 * TODO: Future 3D implementation with SceneView
 * Uncomment when ready to use real 3D model rendering
 *
 * @Composable
 * private fun Avatar3DSceneView(
 *     measurements: Video360Processor.AggregatedMeasurements,
 *     zoneAnalysis: List<BodyIntelligenceAnalyzer.BodyZoneAnalysis>?,
 *     showZoneColors: Boolean,
 *     onModelLoaded: () -> Unit,
 *     modifier: Modifier = Modifier
 * ) {
 *     val context = LocalContext.current
 *     var modelNode by remember { mutableStateOf<ModelNode?>(null) }
 *
 *     AndroidView(
 *         factory = { ctx ->
 *             SceneView(ctx).apply {
 *                 cameraNode.position = Position(z = 3.5f, y = 0.5f)
 *                 try {
 *                     val modelLoader = modelLoader
 *                     modelNode = ModelNode(
 *                         modelInstance = modelLoader.createModelInstance(
 *                             assetFileLocation = "models/body_base.glb"
 *                         ),
 *                         scaleToUnits = 2.0f
 *                     ).apply {
 *                         position = Position(y = -0.8f)
 *                         rotation = Rotation(y = 0f)
 *                     }
 *                     modelNode?.let { addChildNode(it) }
 *                     onModelLoaded()
 *                 } catch (e: Exception) {
 *                     android.util.Log.e("Avatar3D", "Failed to load 3D model", e)
 *                 }
 *             }
 *         },
 *         modifier = modifier
 *     )
 * }
 */

/**
 * Visualizzazione moderna del corpo con Canvas.
 * Mostra silhouette con zone colorate, dati e legenda.
 */
@Composable
private fun BodyVisualization(
    measurements: Video360Processor.AggregatedMeasurements,
    zoneAnalysis: List<BodyIntelligenceAnalyzer.BodyZoneAnalysis>?,
    showZoneColors: Boolean,
    rotationAngle: Float,
    scale: Float,
    onGesture: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // Map zone analysis to data
    val zoneDataMap: Map<String, Pair<Color, Float>> = remember(zoneAnalysis, showZoneColors) {
        if (showZoneColors && zoneAnalysis != null) {
            zoneAnalysis.associate { zoneData ->
                zoneData.zone.displayName to Pair(
                    getZoneColor(zoneData.percentageScore / 100f),
                    zoneData.percentageScore
                )
            }
        } else {
            emptyMap()
        }
    }

    Column(modifier = modifier) {
        // Body Canvas - takes most space
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        onGesture(pan.x * 0.5f, zoom)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width / 2
                val centerY = size.height * 0.45f
                val baseScale = minOf(size.width * 0.8f, size.height * 0.7f) * 0.4f * scale

                drawModernBody(
                    centerX = centerX,
                    centerY = centerY,
                    baseScale = baseScale,
                    measurements = measurements,
                    zoneDataMap = zoneDataMap,
                    rotationAngle = rotationAngle
                )
            }
        }

        // Zone cards at bottom
        if (zoneAnalysis != null && zoneAnalysis.isNotEmpty()) {
            ZoneCardsRow(
                zoneAnalysis = zoneAnalysis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

/**
 * Row of zone cards showing score for each body part.
 */
@Composable
private fun ZoneCardsRow(
    zoneAnalysis: List<BodyIntelligenceAnalyzer.BodyZoneAnalysis>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // First row - upper body
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            zoneAnalysis.take(4).forEach { zone ->
                ZoneScoreCard(
                    zoneName = zone.zone.displayName,
                    score = zone.percentageScore,
                    evaluation = zone.evaluation.name,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Second row - lower body
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            zoneAnalysis.drop(4).forEach { zone ->
                ZoneScoreCard(
                    zoneName = zone.zone.displayName,
                    score = zone.percentageScore,
                    evaluation = zone.evaluation.name,
                    modifier = Modifier.weight(1f)
                )
            }
            // Fill remaining space if odd number
            if (zoneAnalysis.size > 4 && (zoneAnalysis.size - 4) < 4) {
                repeat(4 - (zoneAnalysis.size - 4)) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ZoneScoreCard(
    zoneName: String,
    score: Float,
    evaluation: String,
    modifier: Modifier = Modifier
) {
    val color = getZoneColor(score / 100f)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Zone name
            Text(
                text = zoneName,
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Score with colored background
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${score.toInt()}",
                    color = color,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(DarkSurface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(score / 100f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color)
                )
            }
        }
    }
}

/**
 * Draws modern body silhouette with glow effects and zone colors.
 */
private fun DrawScope.drawModernBody(
    centerX: Float,
    centerY: Float,
    baseScale: Float,
    measurements: Video360Processor.AggregatedMeasurements,
    zoneDataMap: Map<String, Pair<Color, Float>>,
    rotationAngle: Float
) {
    // Calculate proportions
    val shoulderWidth = baseScale * 0.85f * measurements.shoulderToHipRatio.coerceIn(0.9f, 1.3f)
    val hipWidth = baseScale * 0.55f
    val torsoHeight = baseScale * 0.75f
    val armLength = baseScale * 0.9f * measurements.armToTorsoRatio.coerceIn(0.7f, 1.1f)
    val legLength = baseScale * 1.1f * measurements.legToTorsoRatio.coerceIn(0.9f, 1.2f)
    val headRadius = baseScale * 0.2f

    // Rotation effect
    val rotFactor = cos(Math.toRadians(rotationAngle.toDouble())).toFloat().coerceIn(0.4f, 1f)

    // Default body color (skin tone)
    val defaultColor = Color(0xFF8B7355)

    // Get zone colors
    fun zoneColor(name: String) = zoneDataMap[name]?.first ?: defaultColor

    rotate(rotationAngle * 0.08f, pivot = Offset(centerX, centerY)) {

        // === GLOW EFFECT (outer) ===
        val glowColor = AccentBlue.copy(alpha = 0.15f)

        // === HEAD ===
        val headY = centerY - torsoHeight * 0.55f - headRadius
        // Glow
        drawCircle(color = glowColor, radius = headRadius * 1.3f, center = Offset(centerX, headY))
        // Head
        drawCircle(color = defaultColor, radius = headRadius, center = Offset(centerX, headY))
        // Highlight
        drawCircle(
            color = Color.White.copy(alpha = 0.15f),
            radius = headRadius * 0.6f,
            center = Offset(centerX - headRadius * 0.2f, headY - headRadius * 0.2f)
        )

        // === NECK ===
        val neckWidth = baseScale * 0.14f
        val neckTop = headY + headRadius * 0.9f
        val neckBottom = centerY - torsoHeight * 0.4f
        drawRoundRect(
            color = defaultColor,
            topLeft = Offset(centerX - neckWidth / 2, neckTop),
            size = Size(neckWidth, neckBottom - neckTop),
            cornerRadius = CornerRadius(neckWidth / 3)
        )

        // === SHOULDERS / CHEST ===
        val chestTop = neckBottom - baseScale * 0.05f
        val chestBottom = centerY - torsoHeight * 0.05f
        val chestColor = zoneColor("Petto")
        val shouldersColor = zoneColor("Spalle")

        // Shoulders trapezoid with gradient effect
        val shoulderPath = Path().apply {
            moveTo(centerX - neckWidth * 0.6f, chestTop)
            lineTo(centerX - shoulderWidth * 0.52f * rotFactor, chestTop + baseScale * 0.12f)
            lineTo(centerX - shoulderWidth * 0.45f * rotFactor, chestBottom)
            lineTo(centerX + shoulderWidth * 0.45f * rotFactor, chestBottom)
            lineTo(centerX + shoulderWidth * 0.52f * rotFactor, chestTop + baseScale * 0.12f)
            lineTo(centerX + neckWidth * 0.6f, chestTop)
            close()
        }
        // Glow
        drawPath(shoulderPath, glowColor)
        // Fill with chest color
        drawPath(shoulderPath, chestColor)
        // Shoulder accents
        drawCircle(
            color = shouldersColor,
            radius = baseScale * 0.08f,
            center = Offset(centerX - shoulderWidth * 0.48f * rotFactor, chestTop + baseScale * 0.1f)
        )
        drawCircle(
            color = shouldersColor,
            radius = baseScale * 0.08f,
            center = Offset(centerX + shoulderWidth * 0.48f * rotFactor, chestTop + baseScale * 0.1f)
        )

        // === CORE / ABDOMEN ===
        val coreTop = chestBottom - baseScale * 0.02f
        val coreBottom = centerY + torsoHeight * 0.18f
        val coreColor = zoneColor("Core/Addome")
        val waistColor = zoneColor("Vita")

        // Tapered core shape
        val corePath = Path().apply {
            moveTo(centerX - shoulderWidth * 0.42f * rotFactor, coreTop)
            lineTo(centerX - hipWidth * 0.48f * rotFactor, coreBottom)
            lineTo(centerX + hipWidth * 0.48f * rotFactor, coreBottom)
            lineTo(centerX + shoulderWidth * 0.42f * rotFactor, coreTop)
            close()
        }
        drawPath(corePath, coreColor)

        // Waist line indicator
        val waistY = (coreTop + coreBottom) / 2
        drawLine(
            color = waistColor.copy(alpha = 0.6f),
            start = Offset(centerX - hipWidth * 0.35f * rotFactor, waistY),
            end = Offset(centerX + hipWidth * 0.35f * rotFactor, waistY),
            strokeWidth = 3f
        )

        // === HIPS ===
        val hipsTop = coreBottom - baseScale * 0.02f
        val hipsBottom = centerY + torsoHeight * 0.38f
        val hipsColor = zoneColor("Fianchi")

        drawRoundRect(
            color = hipsColor,
            topLeft = Offset(centerX - hipWidth * 0.52f * rotFactor, hipsTop),
            size = Size(hipWidth * 1.04f * rotFactor, hipsBottom - hipsTop),
            cornerRadius = CornerRadius(baseScale * 0.1f)
        )

        // === ARMS ===
        val armWidth = baseScale * 0.11f
        val armColor = zoneColor("Braccia")
        val armStartY = chestTop + baseScale * 0.12f

        // Left arm
        drawModernArm(
            startX = centerX - shoulderWidth * 0.52f * rotFactor,
            startY = armStartY,
            length = armLength,
            width = armWidth,
            angle = -12f,
            color = armColor,
            isLeft = true
        )

        // Right arm
        drawModernArm(
            startX = centerX + shoulderWidth * 0.52f * rotFactor,
            startY = armStartY,
            length = armLength,
            width = armWidth,
            angle = 12f,
            color = armColor,
            isLeft = false
        )

        // === LEGS ===
        val legWidth = baseScale * 0.15f
        val thighColor = zoneColor("Cosce")
        val calfColor = zoneColor("Polpacci")
        val legStartY = hipsBottom - baseScale * 0.02f

        // Left leg
        drawModernLeg(
            startX = centerX - hipWidth * 0.26f * rotFactor,
            startY = legStartY,
            length = legLength,
            width = legWidth * rotFactor,
            thighColor = thighColor,
            calfColor = calfColor
        )

        // Right leg
        drawModernLeg(
            startX = centerX + hipWidth * 0.26f * rotFactor,
            startY = legStartY,
            length = legLength,
            width = legWidth * rotFactor,
            thighColor = thighColor,
            calfColor = calfColor
        )
    }
}

private fun DrawScope.drawModernArm(
    startX: Float,
    startY: Float,
    length: Float,
    width: Float,
    angle: Float,
    color: Color,
    isLeft: Boolean
) {
    val rad = Math.toRadians(angle.toDouble() + 90)
    val upperLen = length * 0.52f
    val lowerLen = length * 0.48f

    val elbowX = startX + (upperLen * cos(rad)).toFloat() * if (isLeft) 1f else 1f
    val elbowY = startY + (upperLen * sin(rad)).toFloat()

    // Upper arm
    drawRoundRect(
        color = color,
        topLeft = Offset(
            if (isLeft) startX - width else startX,
            startY
        ),
        size = Size(width, upperLen),
        cornerRadius = CornerRadius(width / 2)
    )

    // Forearm (slightly offset and thinner)
    val forearmWidth = width * 0.85f
    drawRoundRect(
        color = color.copy(alpha = 0.9f),
        topLeft = Offset(
            if (isLeft) elbowX - forearmWidth * 0.8f else elbowX - forearmWidth * 0.2f,
            elbowY - width * 0.2f
        ),
        size = Size(forearmWidth, lowerLen),
        cornerRadius = CornerRadius(forearmWidth / 2)
    )

    // Elbow joint
    drawCircle(
        color = color.copy(alpha = 0.8f),
        radius = width * 0.4f,
        center = Offset(if (isLeft) elbowX - width * 0.3f else elbowX + width * 0.3f, elbowY)
    )
}

private fun DrawScope.drawModernLeg(
    startX: Float,
    startY: Float,
    length: Float,
    width: Float,
    thighColor: Color,
    calfColor: Color
) {
    val thighLen = length * 0.5f
    val calfLen = length * 0.5f
    val kneeY = startY + thighLen

    // Thigh
    drawRoundRect(
        color = thighColor,
        topLeft = Offset(startX - width / 2, startY),
        size = Size(width, thighLen + width * 0.2f),
        cornerRadius = CornerRadius(width / 3)
    )

    // Knee joint
    drawCircle(
        color = thighColor.copy(alpha = 0.7f),
        radius = width * 0.35f,
        center = Offset(startX, kneeY)
    )

    // Calf (slightly thinner)
    val calfWidth = width * 0.85f
    drawRoundRect(
        color = calfColor,
        topLeft = Offset(startX - calfWidth / 2, kneeY - width * 0.1f),
        size = Size(calfWidth, calfLen),
        cornerRadius = CornerRadius(calfWidth / 3)
    )

    // Foot hint
    drawRoundRect(
        color = calfColor.copy(alpha = 0.8f),
        topLeft = Offset(startX - calfWidth * 0.4f, startY + length - width * 0.3f),
        size = Size(calfWidth * 0.9f, width * 0.4f),
        cornerRadius = CornerRadius(width * 0.15f)
    )
}

/**
 * Returns color based on zone score.
 * Higher score = more green (developed)
 * Lower score = more red/orange (needs work)
 */
private fun getZoneColor(score: Float): Color {
    return when {
        score >= 0.8f -> Color(0xFF4CAF50) // Green - excellent
        score >= 0.6f -> Color(0xFF8BC34A) // Light green - good
        score >= 0.4f -> Color(0xFFFFEB3B) // Yellow - average
        score >= 0.2f -> Color(0xFFFF9800) // Orange - needs work
        else -> Color(0xFFF44336) // Red - underdeveloped
    }
}

private fun DrawScope.drawChestShape(
    centerX: Float,
    top: Float,
    bottom: Float,
    shoulderWidth: Float,
    color: Color
) {
    val path = Path().apply {
        moveTo(centerX - shoulderWidth * 0.5f, top)
        lineTo(centerX + shoulderWidth * 0.5f, top)
        lineTo(centerX + shoulderWidth * 0.4f, bottom)
        lineTo(centerX - shoulderWidth * 0.4f, bottom)
        close()
    }
    drawPath(path, color)
    drawPath(path, Color.White.copy(alpha = 0.2f), style = Stroke(width = 2f))
}

private fun DrawScope.drawArm(
    startX: Float,
    startY: Float,
    length: Float,
    width: Float,
    angle: Float,
    color: Color
) {
    val radians = Math.toRadians(angle.toDouble() + 90)
    val endX = startX + (length * cos(radians)).toFloat()
    val endY = startY + (length * sin(radians)).toFloat()

    // Upper arm
    val upperLength = length * 0.5f
    val midX = startX + (upperLength * cos(radians)).toFloat()
    val midY = startY + (upperLength * sin(radians)).toFloat()

    drawRoundRect(
        color = color,
        topLeft = Offset(minOf(startX, midX) - width / 2, minOf(startY, midY)),
        size = Size(width, upperLength),
        cornerRadius = CornerRadius(width / 2)
    )

    // Forearm (slightly thinner)
    drawRoundRect(
        color = color.copy(alpha = 0.9f),
        topLeft = Offset(minOf(midX, endX) - width * 0.4f, minOf(midY, endY)),
        size = Size(width * 0.8f, length * 0.5f),
        cornerRadius = CornerRadius(width / 2)
    )
}

private fun DrawScope.drawLeg(
    startX: Float,
    startY: Float,
    length: Float,
    width: Float,
    angle: Float,
    color: Color
) {
    val radians = Math.toRadians(angle.toDouble() + 90)

    // Thigh
    val thighLength = length * 0.5f
    drawRoundRect(
        color = color,
        topLeft = Offset(startX - width / 2, startY),
        size = Size(width, thighLength),
        cornerRadius = CornerRadius(width / 3)
    )

    // Calf (slightly thinner)
    drawRoundRect(
        color = color.copy(alpha = 0.85f),
        topLeft = Offset(startX - width * 0.4f, startY + thighLength - 5),
        size = Size(width * 0.8f, length * 0.5f),
        cornerRadius = CornerRadius(width / 3)
    )
}

private fun DrawScope.drawBodyOutline(
    centerX: Float,
    centerY: Float,
    shoulderWidth: Float,
    hipWidth: Float,
    torsoHeight: Float,
    armLength: Float,
    legLength: Float,
    headRadius: Float
) {
    // Add subtle glow/outline effect
    val outlineColor = Color.White.copy(alpha = 0.15f)

    // Shoulder line
    drawLine(
        color = outlineColor,
        start = Offset(centerX - shoulderWidth * 0.5f, centerY - torsoHeight * 0.4f),
        end = Offset(centerX + shoulderWidth * 0.5f, centerY - torsoHeight * 0.4f),
        strokeWidth = 3f
    )

    // Center line (spine)
    drawLine(
        color = outlineColor,
        start = Offset(centerX, centerY - torsoHeight * 0.6f - headRadius),
        end = Offset(centerX, centerY + torsoHeight * 0.35f),
        strokeWidth = 2f
    )
}
