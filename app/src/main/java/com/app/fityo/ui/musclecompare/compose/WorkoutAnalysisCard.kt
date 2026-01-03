package com.app.fityo.ui.musclecompare.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.dominio.CorrelationType
import com.app.fityo.dominio.MuscleGroupCorrelation
import com.app.fityo.dominio.SuggestionPriority
import com.app.fityo.dominio.WorkoutAnalysis
import com.app.fityo.dominio.WorkoutSuggestion

/**
 * Card che mostra l'analisi dell'allenamento correlata ai cambiamenti muscolari visivi.
 * Fornisce feedback incoraggiante e suggerimenti personalizzati.
 */
@Composable
fun WorkoutAnalysisCard(
    analysis: WorkoutAnalysis?,
    modifier: Modifier = Modifier
) {
    if (analysis == null) return

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AccentBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Analytics,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "Analisi Allenamento",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Correlazione scheda - risultati",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Risultato generale
            EffectivenessIndicator(
                isEffective = analysis.isWorkoutEffective,
                message = analysis.overallMessage
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Messaggio incoraggiante
            EncouragementSection(message = analysis.encouragementMessage)

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = TextSecondary.copy(alpha = 0.2f))

            Spacer(modifier = Modifier.height(16.dp))

            // Statistiche workout
            WorkoutStatsSection(analysis)

            Spacer(modifier = Modifier.height(16.dp))

            // Correlazioni per gruppo muscolare
            Text(
                "Correlazione Allenamento-Risultati",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            analysis.muscleGroupCorrelations.forEach { correlation ->
                CorrelationItem(correlation)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Suggerimenti
            if (analysis.suggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = TextSecondary.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                SuggestionsSection(suggestions = analysis.suggestions)
            }
        }
    }
}

@Composable
private fun EffectivenessIndicator(
    isEffective: Boolean,
    message: String
) {
    val backgroundColor = if (isEffective) AccentGreen.copy(alpha = 0.15f) else AccentOrange.copy(alpha = 0.15f)
    val iconColor = if (isEffective) AccentGreen else AccentOrange
    val icon = if (isEffective) Icons.Default.CheckCircle else Icons.Default.TrendingFlat

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                message,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EncouragementSection(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurface)
            .padding(12.dp)
    ) {
        Text(
            message,
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Start,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun WorkoutStatsSection(analysis: WorkoutAnalysis) {
    val stats = analysis.workoutStats

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatBox(
            value = stats.totalWorkouts.toString(),
            label = "Allenamenti",
            icon = Icons.Default.FitnessCenter
        )
        StatBox(
            value = String.format("%.1f", stats.workoutsPerWeek),
            label = "/settimana",
            icon = Icons.Default.TrendingUp
        )
        StatBox(
            value = stats.daysSinceLastWorkout?.toString() ?: "-",
            label = "giorni fa",
            icon = Icons.Default.Analytics
        )
    }
}

@Composable
private fun StatBox(
    value: String,
    label: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = AccentBlue,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun CorrelationItem(correlation: MuscleGroupCorrelation) {
    val (icon, color) = when (correlation.correlation) {
        CorrelationType.POSITIVE_STRONG -> Icons.Default.TrendingUp to AccentGreen
        CorrelationType.POSITIVE_WEAK -> Icons.Default.TrendingUp to AccentGreen.copy(alpha = 0.7f)
        CorrelationType.NEUTRAL -> Icons.Default.TrendingFlat to TextSecondary
        CorrelationType.NEEDS_ATTENTION -> Icons.Default.Warning to AccentOrange
        CorrelationType.UNEXPECTED_DECREASE -> Icons.Default.TrendingDown to AccentRed
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icona correlazione
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Info gruppo muscolare
        Column(modifier = Modifier.weight(1f)) {
            Text(
                correlation.muscleGroup,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Allenamento: ${String.format("%.0f", correlation.trainingPercent)}%",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("•", color = TextSecondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Cambio: ${if (correlation.visualChangePercent >= 0) "+" else ""}${String.format("%.1f", correlation.visualChangePercent)}%",
                    color = if (correlation.visualChangePercent >= 0) AccentGreen else AccentRed,
                    fontSize = 11.sp
                )
            }
        }

        // Barra progressi
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(DarkCard)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (correlation.trainingPercent / 100f).coerceIn(0f, 1f))
                    .height(6.dp)
                    .background(color)
            )
        }
    }
}

@Composable
private fun SuggestionsSection(suggestions: List<WorkoutSuggestion>) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Lightbulb,
                contentDescription = null,
                tint = AccentOrange,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Suggerimenti",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        suggestions.take(3).forEach { suggestion ->
            SuggestionItem(suggestion)
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun SuggestionItem(suggestion: WorkoutSuggestion) {
    val priorityColor = when (suggestion.priority) {
        SuggestionPriority.HIGH -> AccentRed
        SuggestionPriority.MEDIUM -> AccentOrange
        SuggestionPriority.LOW -> AccentBlue
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(priorityColor.copy(alpha = 0.1f))
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(priorityColor)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            suggestion.suggestion,
            color = TextPrimary,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}
