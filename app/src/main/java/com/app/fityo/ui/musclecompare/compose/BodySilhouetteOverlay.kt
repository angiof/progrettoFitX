package com.app.fityo.ui.musclecompare.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.app.fityo.mediapipe.BodyIntelligenceAnalyzer
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

/**
 * Overlay 2D che mostra la silhouette del corpo con zone colorate
 * in base alla valutazione di Body Intelligence.
 */
@Composable
fun BodySilhouetteOverlay(
    landmarks: PoseLandmarkerResult?,
    zoneAnalysis: List<BodyIntelligenceAnalyzer.BodyZoneAnalysis>?,
    modifier: Modifier = Modifier,
    showLabels: Boolean = false,
    alpha: Float = 0.6f
) {
    if (landmarks == null || landmarks.landmarks().isEmpty()) return

    val poseLandmarks = landmarks.landmarks()[0]

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Mappa zone a colori
        val zoneColors = zoneAnalysis?.associate { analysis ->
            analysis.zone to Color(analysis.evaluation.colorHex).copy(alpha = alpha)
        } ?: emptyMap()

        // Helper per convertire coordinate normalizzate
        fun toOffset(index: Int): Offset? {
            if (index >= poseLandmarks.size) return null
            val lm = poseLandmarks[index]
            return Offset(lm.x() * width, lm.y() * height)
        }

        // Disegna le zone del corpo con colori
        drawBodyZones(
            toOffset = { toOffset(it) },
            zoneColors = zoneColors,
            defaultColor = TextSecondary.copy(alpha = alpha * 0.5f)
        )

        // Disegna contorno silhouette
        drawSilhouetteOutline(
            toOffset = { toOffset(it) },
            color = Color.White.copy(alpha = 0.8f)
        )

        // Disegna punti articolazione
        for (i in 11..32) {
            val offset = toOffset(i) ?: continue
            drawCircle(
                color = Color.White,
                radius = 6f,
                center = offset
            )
        }
    }
}

/**
 * Disegna le zone del corpo con colori di valutazione.
 */
private fun DrawScope.drawBodyZones(
    toOffset: (Int) -> Offset?,
    zoneColors: Map<BodyIntelligenceAnalyzer.BodyZone, Color>,
    defaultColor: Color
) {
    // SPALLE - zona trapezio/deltoidi
    drawShoulderZone(toOffset, zoneColors[BodyIntelligenceAnalyzer.BodyZone.SHOULDERS] ?: defaultColor)

    // PETTO - zona pettorali
    drawChestZone(toOffset, zoneColors[BodyIntelligenceAnalyzer.BodyZone.CHEST] ?: defaultColor)

    // BRACCIA - bicipiti/tricipiti
    drawArmsZone(toOffset, zoneColors[BodyIntelligenceAnalyzer.BodyZone.ARMS] ?: defaultColor)

    // CORE - addominali
    drawCoreZone(toOffset, zoneColors[BodyIntelligenceAnalyzer.BodyZone.CORE] ?: defaultColor)

    // VITA
    drawWaistZone(toOffset, zoneColors[BodyIntelligenceAnalyzer.BodyZone.WAIST] ?: defaultColor)

    // COSCE
    drawThighsZone(toOffset, zoneColors[BodyIntelligenceAnalyzer.BodyZone.THIGHS] ?: defaultColor)

    // POLPACCI
    drawCalvesZone(toOffset, zoneColors[BodyIntelligenceAnalyzer.BodyZone.CALVES] ?: defaultColor)
}

/**
 * Zona spalle.
 */
private fun DrawScope.drawShoulderZone(toOffset: (Int) -> Offset?, color: Color) {
    val leftShoulder = toOffset(11) ?: return
    val rightShoulder = toOffset(12) ?: return

    // Disegna un ovale/rettangolo arrotondato tra le spalle
    val centerX = (leftShoulder.x + rightShoulder.x) / 2
    val centerY = (leftShoulder.y + rightShoulder.y) / 2
    val width = kotlin.math.abs(rightShoulder.x - leftShoulder.x) * 1.3f
    val height = width * 0.4f

    drawOval(
        color = color,
        topLeft = Offset(centerX - width / 2, centerY - height / 2),
        size = Size(width, height)
    )
}

/**
 * Zona petto.
 */
private fun DrawScope.drawChestZone(toOffset: (Int) -> Offset?, color: Color) {
    val leftShoulder = toOffset(11) ?: return
    val rightShoulder = toOffset(12) ?: return
    val leftHip = toOffset(23) ?: return
    val rightHip = toOffset(24) ?: return

    val path = Path().apply {
        moveTo(leftShoulder.x, leftShoulder.y)
        lineTo(rightShoulder.x, rightShoulder.y)
        lineTo(rightShoulder.x * 0.9f + rightHip.x * 0.1f, (leftShoulder.y + leftHip.y) / 2.5f)
        lineTo(leftShoulder.x * 0.9f + leftHip.x * 0.1f, (leftShoulder.y + leftHip.y) / 2.5f)
        close()
    }

    drawPath(path, color)
}

/**
 * Zona braccia (sinistra e destra).
 */
private fun DrawScope.drawArmsZone(toOffset: (Int) -> Offset?, color: Color) {
    // Braccio sinistro
    drawArmSegment(toOffset(11), toOffset(13), toOffset(15), color)

    // Braccio destro
    drawArmSegment(toOffset(12), toOffset(14), toOffset(16), color)
}

