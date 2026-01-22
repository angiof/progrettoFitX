package com.app.fityo.ui.wger

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.HtmlCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.fityo.data_layer.network.WgerClient
import com.app.fityo.data_layer.repository.WgerRepository
import com.app.fityo.dominio.WgerExerciseDetail
import com.app.fityo.dominio.WgerImage
import com.app.fityo.dominio.WgerMuscle

private val DialogBackground = Color(0xFF10161B)
private val DialogSurface = Color(0xFF1A222A)
private val DialogCard = Color(0xFF222C35)
private val TextPrimary = Color(0xFFECF0F1)
private val TextSecondary = Color(0xFFB0BEC5)
private val AccentBlue = Color(0xFF40C4FF)

@Composable
fun WgerExerciseInfoDialog(
    exerciseId: Int,
    onDismiss: () -> Unit,
    onConfirm: (() -> Unit)? = null
) {
    val repository = remember { WgerRepository(WgerClient.service) }
    var detail by remember { mutableStateOf<WgerExerciseDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var reloadKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(exerciseId, reloadKey) {
        isLoading = true
        errorMessage = null
        detail = null
        val result = repository.getExerciseDetail(exerciseId)
        result.onSuccess {
            detail = it
        }.onFailure { error ->
            errorMessage = error.message ?: "Errore durante il caricamento."
        }
        isLoading = false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = DialogSurface,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = detail?.name ?: "Dettaglio esercizio",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Chiudi", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when {
                    isLoading -> {
                        LoadingBlock()
                    }
                    errorMessage != null -> {
                        ErrorBlock(
                            message = errorMessage.orEmpty(),
                            onRetry = { reloadKey++ }
                        )
                    }
                    detail != null -> {
                        DetailContent(detail = detail!!)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val confirmEnabled = detail != null && !isLoading && errorMessage == null
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = if (onConfirm == null) "Chiudi" else "Annulla",
                            color = TextSecondary
                        )
                    }
                    if (onConfirm != null) {
                        Spacer(modifier = Modifier.size(8.dp))
                        Button(
                            onClick = onConfirm,
                            enabled = confirmEnabled,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentBlue,
                                disabledContainerColor = DialogCard
                            )
                        ) {
                            Text(text = "OK")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingBlock() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DialogCard, RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = AccentBlue)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Caricamento dettagli...", color = TextSecondary)
    }
}

@Composable
private fun ErrorBlock(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DialogCard, RoundedCornerShape(16.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text(text = "Riprova")
        }
    }
}

@Composable
private fun DetailContent(detail: WgerExerciseDetail) {
    val mediaVideo = detail.videos.firstOrNull()?.url
    val mediaImage = selectMainImage(detail.images)
    val muscleImage = detail.muscles.firstOrNull { it.imageUrlMain != null }?.imageUrlMain

    if (mediaVideo != null) {
        WgerVideoPlayer(videoUrl = mediaVideo)
    } else if (mediaImage != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(mediaImage)
                .crossfade(true)
                .build(),
            contentDescription = detail.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(DialogBackground, RoundedCornerShape(12.dp))
        )
    }

    if (muscleImage != null) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Mappa muscolare",
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(muscleImage)
                .crossfade(true)
                .build(),
            contentDescription = "Mappa muscolare",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(DialogBackground, RoundedCornerShape(12.dp))
        )
    }

    if (detail.muscles.isNotEmpty()) {
        Spacer(modifier = Modifier.height(12.dp))
        MuscleChips(detail.muscles)
    }

    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Descrizione",
        color = TextPrimary,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    HtmlText(detail.descriptionHtml)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MuscleChips(muscles: List<WgerMuscle>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        muscles.forEach { muscle ->
            Surface(
                color = DialogCard,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = muscle.name,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun HtmlText(html: String) {
    if (html.isBlank()) {
        Text(
            text = "Nessuna descrizione disponibile.",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }

    AndroidView(
        factory = { context ->
            TextView(context).apply {
                setTextColor(TextSecondary.toArgb())
                textSize = 14f
            }
        },
        update = { textView ->
            textView.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun WgerVideoPlayer(videoUrl: String) {
    val context = LocalContext.current
    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
            prepare()
            playWhenReady = false
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
                controllerAutoShow = false
                controllerHideOnTouch = true
                controllerShowTimeoutMs = 3000
                setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(DialogBackground, RoundedCornerShape(12.dp))
    )
}

private fun selectMainImage(images: List<WgerImage>): String? {
    return images.firstOrNull { it.isMain == true }?.url ?: images.firstOrNull()?.url
}
