package com.app.fityo.analytics.ui

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
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.analytics.DigitalTwinPrediction
import com.app.fityo.analytics.OptimalLoad
import com.app.fityo.analytics.OvertrainingLevel
import com.app.fityo.analytics.RecoveryLevel
import com.app.fityo.analytics.RecoveryStatus
import com.app.fityo.analytics.SessionStats
import com.app.fityo.analytics.SuggestionPriority
import com.app.fityo.analytics.WorkoutSuggestion
import com.app.fityo.ui.dashboard.compose.ChartCard
import com.app.fityo.ui.dashboard.compose.DashboardAccentBlue
import com.app.fityo.ui.dashboard.compose.DashboardAccentGreen
import com.app.fityo.ui.dashboard.compose.DashboardAccentPurple
import com.app.fityo.ui.dashboard.compose.DashboardCardBorder
import com.app.fityo.ui.dashboard.compose.DashboardChartColors
import com.app.fityo.ui.dashboard.compose.DashboardError
import com.app.fityo.ui.dashboard.compose.DashboardSurface
import com.app.fityo.ui.dashboard.compose.DashboardTextMuted
import com.app.fityo.ui.dashboard.compose.DashboardTextPrimary
import com.app.fityo.ui.dashboard.compose.DashboardTextSecondary
import java.util.Locale

/*
 * Card testuali delle analytics. Nessuna di queste chiama un modello: leggono
 * WorkoutInsights, che e' calcolato con statistica sui dati del database.
 */

@Composable
fun SessionStatsCard(stats: SessionStats) {
    ChartCard(
        title = "Statistiche sessioni",
        subtitle = "Il quadro generale del periodo analizzato",
        accent = DashboardChartColors[2]
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem("Allenamenti", stats.totalWorkouts.toString(), DashboardAccentBlue)
            StatItem("Completati 7gg", stats.streakDays.toString(), DashboardAccentGreen)
            StatItem(
                "Consistenza",
                String.format(Locale.getDefault(), "%.0f%%", stats.consistencyScore),
                DashboardAccentPurple
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem(
                "Volume totale",
                String.format(Locale.getDefault(), "%.0f", stats.totalVolume),
                DashboardAccentBlue
            )
            StatItem(
                "Intensita media",
                String.format(Locale.getDefault(), "%.0f%%", stats.avgIntensity * 100),
                DashboardChartColors[4]
            )
            StatItem(
                "Frequenza",
                String.format(Locale.getDefault(), "%.1f/sett", stats.weeklyFrequency),
                DashboardAccentGreen
            )
        }

        if (stats.strongestMuscle != null || stats.weakestMuscle != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                stats.strongestMuscle?.let {
                    StatItem("Piu allenato", it, DashboardAccentGreen)
                }
                stats.weakestMuscle?.let {
                    StatItem("Da recuperare", it, DashboardChartColors[3])
                }
            }
        }
    }
}

