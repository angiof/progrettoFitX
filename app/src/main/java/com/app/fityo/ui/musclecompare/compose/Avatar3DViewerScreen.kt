package com.app.fityo.ui.musclecompare.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.app.fityo.R
import com.app.fityo.avatar3d.model.BodyMeshGenerator
import com.app.fityo.avatar3d.model.ShapeParameters
import com.app.fityo.avatar3d.model.ZoneColors
import com.app.fityo.avatar3d.processing.Video360Processor
import com.app.fityo.mediapipe.BodyIntelligenceAnalyzer
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode

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
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showZoneColors by remember { mutableStateOf(true) }
    var showInfo by remember { mutableStateOf(false) }

    // SceneView reference
    var sceneView by remember { mutableStateOf<SceneView?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 3D Scene background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
        ) {
            if (!isProcessing && measurements != null) {
                // 3D Viewer usando SceneView
                AndroidView(
                    factory = { ctx ->
                        SceneView(ctx).apply {
                            sceneView = this

                            // Setup camera
                            cameraNode.position = Position(z = 4.0f)

                            // Setup lighting
                            // Note: SceneView handles default lighting

                            // Create simple body representation
                            // In a full implementation, load .glb model with morphing
                            createBodyVisualization(this, measurements, zoneAnalysis, showZoneColors)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { view ->
                        // Update zone colors when toggle changes
                        updateBodyColors(view, zoneAnalysis, showZoneColors)
                    }
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
                step = processingStep
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
    step: String
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
            // Rotating 3D icon
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(120.dp),
                    color = AccentBlue,
                    strokeWidth = 8.dp,
                    trackColor = DarkSurface,
                )
                Icon(
                    Icons.Default.ViewInAr,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(48.dp)
                )
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

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${(progress * 100).toInt()}%",
                color = AccentBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
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

/**
 * Crea visualizzazione 3D del corpo usando SceneView.
 * Genera mesh procedurale basata sui parametri shape.
 */
private fun createBodyVisualization(
    sceneView: SceneView,
    measurements: Video360Processor.AggregatedMeasurements,
    zoneAnalysis: List<BodyIntelligenceAnalyzer.BodyZoneAnalysis>?,
    showZoneColors: Boolean
) {
    // Genera shape parameters dalle misurazioni
    val shapeParams = ShapeParameters(
        height = 0.5f,
        weight = 0.5f,
        muscle = 0.6f,
        shoulderWidth = ((measurements.shoulderToHipRatio - 0.8f) / 0.8f).coerceIn(0f, 1f),
        hipWidth = 0.5f,
        torsoLength = 0.5f,
        legLength = ((measurements.legToTorsoRatio - 0.8f) / 0.6f).coerceIn(0.3f, 0.7f),
        armLength = ((measurements.armToTorsoRatio - 0.3f) / 0.4f).coerceIn(0.3f, 0.7f),
        chestDepth = 0.6f,
        waistWidth = 0.4f
    )

    // Genera colori zone
    val zoneColors = if (showZoneColors && zoneAnalysis != null) {
        ZoneColors.fromZoneAnalysis(zoneAnalysis)
    } else {
        null
    }

    // Genera mesh
    val meshGenerator = BodyMeshGenerator()
    val bodyMesh = meshGenerator.generateMesh(shapeParams, zoneColors)

    // Log mesh info per debug
    android.util.Log.d("Avatar3D", "Generated mesh with ${bodyMesh.vertices.size} vertices, ${bodyMesh.indices.size} indices")

    // Note: SceneView richiede modelli glTF/GLB per il rendering.
    // Per utilizzare vertex data procedurali, servirebbero le API Filament low-level.
    // In produzione, le opzioni sono:
    // 1. Caricare un modello .glb base e applicare morph targets
    // 2. Generare un file .glb runtime usando una libreria glTF
    // 3. Usare Filament VertexBuffer/IndexBuffer direttamente

    // Per ora, la mesh è generata e pronta per essere visualizzata
    // quando viene implementato il rendering Filament completo.
}

/**
 * Aggiorna i colori delle zone muscolari sull'avatar.
 */
private fun updateBodyColors(
    sceneView: SceneView,
    zoneAnalysis: List<BodyIntelligenceAnalyzer.BodyZoneAnalysis>?,
    showZoneColors: Boolean
) {
    // I colori zone vengono applicati durante la generazione mesh
    // Questa funzione viene chiamata quando l'utente togla il toggle
    android.util.Log.d("Avatar3D", "Zone colors enabled: $showZoneColors")
}
