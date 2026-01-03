package com.app.fityo.ui.musclecompare.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

/**
 * Overlay che disegna lo scheletro della posa rilevata in tempo reale.
 * Mostra i 33 punti MediaPipe e le connessioni tra di essi.
 *
 * Gestisce correttamente l'aspect ratio tra l'immagine analizzata e il display.
 *
 * @param imageAspectRatio Aspect ratio dell'immagine sorgente (width/height).
 *                         Default 4/3 per la maggior parte delle fotocamere.
 *                         Se null, usa mapping diretto (può non essere allineato).
 */
@Composable
fun SkeletonOverlay(
    landmarks: PoseLandmarkerResult?,
    modifier: Modifier = Modifier,
    pointColor: Color = AccentGreen,
    lineColor: Color = AccentBlue,
    pointRadius: Float = 8f,
    lineWidth: Float = 4f,
    mirrorHorizontally: Boolean = false,
    imageAspectRatio: Float? = 4f / 3f // Default camera aspect ratio
) {
    if (landmarks == null || landmarks.landmarks().isEmpty()) return

    val poseLandmarks = landmarks.landmarks()[0]

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val canvasAspectRatio = canvasWidth / canvasHeight

        // Calcola le dimensioni effettive considerando FILL_CENTER (crop to fill)
        // FILL_CENTER: l'immagine viene scalata per riempire il canvas, croppando se necessario
        val (effectiveWidth, effectiveHeight, offsetX, offsetY) = if (imageAspectRatio != null) {
            if (canvasAspectRatio > imageAspectRatio) {
                // Canvas più largo dell'immagine -> l'immagine viene scalata per altezza
                // e i lati vengono croppati
                val scaledWidth = canvasHeight * imageAspectRatio
                val cropX = (scaledWidth - canvasWidth) / 2f
                // Le coordinate x nell'immagine originale partono da cropX
                Quadruple(canvasWidth, canvasHeight, -cropX / scaledWidth, 0f)
            } else {
                // Canvas più alto dell'immagine -> l'immagine viene scalata per larghezza
                // e sopra/sotto vengono croppati
                val scaledHeight = canvasWidth / imageAspectRatio
                val cropY = (scaledHeight - canvasHeight) / 2f
                Quadruple(canvasWidth, canvasHeight, 0f, -cropY / scaledHeight)
            }
        } else {
            Quadruple(canvasWidth, canvasHeight, 0f, 0f)
        }

        // Calcola la scala per mappare le coordinate normalizzate
        val scaleX: Float
        val scaleY: Float

        if (imageAspectRatio != null) {
            if (canvasAspectRatio > imageAspectRatio) {
                // L'immagine è più alta rispetto al canvas
                val scaledWidth = canvasHeight * imageAspectRatio
                scaleX = scaledWidth
                scaleY = canvasHeight
            } else {
                // L'immagine è più larga rispetto al canvas
                val scaledHeight = canvasWidth / imageAspectRatio
                scaleX = canvasWidth
                scaleY = scaledHeight
            }
        } else {
            scaleX = canvasWidth
            scaleY = canvasHeight
        }

        // Funzione helper per convertire coordinate normalizzate in pixel
        fun landmarkToOffset(index: Int): Offset? {
            if (index >= poseLandmarks.size) return null
            val landmark = poseLandmarks[index]

            // Verifica visibilità
            val visibility = landmark.visibility()
            if (visibility.isPresent && visibility.get() < 0.5f) return null

            var normX = landmark.x()
            var normY = landmark.y()

            // Mirror se richiesto (per front camera)
            if (mirrorHorizontally) {
                normX = 1f - normX
            }

            // Applica scaling e offset per FILL_CENTER
            val x: Float
            val y: Float

            if (imageAspectRatio != null) {
                if (canvasAspectRatio > imageAspectRatio) {
                    // L'immagine viene scalata per altezza, croppata ai lati
                    val scaledWidth = canvasHeight * imageAspectRatio
                    val offsetAmount = (scaledWidth - canvasWidth) / 2f
                    x = normX * scaledWidth - offsetAmount
                    y = normY * canvasHeight
                } else {
                    // L'immagine viene scalata per larghezza, croppata sopra/sotto
                    val scaledHeight = canvasWidth / imageAspectRatio
                    val offsetAmount = (scaledHeight - canvasHeight) / 2f
                    x = normX * canvasWidth
                    y = normY * scaledHeight - offsetAmount
                }
            } else {
                x = normX * canvasWidth
                y = normY * canvasHeight
            }

            return Offset(x, y)
        }

        // Disegna le connessioni (linee)
        POSE_CONNECTIONS.forEach { (start, end) ->
            val startOffset = landmarkToOffset(start)
            val endOffset = landmarkToOffset(end)

            if (startOffset != null && endOffset != null) {
                // Determina il colore in base alla parte del corpo
                val connectionColor = when {
                    start in TORSO_POINTS && end in TORSO_POINTS -> TorsoColor
                    start in LEFT_ARM_POINTS || end in LEFT_ARM_POINTS -> LeftArmColor
                    start in RIGHT_ARM_POINTS || end in RIGHT_ARM_POINTS -> RightArmColor
                    start in LEFT_LEG_POINTS || end in LEFT_LEG_POINTS -> LeftLegColor
                    start in RIGHT_LEG_POINTS || end in RIGHT_LEG_POINTS -> RightLegColor
                    else -> lineColor
                }

                drawLine(
                    color = connectionColor.copy(alpha = 0.8f),
                    start = startOffset,
                    end = endOffset,
                    strokeWidth = lineWidth,
                    cap = StrokeCap.Round
                )
            }
        }

        // Disegna i punti
        for (i in 0 until minOf(poseLandmarks.size, 33)) {
            val offset = landmarkToOffset(i) ?: continue

            // Colore del punto in base alla parte del corpo
            val dotColor = when (i) {
                in TORSO_POINTS -> TorsoColor
                in LEFT_ARM_POINTS -> LeftArmColor
                in RIGHT_ARM_POINTS -> RightArmColor
                in LEFT_LEG_POINTS -> LeftLegColor
                in RIGHT_LEG_POINTS -> RightLegColor
                in FACE_POINTS -> FaceColor
                else -> pointColor
            }

            // Cerchio esterno (bordo)
            drawCircle(
                color = Color.White,
                radius = pointRadius + 2f,
                center = offset
            )

            // Cerchio interno (punto colorato)
            drawCircle(
                color = dotColor,
                radius = pointRadius,
                center = offset
            )
        }
    }
}

