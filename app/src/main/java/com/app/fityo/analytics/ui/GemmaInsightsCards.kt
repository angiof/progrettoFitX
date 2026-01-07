package com.app.fityo.analytics.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.analytics.*
import com.app.fityo.import_scheda.GemmaEngineType

// Colors
private val DarkBackground = Color(0xFF0D0D0D)
private val CardBackground = Color(0xFF1A1A1A)
private val CardBackgroundElevated = Color(0xFF252525)
private val TextPrimary = Color(0xFFE8E8E8)
private val TextSecondary = Color(0xFFA0A0A0)
private val AccentPurple = Color(0xFF8B5CF6)
private val AccentGreen = Color(0xFF10B981)
private val AccentYellow = Color(0xFFF59E0B)
private val AccentRed = Color(0xFFEF4444)
private val AccentBlue = Color(0xFF3B82F6)

@Composable
fun GemmaWeeklyMotivationCard(
    insights: GemmaLiveInsights?,
    isLoading: Boolean,
    engineType: GemmaEngineType,
    onRefresh: () -> Unit,
    onEngineClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AccentPurple.copy(alpha = 0.15f),
                            CardBackground
                        )
                    )
                )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AccentPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                "Coach AI",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            insights?.profileName?.let { name ->
                                Text(
                                    text = name,
                                    color = AccentPurple,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Engine chip
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentPurple.copy(alpha = 0.2f),
                            modifier = Modifier.clickable { onEngineClick() }
                        ) {
                            Text(
                                text = engineType.displayName,
                                color = AccentPurple,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        // Refresh button
                        IconButton(
                            onClick = { if (!isLoading) onRefresh() },
                            enabled = !isLoading,
                            modifier = Modifier.size(32.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = AccentPurple,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Aggiorna",
                                    tint = AccentPurple,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Content
                when {
                    isLoading -> {
                        // Loading state
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(Modifier.height(12.dp))
                            CircularProgressIndicator(
                                color = AccentPurple,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Gemma sta analizzando i tuoi dati...",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                    insights != null && insights.weeklyMotivation.isNotBlank() -> {
                        // Success state with data
                        Column {
                            Text(
                                text = insights.weeklyMotivation,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                lineHeight = 24.sp
                            )

                            if (insights.performanceSummary.isNotBlank()) {
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = insights.performanceSummary,
                                    color = TextSecondary,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Generato: ${insights.generatedDate} (${insights.engineUsed})",
                                color = TextSecondary.copy(alpha = 0.6f),
                                fontSize = 10.sp
                            )
                        }
                    }
                    else -> {
                        // Empty/Error state - show prompt to refresh
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Psychology,
                                contentDescription = null,
                                tint = AccentPurple.copy(alpha = 0.5f),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Premi il pulsante aggiorna per generare\nconsigli personalizzati con l'AI",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = onRefresh,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AccentPurple
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Genera Consigli AI")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyStatsQuickCard(
    stats: WeeklyStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.FitnessCenter,
                value = stats.sessionsThisWeek.toString(),
                label = "Sessioni",
                color = AccentBlue
            )
            StatItem(
                icon = Icons.Default.Scale,
                value = String.format("%.0f", stats.totalVolumeKg),
                label = "Volume kg",
                color = AccentGreen
            )
            StatItem(
                icon = Icons.Default.Whatshot,
                value = stats.mostTrainedMuscle.take(8),
                label = "Top Muscolo",
                color = AccentYellow
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
fun NextWorkoutFocusCard(
    focus: NextWorkoutFocus,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.DirectionsRun,
                    null,
                    tint = AccentGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Prossimo Allenamento",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        focus.muscleGroup,
                        color = AccentGreen,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Intensita: ${focus.suggestedIntensity}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(focus.reason, color = TextSecondary, fontSize = 13.sp)

            if (focus.suggestedExercises.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text("Esercizi suggeriti:", color = TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    focus.suggestedExercises.take(3).forEach { exercise ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                exercise,
                                color = AccentGreen,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GemmaLiveSuggestionsCard(
    suggestions: List<LiveSuggestion>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Lightbulb,
                    null,
                    tint = AccentYellow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Suggerimenti AI",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))

            suggestions.forEach { suggestion ->
                SuggestionItem(suggestion)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SuggestionItem(suggestion: LiveSuggestion) {
    val priorityColor = when (suggestion.priority) {
        "HIGH" -> AccentRed
        "MEDIUM" -> AccentYellow
        else -> AccentBlue
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackgroundElevated)
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(priorityColor)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                suggestion.title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(2.dp))
            Text(
                suggestion.description,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun LoadRecommendationCard(
    recommendation: LoadRecommendation?,
    modifier: Modifier = Modifier
) {
    if (recommendation == null) return

    val (icon, color, text) = when (recommendation.trend) {
        LoadTrend.INCREASE -> Triple(Icons.Default.TrendingUp, AccentGreen, "Aumenta")
        LoadTrend.DECREASE -> Triple(Icons.Default.TrendingDown, AccentRed, "Riduci")
        LoadTrend.MAINTAIN -> Triple(Icons.Default.Remove, AccentBlue, "Mantieni")
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
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
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "$text Carichi",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (recommendation.percentage > 0) {
                    Text(
                        "${if (recommendation.trend == LoadTrend.INCREASE) "+" else "-"}${recommendation.percentage}%",
                        color = color,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    recommendation.reason,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun RestRecommendationCard(
    recommendation: RestRecommendation?,
    modifier: Modifier = Modifier
) {
    if (recommendation == null) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
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
                    .background(AccentBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Bedtime,
                    null,
                    tint = AccentBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Riposo Consigliato",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "${recommendation.suggestedDays}",
                        color = AccentBlue,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "giorni tra sessioni",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Text(
                    "Attuale: ${String.format("%.1f", recommendation.currentAvgDays)} giorni",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun DeloadWarningCard(
    deloadNeeded: Boolean,
    deloadReason: String?,
    modifier: Modifier = Modifier
) {
    if (!deloadNeeded) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, AccentYellow.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AccentYellow.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                null,
                tint = AccentYellow,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    "Settimana di Scarico Consigliata",
                    color = AccentYellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                deloadReason?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(it, color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun GemmaNotAvailableCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.SmartToy,
                null,
                tint = TextSecondary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Gemma AI non disponibile",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Copia il modello Gemma sul device per attivare i suggerimenti AI personalizzati",
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
