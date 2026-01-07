package com.app.fityo.import_scheda

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkCard = Color(0xFF222C35)
private val DarkSurface = Color(0xFF1A222A)
private val TextPrimary = Color(0xFFECF0F1)
private val TextSecondary = Color(0xFFB0BEC5)
private val AccentBlue = Color(0xFF40C4FF)
private val AccentGreen = Color(0xFF4CAF50)
private val AccentOrange = Color(0xFFFF9800)

/**
 * Dialog per selezionare l'engine Gemma (GPU o CPU)
 */
@Composable
fun GemmaEngineSelectionDialog(
    onDismiss: () -> Unit,
    onEngineSelected: (GemmaEngineType) -> Unit
) {
    val context = LocalContext.current
    val availableEngines = remember { GemmaLlmHelper.getAvailableEngines(context) }
    val currentEngine = GemmaLlmHelper.getEngineType()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = {
            Text(
                text = "Seleziona Motore IA",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Scegli il motore per l'elaborazione IA locale",
                    color = TextSecondary,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                GemmaEngineType.getAll().forEach { engineType ->
                    val isAvailable = availableEngines.contains(engineType)
                    val isSelected = engineType == currentEngine

                    EngineOptionCard(
                        icon = if (engineType == GemmaEngineType.GPU) Icons.Default.Speed else Icons.Default.Memory,
                        title = engineType.displayName,
                        subtitle = engineType.description,
                        accentColor = if (engineType == GemmaEngineType.GPU) AccentOrange else AccentBlue,
                        isAvailable = isAvailable,
                        isSelected = isSelected,
                        onClick = {
                            if (isAvailable) {
                                onEngineSelected(engineType)
                            }
                        }
                    )
                }

                val missingEngines = GemmaEngineType.getAll().filter { !availableEngines.contains(it) }
                if (missingEngines.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Modelli mancanti",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            missingEngines.forEach { engine ->
                                Text(
                                    text = "- ${engine.modelFileName}",
                                    color = TextSecondary.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Copia via ADB: adb push <file> /data/local/tmp/llm/",
                                color = TextSecondary.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Chiudi", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun EngineOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    isAvailable: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> accentColor.copy(alpha = 0.15f)
        !isAvailable -> DarkSurface.copy(alpha = 0.5f)
        else -> DarkSurface
    }

    val contentAlpha = if (isAvailable) 1f else 0.5f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isAvailable) Modifier.clickable(onClick = onClick) else Modifier),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f * contentAlpha)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor.copy(alpha = contentAlpha),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = TextPrimary.copy(alpha = contentAlpha),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = subtitle,
                    color = TextSecondary.copy(alpha = contentAlpha),
                    fontSize = 12.sp
                )
                if (!isAvailable) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Modello non trovato",
                        color = AccentOrange.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (isAvailable) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }
        }
    }
}

/**
 * Chip compatto per mostrare l'engine corrente
 */
@Composable
fun CurrentEngineChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentEngine = GemmaLlmHelper.getEngineType()
    val chipColor = if (currentEngine == GemmaEngineType.GPU) AccentOrange else AccentBlue

    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = chipColor.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (currentEngine == GemmaEngineType.GPU) Icons.Default.Speed else Icons.Default.Memory,
                contentDescription = null,
                tint = chipColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = currentEngine.displayName,
                color = chipColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.ExpandMore,
                contentDescription = null,
                tint = chipColor,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
