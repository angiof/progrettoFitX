package com.app.fityo.ui.tutor.compose

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.app.fityo.dominio.ErrorSeverity
import com.app.fityo.dominio.ExerciseError
import com.app.fityo.dominio.ExerciseType
import kotlinx.coroutines.delay
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackScreen(
    exerciseType: ExerciseType,
    videoPath: String,
    errors: List<ExerciseError>,
    overallScore: Float,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onBack: () -> Unit,
    isNewAnalysis: Boolean = true
) {
    val context = LocalContext.current
    val exerciseColor = getExerciseColor(exerciseType)
    val scoreColor = getScoreColor(overallScore)

    // ExoPlayer setup
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val videoUri = if (videoPath.startsWith("/")) {
                Uri.fromFile(File(videoPath))
            } else {
                Uri.parse(videoPath)
            }
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = false
        }
    }

    var currentPosition by remember { mutableLongStateOf(0L) }
    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableLongStateOf(0L) }

    // Track playback position
    LaunchedEffect(exoPlayer) {
        while (true) {
            currentPosition = exoPlayer.currentPosition
            duration = exoPlayer.duration.coerceAtLeast(0L)
            isPlaying = exoPlayer.isPlaying
            delay(100)
        }
    }

    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Find current error based on position
    val currentError = errors.find { error ->
        currentPosition >= error.timestampMs && currentPosition <= error.endTimestampMs
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(exerciseType.displayName, fontSize = 16.sp)
                        Text(
                            text = "Punteggio: ${overallScore.toInt()}/100",
                            fontSize = 12.sp,
                            color = scoreColor
                        )
                    }
                },
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
                .padding(paddingValues)
        ) {
            // Video Player
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = true
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Error indicator overlay
                currentError?.let { error ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(getSeverityColor(error.severity).copy(alpha = 0.9f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = error.errorType.displayName,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Error timeline markers
            if (errors.isNotEmpty() && duration > 0) {
                ErrorTimeline(
                    errors = errors,
                    duration = duration,
                    currentPosition = currentPosition,
                    onSeek = { position ->
                        exoPlayer.seekTo(position)
                    }
                )
            }

            // Scrollable content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                // Quick score
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    QuickScoreCard(
                        score = overallScore,
                        scoreColor = scoreColor,
                        errorsCount = errors.size
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Error chips (quick jump)
                if (errors.isNotEmpty()) {
                    item {
                        Text(
                            text = "Salta agli errori:",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(errors) { error ->
                                ErrorChip(
                                    error = error,
                                    isActive = currentError == error,
                                    onClick = {
                                        exoPlayer.seekTo(error.timestampMs)
                                        if (!exoPlayer.isPlaying) {
                                            exoPlayer.play()
                                        }
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Detailed errors
                item {
                    Text(
                        text = if (errors.isEmpty()) "Nessun errore rilevato" else "Dettagli errori (${errors.size})",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (errors.isEmpty()) {
                    item {
                        PerfectExecutionCard()
                    }
                } else {
                    items(errors) { error ->
                        PlaybackErrorCard(
                            error = error,
                            isActive = currentError == error,
                            onClick = {
                                exoPlayer.seekTo(error.timestampMs)
                                exoPlayer.play()
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Bottom buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCard)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDiscard,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextSecondary
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isNewAnalysis) "Scarta" else "Chiudi")
                }

                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGreen
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isNewAnalysis) "Salva" else "OK")
                }
            }
        }
    }
}

@Composable
private fun ErrorTimeline(
    errors: List<ExerciseError>,
    duration: Long,
    currentPosition: Long,
    onSeek: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(DarkCard)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        // Timeline background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(DarkSurface)
                .align(Alignment.Center)
        )

        // Progress
        if (duration > 0) {
            val progressFraction = (currentPosition.toFloat() / duration).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFraction)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(AccentBlue)
                    .align(Alignment.CenterStart)
            )
        }

        // Error markers
        errors.forEach { error ->
            if (duration > 0) {
                val position = (error.timestampMs.toFloat() / duration).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(position)
                        .align(Alignment.CenterStart)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(getSeverityColor(error.severity))
                            .align(Alignment.CenterEnd)
                            .clickable { onSeek(error.timestampMs) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickScoreCard(
    score: Float,
    scoreColor: Color,
    errorsCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Punteggio",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${score.toInt()}",
                        color = scoreColor,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "/100",
                        color = scoreColor.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Errori",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "$errorsCount",
                    color = if (errorsCount == 0) AccentGreen else AccentOrange,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ErrorChip(
    error: ExerciseError,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val severityColor = getSeverityColor(error.severity)
    val backgroundColor = if (isActive) severityColor else severityColor.copy(alpha = 0.2f)
    val textColor = if (isActive) Color.White else severityColor

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formatTimestamp(error.timestampMs),
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PlaybackErrorCard(
    error: ExerciseError,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val severityColor = getSeverityColor(error.severity)
    val borderColor = if (isActive) severityColor else Color.Transparent

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) severityColor.copy(alpha = 0.1f) else DarkCard
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Timestamp button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(severityColor.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Vai a",
                        tint = severityColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatTimestamp(error.timestampMs),
                        color = severityColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = error.errorType.displayName,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SeverityBadge(error.severity)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = error.correctionHint,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SeverityBadge(severity: ErrorSeverity) {
    val color = getSeverityColor(severity)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = severity.displayName,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PerfectExecutionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AccentGreen.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Esecuzione Perfetta!",
                color = AccentGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Nessun errore rilevato. Ottimo lavoro!",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun getSeverityColor(severity: ErrorSeverity): Color {
    return when (severity) {
        ErrorSeverity.WARNING -> AccentYellow
        ErrorSeverity.ERROR -> AccentOrange
        ErrorSeverity.CRITICAL -> AccentRed
    }
}

private fun formatTimestamp(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = (ms / 1000) / 60
    return String.format("%d:%02d", minutes, seconds)
}