// Colori per le diverse parti del corpo
private val TorsoColor = Color(0xFF4CAF50)      // Verde - torso
private val LeftArmColor = Color(0xFF2196F3)    // Blu - braccio sinistro
private val RightArmColor = Color(0xFFFF9800)   // Arancione - braccio destro
private val LeftLegColor = Color(0xFF9C27B0)    // Viola - gamba sinistra
private val RightLegColor = Color(0xFFE91E63)   // Rosa - gamba destra
private val FaceColor = Color(0xFFFFEB3B)       // Giallo - viso

// Indici dei punti per parte del corpo
private val FACE_POINTS = setOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
private val TORSO_POINTS = setOf(11, 12, 23, 24)
private val LEFT_ARM_POINTS = setOf(11, 13, 15, 17, 19, 21)
private val RIGHT_ARM_POINTS = setOf(12, 14, 16, 18, 20, 22)
private val LEFT_LEG_POINTS = setOf(23, 25, 27, 29, 31)
private val RIGHT_LEG_POINTS = setOf(24, 26, 28, 30, 32)

/**
 * Connessioni standard MediaPipe Pose.
 * Ogni coppia rappresenta due punti da collegare con una linea.
 */
private val POSE_CONNECTIONS = listOf(
    // Viso
    Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 7),
    Pair(0, 4), Pair(4, 5), Pair(5, 6), Pair(6, 8),
    Pair(9, 10),

    // Spalle
    Pair(11, 12),

    // Braccio sinistro
    Pair(11, 13), Pair(13, 15),
    Pair(15, 17), Pair(15, 19), Pair(15, 21), Pair(17, 19),

    // Braccio destro
    Pair(12, 14), Pair(14, 16),
    Pair(16, 18), Pair(16, 20), Pair(16, 22), Pair(18, 20),

    // Torso
    Pair(11, 23), Pair(12, 24), Pair(23, 24),

    // Gamba sinistra
    Pair(23, 25), Pair(25, 27),
    Pair(27, 29), Pair(27, 31), Pair(29, 31),

    // Gamba destra
    Pair(24, 26), Pair(26, 28),
    Pair(28, 30), Pair(28, 32), Pair(30, 32)
)

/**
 * Mappa dei nomi dei landmark per debug/visualizzazione.
 */
object PoseLandmarkNames {
    const val NOSE = 0
    const val LEFT_EYE_INNER = 1
    const val LEFT_EYE = 2
    const val LEFT_EYE_OUTER = 3
    const val RIGHT_EYE_INNER = 4
    const val RIGHT_EYE = 5
    const val RIGHT_EYE_OUTER = 6
    const val LEFT_EAR = 7
    const val RIGHT_EAR = 8
    const val MOUTH_LEFT = 9
    const val MOUTH_RIGHT = 10
    const val LEFT_SHOULDER = 11
    const val RIGHT_SHOULDER = 12
    const val LEFT_ELBOW = 13
    const val RIGHT_ELBOW = 14
    const val LEFT_WRIST = 15
    const val RIGHT_WRIST = 16
    const val LEFT_PINKY = 17
    const val RIGHT_PINKY = 18
    const val LEFT_INDEX = 19
    const val RIGHT_INDEX = 20
    const val LEFT_THUMB = 21
    const val RIGHT_THUMB = 22
    const val LEFT_HIP = 23
    const val RIGHT_HIP = 24
    const val LEFT_KNEE = 25
    const val RIGHT_KNEE = 26
    const val LEFT_ANKLE = 27
    const val RIGHT_ANKLE = 28
    const val LEFT_HEEL = 29
    const val RIGHT_HEEL = 30
    const val LEFT_FOOT_INDEX = 31
    const val RIGHT_FOOT_INDEX = 32
}

/**
 * Helper data class per contenere 4 valori Float.
 */
private data class Quadruple(
    val first: Float,
    val second: Float,
    val third: Float,
    val fourth: Float
)