@Composable
fun RecoveryCard(
    recoveryStatus: RecoveryStatus,
    overtrainingRisk: OvertrainingLevel,
    fatigueScore: Float,
    optimalRestDays: Int
) {
    val color = recoveryColor(recoveryStatus.level)

    ChartCard(
        title = "Recupero complessivo",
        subtitle = recoveryLabel(recoveryStatus.level),
        accent = color
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = { (recoveryStatus.percentage / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .clip(CircleShape),
                color = color,
                trackColor = DashboardCardBorder
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = String.format(Locale.getDefault(), "%.0f%%", recoveryStatus.percentage),
                color = DashboardTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoChip(
                icon = Icons.Default.LocalFireDepartment,
                label = "Fatica",
                value = String.format(Locale.getDefault(), "%.0f%%", fatigueScore * 100),
                color = fatigueColor(fatigueScore)
            )
            InfoChip(
                icon = Icons.Default.Schedule,
                label = "Riposo",
                value = "$optimalRestDays giorni",
                color = DashboardAccentBlue
            )
            OvertrainingBadge(overtrainingRisk)
        }
    }
}

@Composable
private fun OvertrainingBadge(level: OvertrainingLevel) {
    val (color, text) = when (level) {
        OvertrainingLevel.LOW -> DashboardAccentGreen to "Carico ok"
        OvertrainingLevel.MODERATE -> DashboardChartColors[3] to "Attenzione"
        OvertrainingLevel.HIGH -> DashboardChartColors[4] to "Carico alto"
        OvertrainingLevel.CRITICAL -> DashboardError to "Critico"
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.18f))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(text = text, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun InfoChip(icon: ImageVector, label: String, value: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DashboardSurface)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(value, color = DashboardTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(label, color = DashboardTextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
fun OptimalLoadsCard(optimalLoads: Map<String, OptimalLoad>) {
    val items = optimalLoads.values
        .sortedWith(compareByDescending<OptimalLoad> { it.confidence }.thenByDescending { it.progressionPercent })
        .take(4)

    ChartCard(
        title = "Carichi consigliati",
        subtitle = "Calcolati dalla progressione registrata sulle schede",
        accent = DashboardAccentGreen
    ) {
        if (items.isEmpty()) {
            Text(
                text = "Servono piu sessioni con carichi registrati.",
                color = DashboardTextMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            return@ChartCard
        }
        items.forEachIndexed { index, load ->
            OptimalLoadRow(load)
            if (index != items.lastIndex) Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun OptimalLoadRow(load: OptimalLoad) {
    val progression = load.progressionPercent
    val (icon, color) = when {
        progression >= 1f -> Icons.Default.TrendingUp to DashboardAccentGreen
        progression <= -1f -> Icons.Default.TrendingDown to DashboardError
        else -> Icons.Default.TrendingFlat to DashboardChartColors[3]
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DashboardSurface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = load.exerciseName.replaceFirstChar { it.titlecase(Locale.getDefault()) },
                color = DashboardTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Affidabilita ${(load.confidence * 100).toInt()}%",
                color = DashboardTextMuted,
                fontSize = 11.sp
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(formatWeight(load.currentWeight), color = DashboardTextMuted, fontSize = 11.sp)
            Text(formatWeight(load.suggestedWeight), color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
                text = String.format(Locale.getDefault(), "%+.1f%%", progression),
                color = color,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun SuggestionsCard(suggestions: List<WorkoutSuggestion>) {
    ChartCard(
        title = "Cosa dicono i dati",
        subtitle = "Regole applicate a volume, recupero ed equilibrio",
        accent = DashboardChartColors[5]
    ) {
        if (suggestions.isEmpty()) {
            Text(
                text = "Nessun rilievo: continua cosi.",
                color = DashboardTextMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            return@ChartCard
        }
        suggestions.take(4).forEachIndexed { index, suggestion ->
            SuggestionItem(suggestion)
            if (index != suggestions.take(4).lastIndex) Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun SuggestionItem(suggestion: WorkoutSuggestion) {
    val color = when (suggestion.priority) {
        SuggestionPriority.CRITICAL -> DashboardError
        SuggestionPriority.HIGH -> DashboardChartColors[4]
        SuggestionPriority.MEDIUM -> DashboardChartColors[3]
        SuggestionPriority.LOW -> DashboardAccentGreen
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DashboardSurface)
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = suggestion.title,
                color = DashboardTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(text = suggestion.description, color = DashboardTextSecondary, fontSize = 12.sp)
            suggestion.actionText?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = it, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProjectionCard(prediction: DigitalTwinPrediction?) {
    ChartCard(
        title = "Proiezione forza",
        subtitle = "Estrapolazione dell'andamento registrato finora",
        accent = DashboardAccentPurple
    ) {
        if (prediction == null) {
            Text(
                text = "Servono piu allenamenti e un profilo compilato.",
                color = DashboardTextMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            return@ChartCard
        }

        Text(text = "Potenziale utilizzato", color = DashboardTextMuted, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (prediction.currentPotentialUsage / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(CircleShape),
            color = DashboardAccentPurple,
            trackColor = DashboardCardBorder
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = String.format(Locale.getDefault(), "%.0f%%", prediction.currentPotentialUsage),
            color = DashboardTextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProjectionBadge("Oggi", prediction.currentStrengthIndex, DashboardAccentBlue)
            ProjectionBadge("1 mese", prediction.projectedStrength1Month, DashboardAccentGreen)
            ProjectionBadge("3 mesi", prediction.projectedStrength3Months, DashboardAccentPurple)
        }

        if (prediction.injuryRiskScore > 40f) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DashboardChartColors[4].copy(alpha = 0.14f))
                    .padding(12.dp)
            ) {
                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "Rischio infortunio stimato: %.0f%%",
                        prediction.injuryRiskScore
                    ),
                    color = DashboardChartColors[4],
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ProjectionBadge(label: String, value: Float, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = String.format(Locale.getDefault(), "%.1f", value),
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = label, color = DashboardTextMuted, fontSize = 10.sp)
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = DashboardTextMuted, fontSize = 10.sp)
    }
}

private fun recoveryColor(level: RecoveryLevel): Color = when (level) {
    RecoveryLevel.FULLY_RECOVERED -> DashboardAccentGreen
    RecoveryLevel.MOSTLY_RECOVERED -> DashboardAccentBlue
    RecoveryLevel.PARTIAL_RECOVERY -> DashboardChartColors[3]
    RecoveryLevel.NEEDS_REST -> DashboardError
}

private fun recoveryLabel(level: RecoveryLevel): String = when (level) {
    RecoveryLevel.FULLY_RECOVERED -> "Completamente recuperato"
    RecoveryLevel.MOSTLY_RECOVERED -> "Quasi pronto"
    RecoveryLevel.PARTIAL_RECOVERY -> "Recupero parziale"
    RecoveryLevel.NEEDS_REST -> "Riposo consigliato"
}

private fun fatigueColor(fatigue: Float): Color = when {
    fatigue < 0.3f -> DashboardAccentGreen
    fatigue < 0.5f -> DashboardChartColors[3]
    fatigue < 0.7f -> DashboardChartColors[4]
    else -> DashboardError
}

private fun formatWeight(value: Float): String =
    if (value > 0f) String.format(Locale.getDefault(), "%.1f kg", value) else "n/d"
