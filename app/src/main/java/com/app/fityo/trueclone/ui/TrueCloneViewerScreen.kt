package com.app.fityo.trueclone.ui

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.trueclone.TrueCloneProcessor
import com.app.fityo.trueclone.comparison.MeshComparator
import com.app.fityo.trueclone.mesh.Mesh3D
import com.app.fityo.trueclone.mesh.HumanBodyMeshGenerator
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// Colori
private val DarkBackground = Color(0xFF0D0D0D)
private val DarkCard = Color(0xFF1A1A1A)
private val DarkSurface = Color(0xFF141414)
private val AccentBlue = Color(0xFF00B4D8)
private val AccentGreen = Color(0xFF00F5A0)
private val AccentRed = Color(0xFFFF6B6B)
private val AccentOrange = Color(0xFFFFAA33)
private val AccentYellow = Color(0xFFFFE066)
private val TextPrimary = Color(0xFFE8E8E8)
private val TextSecondary = Color(0xFFA0A0A0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrueCloneViewerScreen(
    result: TrueCloneProcessor.TrueCloneResult,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    var rotationY by remember { mutableFloatStateOf(0f) }
    var zoom by remember { mutableFloatStateOf(1f) }
    var showZoneColors by remember { mutableStateOf(true) }
    var showComparison by remember { mutableStateOf(result.comparisonResult != null) }

    val animatedRotation by animateFloatAsState(
        targetValue = rotationY,
        animationSpec = tween(100),
        label = "rotation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TrueClone 3D", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Indietro", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { showZoneColors = !showZoneColors }) {
                        Icon(
                            Icons.Default.Palette,
                            "Toggle colori zone",
                            tint = if (showZoneColors) AccentBlue else TextSecondary
                        )
                    }
                    if (result.comparisonResult != null) {
                        IconButton(onClick = { showComparison = !showComparison }) {
                            Icon(
                                Icons.Default.Compare,
                                "Toggle confronto",
                                tint = if (showComparison) AccentGreen else TextSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onSave,
                containerColor = AccentBlue
            ) {
                Icon(Icons.Default.Save, "Salva", tint = Color.White)
            }
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 3D Viewer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.8f)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1A1A2E),
                                Color(0xFF0F0F1A)
                            )
                        )
                    )
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            rotationY += dragAmount.x * 0.5f
                        }
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoomChange, _ ->
                            zoom = (zoom * zoomChange).coerceIn(0.5f, 2f)
                        }
                    }
            ) {
                // Canvas 3D Body
                result.mesh?.let { mesh ->
                    Canvas3DBody(
                        mesh = mesh,
                        rotationY = animatedRotation,
                        zoom = zoom,
                        showZoneColors = showZoneColors,
                        showComparison = showComparison,
                        comparisonResult = result.comparisonResult,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Rotation hint
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkCard.copy(alpha = 0.8f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.TouchApp,
                        null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Trascina per ruotare",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                // Reset rotation
                IconButton(
                    onClick = { rotationY = 0f; zoom = 1f },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Default.RotateLeft,
                        "Reset rotazione",
                        tint = TextSecondary
                    )
                }
            }

            // Legend heatmap (se in modalità confronto)
            if (showComparison && result.comparisonResult != null) {
                HeatmapLegend(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Zone cards
            if (result.zones != null) {
                Text(
                    text = if (showComparison) "Progressi per Zona" else "Analisi Zone Muscolari",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Zone grid
                ZoneCardsGrid(
                    zones = result.zones,
                    comparisonResult = result.comparisonResult,
                    showComparison = showComparison,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Comparison summary
            result.comparisonResult?.let { comparison ->
                Spacer(modifier = Modifier.height(20.dp))

                ComparisonSummaryCard(
                    comparison = comparison,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun Canvas3DBody(
    mesh: Mesh3D,
    rotationY: Float,
    zoom: Float,
    showZoneColors: Boolean,
    showComparison: Boolean,
    comparisonResult: MeshComparator.ComparisonResult?,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val scale = minOf(size.width, size.height) * 0.4f * zoom

        val angleRad = rotationY * PI.toFloat() / 180f

        // Proietta e disegna i vertici con colori
        drawProjectedMesh(
            mesh = mesh,
            centerX = centerX,
            centerY = centerY,
            scale = scale,
            rotationY = angleRad,
            showZoneColors = showZoneColors
        )
    }
}

private fun DrawScope.drawProjectedMesh(
    mesh: Mesh3D,
    centerX: Float,
    centerY: Float,
    scale: Float,
    rotationY: Float,
    showZoneColors: Boolean
) {
    // Calcola posizioni proiettate
    val projectedPoints = mesh.vertices.map { v ->
        // Rotazione Y
        val rotatedX = v.x * cos(rotationY) - v.z * sin(rotationY)
        val rotatedZ = v.x * sin(rotationY) + v.z * cos(rotationY)

        // Proiezione prospettica semplificata
        val perspective = 1f / (1f + rotatedZ * 0.3f)
        val screenX = centerX + rotatedX * scale * perspective
        val screenY = centerY - v.y * scale * perspective

        Triple(screenX, screenY, rotatedZ)
    }

    // Disegna facce (ordinate per profondità - back to front)
    val sortedFaces = mesh.faces.sortedByDescending { face ->
        (projectedPoints[face.v1].third +
                projectedPoints[face.v2].third +
                projectedPoints[face.v3].third) / 3f
    }

    for (face in sortedFaces) {
        val p1 = projectedPoints[face.v1]
        val p2 = projectedPoints[face.v2]
        val p3 = projectedPoints[face.v3]

        // Calcola colore medio della faccia
        val v1 = mesh.vertices[face.v1]
        val v2 = mesh.vertices[face.v2]
        val v3 = mesh.vertices[face.v3]

        val avgR = (v1.r + v2.r + v3.r) / 3f
        val avgG = (v1.g + v2.g + v3.g) / 3f
        val avgB = (v1.b + v2.b + v3.b) / 3f

        // Shading basato sulla profondità
        val avgZ = (p1.third + p2.third + p3.third) / 3f
        val shading = (0.6f + 0.4f * (1f - avgZ.coerceIn(-1f, 1f))).coerceIn(0.3f, 1f)

        val faceColor = if (showZoneColors) {
            Color(
                red = (avgR * shading).coerceIn(0f, 1f),
                green = (avgG * shading).coerceIn(0f, 1f),
                blue = (avgB * shading).coerceIn(0f, 1f)
            )
        } else {
            Color(
                red = 0.7f * shading,
                green = 0.6f * shading,
                blue = 0.55f * shading
            )
        }

        val path = Path().apply {
            moveTo(p1.first, p1.second)
            lineTo(p2.first, p2.second)
            lineTo(p3.first, p3.second)
            close()
        }

        drawPath(path, faceColor, style = Fill)

        // Bordo sottile per definizione
        drawPath(
            path,
            faceColor.copy(alpha = 0.3f),
            style = Stroke(width = 0.5f)
        )
    }
}

@Composable
private fun HeatmapLegend(modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Legenda Progressi",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Gradient bar
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFE63329),  // Rosso
                            Color(0xFFFF9933),  // Arancio
                            Color(0xFFFFE64D),  // Giallo
                            Color(0xFF99FF66),  // Verde chiaro
                            Color(0xFF33CC80)   // Verde
                        )
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Perdita", color = AccentRed, fontSize = 10.sp)
                Text("Invariato", color = AccentYellow, fontSize = 10.sp)
                Text("Crescita", color = AccentGreen, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun ZoneCardsGrid(
    zones: Map<Int, HumanBodyMeshGenerator.MuscleZone>,
    comparisonResult: MeshComparator.ComparisonResult?,
    showComparison: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        zones.values.chunked(2).forEach { rowZones ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowZones.forEach { zone ->
                    val zoneChange = comparisonResult?.zoneChanges?.get(zone.id)

                    ZoneCard(
                        zone = zone,
                        change = if (showComparison) zoneChange else null,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Spacer se numero dispari
                if (rowZones.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ZoneCard(
    zone: HumanBodyMeshGenerator.MuscleZone,
    change: MeshComparator.ZoneChange?,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (change != null) {
        when (change.evaluation) {
            MeshComparator.ChangeEvaluation.SIGNIFICANT_GAIN,
            MeshComparator.ChangeEvaluation.MODERATE_GAIN -> AccentGreen.copy(alpha = 0.1f)
            MeshComparator.ChangeEvaluation.SIGNIFICANT_LOSS,
            MeshComparator.ChangeEvaluation.MODERATE_LOSS -> AccentRed.copy(alpha = 0.1f)
            else -> DarkCard
        }
    } else {
        DarkCard
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                zone.name,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (change != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (change.averageChange >= 0) Icons.Default.ArrowUpward
                        else Icons.Default.ArrowDownward,
                        null,
                        tint = if (change.averageChange >= 0) AccentGreen else AccentRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        String.format("%+.1f%%", change.percentChange),
                        color = if (change.averageChange >= 0) AccentGreen else AccentRed,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Progress bar per sviluppo zona
                val score = zone.developmentScore
                val scoreColor = when {
                    score >= 0.7f -> AccentGreen
                    score >= 0.4f -> AccentYellow
                    else -> AccentOrange
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                ) {
                    drawRect(Color.Gray.copy(alpha = 0.3f))
                    drawRect(
                        scoreColor,
                        size = size.copy(width = size.width * score)
                    )
                }

                Text(
                    "${(score * 100).toInt()}%",
                    color = scoreColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ComparisonSummaryCard(
    comparison: MeshComparator.ComparisonResult,
    modifier: Modifier = Modifier
) {
    val overallPercent = comparison.overallChange * 100
    val isPositive = overallPercent >= 0

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isPositive) AccentGreen.copy(alpha = 0.1f)
            else AccentRed.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Progresso Complessivo",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    null,
                    tint = if (isPositive) AccentGreen else AccentRed,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    String.format("%+.1f%%", abs(overallPercent)),
                    color = if (isPositive) AccentGreen else AccentRed,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Zone gained/lost
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${comparison.gainedZones.size}",
                        color = AccentGreen,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Zone in crescita", color = TextSecondary, fontSize = 11.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${comparison.unchangedZones.size}",
                        color = AccentYellow,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Zone stabili", color = TextSecondary, fontSize = 11.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${comparison.lostZones.size}",
                        color = AccentRed,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Zone in calo", color = TextSecondary, fontSize = 11.sp)
                }
            }

            // Message
            if (comparison.gainedZones.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "💪 Ottimi progressi in: ${comparison.gainedZones.take(3).joinToString(", ")}",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