private fun DrawScope.drawArmSegment(
    shoulder: Offset?,
    elbow: Offset?,
    wrist: Offset?,
    color: Color
) {
    if (shoulder == null || elbow == null || wrist == null) return

    val strokeWidth = 30f

    // Braccio superiore
    drawLine(
        color = color,
        start = shoulder,
        end = elbow,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )

    // Avambraccio
    drawLine(
        color = color,
        start = elbow,
        end = wrist,
        strokeWidth = strokeWidth * 0.8f,
        cap = StrokeCap.Round
    )
}

/**
 * Zona core/addominali.
 */
private fun DrawScope.drawCoreZone(toOffset: (Int) -> Offset?, color: Color) {
    val leftShoulder = toOffset(11) ?: return
    val rightShoulder = toOffset(12) ?: return
    val leftHip = toOffset(23) ?: return
    val rightHip = toOffset(24) ?: return

    // Rettangolo centrale (addome)
    val topY = (leftShoulder.y + leftHip.y) / 2.2f
    val bottomY = leftHip.y
    val leftX = leftShoulder.x * 0.85f + leftHip.x * 0.15f
    val rightX = rightShoulder.x * 0.85f + rightHip.x * 0.15f

    val path = Path().apply {
        moveTo(leftX, topY)
        lineTo(rightX, topY)
        lineTo(rightX * 0.95f + rightHip.x * 0.05f, bottomY)
        lineTo(leftX * 0.95f + leftHip.x * 0.05f, bottomY)
        close()
    }

    drawPath(path, color)
}

/**
 * Zona vita.
 */
private fun DrawScope.drawWaistZone(toOffset: (Int) -> Offset?, color: Color) {
    val leftHip = toOffset(23) ?: return
    val rightHip = toOffset(24) ?: return

    val centerX = (leftHip.x + rightHip.x) / 2
    val centerY = leftHip.y
    val width = kotlin.math.abs(rightHip.x - leftHip.x) * 1.2f
    val height = width * 0.3f

    drawOval(
        color = color,
        topLeft = Offset(centerX - width / 2, centerY - height / 2),
        size = Size(width, height)
    )
}

/**
 * Zona cosce.
 */
private fun DrawScope.drawThighsZone(toOffset: (Int) -> Offset?, color: Color) {
    // Coscia sinistra
    drawLegSegment(toOffset(23), toOffset(25), color, isUpper = true)

    // Coscia destra
    drawLegSegment(toOffset(24), toOffset(26), color, isUpper = true)
}

/**
 * Zona polpacci.
 */
private fun DrawScope.drawCalvesZone(toOffset: (Int) -> Offset?, color: Color) {
    // Polpaccio sinistro
    drawLegSegment(toOffset(25), toOffset(27), color, isUpper = false)

    // Polpaccio destro
    drawLegSegment(toOffset(26), toOffset(28), color, isUpper = false)
}

private fun DrawScope.drawLegSegment(
    top: Offset?,
    bottom: Offset?,
    color: Color,
    isUpper: Boolean
) {
    if (top == null || bottom == null) return

    val strokeWidth = if (isUpper) 45f else 35f

    drawLine(
        color = color,
        start = top,
        end = bottom,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

/**
 * Disegna il contorno della silhouette.
 */
private fun DrawScope.drawSilhouetteOutline(
    toOffset: (Int) -> Offset?,
    color: Color
) {
    val strokeWidth = 3f

    // Connessioni principali del corpo
    val connections = listOf(
        // Spalle
        Pair(11, 12),
        // Torso
        Pair(11, 23), Pair(12, 24), Pair(23, 24),
        // Braccia
        Pair(11, 13), Pair(13, 15),
        Pair(12, 14), Pair(14, 16),
        // Gambe
        Pair(23, 25), Pair(25, 27), Pair(27, 29), Pair(27, 31),
        Pair(24, 26), Pair(26, 28), Pair(28, 30), Pair(28, 32)
    )

    connections.forEach { (start, end) ->
        val startOffset = toOffset(start)
        val endOffset = toOffset(end)

        if (startOffset != null && endOffset != null) {
            drawLine(
                color = color,
                start = startOffset,
                end = endOffset,
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Versione semplificata per preview rapido durante la cattura.
 */
@Composable
fun SimpleSilhouetteOverlay(
    landmarks: PoseLandmarkerResult?,
    modifier: Modifier = Modifier,
    color: Color = AccentBlue.copy(alpha = 0.5f)
) {
    if (landmarks == null || landmarks.landmarks().isEmpty()) return

    val poseLandmarks = landmarks.landmarks()[0]

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        fun toOffset(index: Int): Offset? {
            if (index >= poseLandmarks.size) return null
            val lm = poseLandmarks[index]
            return Offset(lm.x() * width, lm.y() * height)
        }

        // Disegna silhouette semplice
        val connections = listOf(
            Pair(11, 12), Pair(11, 23), Pair(12, 24), Pair(23, 24),
            Pair(11, 13), Pair(13, 15), Pair(12, 14), Pair(14, 16),
            Pair(23, 25), Pair(25, 27), Pair(24, 26), Pair(26, 28)
        )

        connections.forEach { (start, end) ->
            val startOffset = toOffset(start)
            val endOffset = toOffset(end)

            if (startOffset != null && endOffset != null) {
                drawLine(
                    color = color,
                    start = startOffset,
                    end = endOffset,
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
            }
        }

        // Punti
        for (i in 11..28) {
            val offset = toOffset(i) ?: continue
            drawCircle(color = color, radius = 8f, center = offset)
            drawCircle(color = Color.White, radius = 4f, center = offset)
        }
    }
}
