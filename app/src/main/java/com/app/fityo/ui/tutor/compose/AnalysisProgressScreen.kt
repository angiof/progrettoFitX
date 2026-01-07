package com.app.fityo.ui.tutor.compose

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.dominio.ExerciseType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AnalysisProgressScreen(
    exerciseType: ExerciseType,
    progress: Float,
    currentFrame: Int,
    totalFrames: Int,
    statusText: String,
    videoPath: String? = null
) {
    val exerciseColor = getExerciseColor(exerciseType)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "progress"
    )

    // Thumbnails del video (inizio, metà, fine)
    var thumbnails by remember { mutableStateOf<List<Bitmap?>>(listOf(null, null, null)) }
    var videoDuration by remember { mutableStateOf(0L) }

    // Estrai thumbnails dal video
    LaunchedEffect(videoPath) {
        if (videoPath != null) {
            withContext(Dispatchers.IO) {
                try {
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(videoPath)

                    val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    videoDuration = durationStr?.toLongOrNull() ?: 0L

                    if (videoDuration > 0) {
                        val startFrame = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        val midFrame = retriever.getFrameAtTime(videoDuration * 500, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        val endFrame = retriever.getFrameAtTime(videoDuration * 1000 - 100000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)

                        thumbnails = listOf(startFrame, midFrame, endFrame)
                    }

                    retriever.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Thumbnails del video con indicatore di progresso
            if (thumbnails.any { it != null }) {
                VideoThumbnailProgress(
                    thumbnails = thumbnails,
                    progress = animatedProgress,
                    exerciseColor = exerciseColor
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Icona animata con progresso circolare
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                // Cerchio di sfondo
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(120.dp),
                    color = DarkCard,
                    strokeWidth = 6.dp,
                    trackColor = DarkCard,
                    strokeCap = StrokeCap.Round
                )

                // Cerchio di progresso
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(120.dp),
                    color = exerciseColor,
                    strokeWidth = 6.dp,
                    trackColor = DarkCard,
                    strokeCap = StrokeCap.Round
                )

                // Contenuto centrale
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = exerciseColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Analisi in corso",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = exerciseType.displayName,
                color = exerciseColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Barra di progresso lineare
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = exerciseColor,
                trackColor = DarkCard,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = statusText,
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            if (totalFrames > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Frame $currentFrame / $totalFrames",
                    color = TextSecondary.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tips durante l'attesa
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCard)
                    .padding(14.dp)
            ) {
                Text(
                    text = "L'IA sta analizzando:",
                    color = AccentBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                AnalysisStepItem("Rilevamento pose frame per frame", animatedProgress > 0.1f)
                AnalysisStepItem("Calcolo angoli articolari", animatedProgress > 0.3f)
                AnalysisStepItem("Identificazione errori di forma", animatedProgress > 0.6f)
                AnalysisStepItem("Generazione feedback personalizzato", animatedProgress > 0.9f)
            }
        }
    }
}

@Composable
private fun VideoThumbnailProgress(
    thumbnails: List<Bitmap?>,
    progress: Float,
    exerciseColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        thumbnails.forEachIndexed { index, bitmap ->
            val isCompleted = when (index) {
                0 -> progress > 0.1f
                1 -> progress > 0.5f
                2 -> progress > 0.9f
                else -> false
            }

            val isActive = when (index) {
                0 -> progress in 0.01f..0.33f
                1 -> progress in 0.34f..0.66f
                2 -> progress in 0.67f..1f
                else -> false
            }

            VideoThumbnailItem(
                bitmap = bitmap,
                isCompleted = isCompleted,
                isActive = isActive,
                exerciseColor = exerciseColor,
                label = when (index) {
                    0 -> "Inizio"
                    1 -> "Metà"
                    2 -> "Fine"
                    else -> ""
                }
            )

            // Linea di connessione tra thumbnails
            if (index < thumbnails.size - 1) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .background(
                            if (progress > (index + 1) * 0.33f) exerciseColor
                            else DarkCard
                        )
                )
            }
        }
    }
}

@Composable
private fun VideoThumbnailItem(
    bitmap: Bitmap?,
    isCompleted: Boolean,
    isActive: Boolean,
    exerciseColor: Color,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = if (isActive) 2.dp else 1.dp,
                    color = when {
                        isActive -> exerciseColor
                        isCompleted -> AccentGreen
                        else -> DarkCard
                    },
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = label,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    alpha = if (isCompleted || isActive) 1f else 0.5f
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkSurface)
                )
            }

            // Overlay di completamento
            if (isCompleted) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AccentGreen.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Completato",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Indicatore attivo (pulsante)
            if (isActive && !isCompleted) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(exerciseColor.copy(alpha = 0.2f))
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = when {
                isActive -> exerciseColor
                isCompleted -> AccentGreen
                else -> TextSecondary
            },
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun AnalysisStepItem(text: String, isCompleted: Boolean = false) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (isCompleted) AccentGreen else TextSecondary.copy(alpha = 0.5f))
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = text,
            color = if (isCompleted) TextPrimary else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isCompleted) FontWeight.Medium else FontWeight.Normal
        )
        if (isCompleted) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
