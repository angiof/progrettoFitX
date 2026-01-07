package com.app.fityo.analytics.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.analytics.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private val DarkCard = Color(0xFF1A1A1A)
private val DarkCardElevated = Color(0xFF252525)
private val AccentBlue = Color(0xFF00B4D8)
private val AccentGreen = Color(0xFF00F5A0)
private val AccentRed = Color(0xFFFF6B6B)
private val AccentOrange = Color(0xFFFFAB40)
private val AccentYellow = Color(0xFFFFD93D)
private val AccentPurple = Color(0xFFBB86FC)
private val AccentCyan = Color(0xFF00E5FF)
private val TextPrimary = Color(0xFFE8E8E8)
private val TextSecondary = Color(0xFFA0A0A0)

@Composable
fun PerformanceScoreCard(
    score: Float,
    weeklyProgress: Float,
    monthlyProgress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Performance Score", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.Speed, null, tint = AccentBlue, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
                CircularProgressRing(score / 100f, getScoreColor(score), Modifier.fillMaxSize())
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${score.toInt()}", color = TextPrimary, fontSize = 42.sp, fontWeight = FontWeight.Bold)
                    Text(getScoreLabel(score), color = getScoreColor(score), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ProgressChip("Settimana", weeklyProgress)
                ProgressChip("Mese", monthlyProgress)
            }
        }
    }
}

@Composable
private fun CircularProgressRing(progress: Float, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 12.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)

        drawCircle(DarkCardElevated, radius, center, style = Stroke(strokeWidth))
        drawArc(color, -90f, 360 * progress, false,
            Offset(center.x - radius, center.y - radius),
            Size(radius * 2, radius * 2),
            style = Stroke(strokeWidth, cap = StrokeCap.Round))
    }
}

@Composable
private fun ProgressChip(label: String, value: Float) {
    val isPositive = value >= 0
    val color = if (isPositive) AccentGreen else AccentRed
    val icon = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text("${if (isPositive) "+" else ""}${value.toInt()}%", color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(4.dp))
        Text(label, color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
fun DigitalTwinCard(prediction: DigitalTwinPrediction?, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, tint = AccentPurple, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Gemello Digitale", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.Psychology, null, tint = AccentPurple.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
            }

            if (prediction == null) {
                Spacer(Modifier.height(20.dp))
                Text("Dati insufficienti", color = TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            } else {
                Spacer(Modifier.height(16.dp))

                Text("Potenziale Utilizzato", color = TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { prediction.currentPotentialUsage / 100f },
                    modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                    color = AccentPurple,
                    trackColor = DarkCardElevated
                )
                Text("${prediction.currentPotentialUsage.toInt()}%", color = TextPrimary, fontSize = 12.sp, modifier = Modifier.align(Alignment.End))

                Spacer(Modifier.height(16.dp))

                Text("Proiezione Forza", color = TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ProjectionBadge("Oggi", prediction.currentStrengthIndex, AccentBlue)
                    ProjectionBadge("1 Mese", prediction.projectedStrength1Month, AccentGreen)
                    ProjectionBadge("3 Mesi", prediction.projectedStrength3Months, AccentPurple)
                }

                if (prediction.injuryRiskScore > 40) {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(AccentOrange.copy(alpha = 0.15f)).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, null, tint = AccentOrange, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Rischio infortunio: ${prediction.injuryRiskScore.toInt()}%", color = AccentOrange, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectionBadge(label: String, value: Float, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.15f)).padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(String.format("%.1f", value), color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextSecondary, fontSize = 10.sp)
    }
}

@Composable
fun RecoveryCard(
    recoveryStatus: RecoveryStatus,
    overtrainingRisk: OvertrainingLevel,
    fatigueScore: Float,
    optimalRestDays: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BatteryChargingFull, null, tint = getRecoveryColor(recoveryStatus.level), modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Recupero", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                OvertrainingBadge(overtrainingRisk)
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { recoveryStatus.percentage / 100f },
                    modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(5.dp)),
                    color = getRecoveryColor(recoveryStatus.level),
                    trackColor = DarkCardElevated
                )
                Spacer(Modifier.width(12.dp))
                Text("${recoveryStatus.percentage.toInt()}%", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))
            Text(getRecoveryLabel(recoveryStatus.level), color = getRecoveryColor(recoveryStatus.level), fontSize = 14.sp, fontWeight = FontWeight.Medium)

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InfoChip(Icons.Default.LocalFireDepartment, "Fatica", "${(fatigueScore * 100).toInt()}%", getFatigueColor(fatigueScore))
                InfoChip(Icons.Default.Schedule, "Riposo", "$optimalRestDays giorni", AccentCyan)
            }
        }
    }
}

@Composable
private fun OvertrainingBadge(level: OvertrainingLevel) {
    val (color, text) = when (level) {
        OvertrainingLevel.LOW -> AccentGreen to "OK"
        OvertrainingLevel.MODERATE -> AccentYellow to "Attenzione"
        OvertrainingLevel.HIGH -> AccentOrange to "Alto"
        OvertrainingLevel.CRITICAL -> AccentRed to "Critico"
    }
    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.2f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
        Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun InfoChip(icon: ImageVector, label: String, value: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(DarkCardElevated).padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(label, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
fun OptimalLoadsCard(optimalLoads: Map<String, OptimalLoad>, modifier: Modifier = Modifier) {
    val items = optimalLoads.values
        .sortedWith(compareByDescending<OptimalLoad> { it.confidence }.thenByDescending { it.progressionPercent })
        .take(4)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TrendingUp, null, tint = AccentGreen, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Carichi consigliati", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            if (items.isEmpty()) {
                Text("Dati insufficienti per suggerire carichi", color = TextSecondary, fontSize = 14.sp)
            } else {
                items.forEachIndexed { index, load ->
                    OptimalLoadRow(load)
                    if (index != items.lastIndex) {
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun OptimalLoadRow(load: OptimalLoad) {
    val progression = load.progressionPercent
    val (icon, color) = when {
        progression >= 1f -> Icons.Default.TrendingUp to AccentGreen
        progression <= -1f -> Icons.Default.TrendingDown to AccentRed
        else -> Icons.Default.TrendingFlat to AccentYellow
    }

    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(DarkCardElevated).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(formatExerciseName(load.exerciseName), color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text("Conf ${(load.confidence * 100).toInt()}%", color = TextSecondary, fontSize = 11.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(formatWeight(load.currentWeight), color = TextSecondary, fontSize = 11.sp)
            Text(formatWeight(load.suggestedWeight), color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(String.format("%+.1f%%", progression), color = color, fontSize = 11.sp)
        }
    }
}

@Composable
fun MuscleBalanceCard(balance: MuscleBalance, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Balance, null, tint = AccentCyan, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Bilancio Muscolare", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Text("${balance.balanceScore.toInt()}%", color = getBalanceColor(balance.balanceScore), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            // Mostra lista muscoli se pochi dati, altrimenti radar chart
            if (balance.scores.size < 3) {
                // Lista semplice per pochi muscoli
                MuscleBalanceList(balance.scores)
            } else {
                // Radar chart con etichette
                Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                    RadarChartWithLabels(balance.scores)
                }
            }

            if (balance.imbalances.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                balance.imbalances.take(2).forEach { imbalance ->
                    ImbalanceWarning(imbalance.muscleGroup, imbalance.recommendation)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun MuscleBalanceList(scores: Map<String, Float>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        scores.entries.sortedByDescending { it.value }.forEach { (muscle, score) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatMuscleLabel(muscle),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { (score / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier.width(100.dp).height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = getBalanceColor(score),
                        trackColor = DarkCardElevated
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${score.toInt()}%",
                        color = getBalanceColor(score),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun RadarChartWithLabels(data: Map<String, Float>) {
    val entries = data.entries.toList()
    if (entries.isEmpty()) return

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Radar chart
        Canvas(modifier = Modifier.size(140.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = min(size.width, size.height) / 2 * 0.85f
            val angleStep = (2 * PI / entries.size).toFloat()

            // Grid circles
            for (level in 1..4) {
                val r = radius * level / 4
                val path = Path()
                for (i in entries.indices) {
                    val angle = -PI.toFloat() / 2 + i * angleStep
                    val x = center.x + r * cos(angle)
                    val y = center.y + r * sin(angle)
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(path, TextSecondary.copy(alpha = 0.2f), style = Stroke(1.dp.toPx()))
            }

            // Data path
            val dataPath = Path()
            for (i in entries.indices) {
                val value = (entries[i].value / 100f).coerceIn(0f, 1f)
                val angle = -PI.toFloat() / 2 + i * angleStep
                val x = center.x + radius * value * cos(angle)
                val y = center.y + radius * value * sin(angle)
                if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
            }
            dataPath.close()
            drawPath(dataPath, AccentCyan.copy(alpha = 0.3f))
            drawPath(dataPath, AccentCyan, style = Stroke(2.dp.toPx()))

            // Points
            for (i in entries.indices) {
                val value = (entries[i].value / 100f).coerceIn(0f, 1f)
                val angle = -PI.toFloat() / 2 + i * angleStep
                val x = center.x + radius * value * cos(angle)
                val y = center.y + radius * value * sin(angle)
                drawCircle(AccentCyan, 4.dp.toPx(), Offset(x, y))
            }
        }

        // Labels around the chart
        entries.forEachIndexed { i, (muscle, score) ->
            val angleStep = (2 * PI / entries.size).toFloat()
            val angle = -PI.toFloat() / 2 + i * angleStep
            val labelRadius = 95.dp

            val offsetX = (labelRadius.value * cos(angle)).dp
            val offsetY = (labelRadius.value * sin(angle)).dp

            Box(
                modifier = Modifier.offset(x = offsetX, y = offsetY),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatMuscleLabel(muscle).take(6),
                        color = TextPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${score.toInt()}%",
                        color = AccentCyan,
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}

private fun formatMuscleLabel(muscle: String): String {
    return when (muscle.lowercase()) {
        "petto", "chest" -> "Petto"
        "schiena", "back", "dorso" -> "Schiena"
        "gambe", "legs", "quadricipiti" -> "Gambe"
        "spalle", "shoulders", "deltoidi" -> "Spalle"
        "bicipiti", "biceps" -> "Bicipiti"
        "tricipiti", "triceps" -> "Tricipiti"
        "addominali", "abs", "core" -> "Addome"
        "glutei", "glutes" -> "Glutei"
        "polpacci", "calves" -> "Polpacci"
        else -> muscle.replaceFirstChar { it.uppercase() }.take(8)
    }
}

@Composable
private fun RadarChart(data: Map<String, Float>, modifier: Modifier = Modifier) {
    val entries = data.entries.toList()
    if (entries.isEmpty()) return

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = min(size.width, size.height) / 2 * 0.8f
        val angleStep = (2 * PI / entries.size).toFloat()

        // Grid
        for (level in 1..4) {
            val r = radius * level / 4
            val path = Path()
            for (i in entries.indices) {
                val angle = -PI.toFloat() / 2 + i * angleStep
                val x = center.x + r * cos(angle)
                val y = center.y + r * sin(angle)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path, TextSecondary.copy(alpha = 0.2f), style = Stroke(1.dp.toPx()))
        }

        // Data
        val dataPath = Path()
        for (i in entries.indices) {
            val value = entries[i].value / 100f
            val angle = -PI.toFloat() / 2 + i * angleStep
            val x = center.x + radius * value * cos(angle)
            val y = center.y + radius * value * sin(angle)
            if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
        }
        dataPath.close()
        drawPath(dataPath, AccentCyan.copy(alpha = 0.3f))
        drawPath(dataPath, AccentCyan, style = Stroke(2.dp.toPx()))
    }
}

@Composable
private fun ImbalanceWarning(muscle: String, recommendation: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(AccentOrange.copy(alpha = 0.1f)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Warning, null, tint = AccentOrange, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(muscle, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(recommendation, color = TextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
fun SuggestionsCard(suggestions: List<WorkoutSuggestion>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoGraph, null, tint = AccentGreen, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Suggerimenti AI", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            if (suggestions.isEmpty()) {
                Text("Nessun suggerimento. Continua ad allenarti!", color = TextSecondary, fontSize = 14.sp)
            } else {
                suggestions.take(4).forEach { suggestion ->
                    SuggestionItem(suggestion)
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(suggestion: WorkoutSuggestion) {
    val color = when (suggestion.priority) {
        SuggestionPriority.CRITICAL -> AccentRed
        SuggestionPriority.HIGH -> AccentOrange
        SuggestionPriority.MEDIUM -> AccentYellow
        SuggestionPriority.LOW -> AccentGreen
    }

    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(DarkCardElevated).padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(suggestion.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(suggestion.description, color = TextSecondary, fontSize = 12.sp)
            suggestion.actionText?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun VolumeIntensityCard(volumes: List<Float>, intensities: List<Float>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FitnessCenter, null, tint = AccentBlue, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Volume & Intensità", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                VolumeChart(volumes, intensities, Modifier.fillMaxSize())
            }

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                LegendItem(AccentBlue, "Volume")
                Spacer(Modifier.width(24.dp))
                LegendItem(AccentOrange, "Intensità")
            }
        }
    }
}

@Composable
private fun VolumeChart(volumes: List<Float>, intensities: List<Float>, modifier: Modifier) {
    if (volumes.isEmpty()) return
    val maxVolume = volumes.maxOrNull() ?: 1f

    Canvas(modifier = modifier) {
        val barWidth = size.width / (volumes.size * 2 + 1)
        val padding = barWidth / 2

        volumes.forEachIndexed { i, volume ->
            val h = (volume / maxVolume) * size.height * 0.8f
            val x = padding + i * barWidth * 2
            drawRoundRect(AccentBlue, Offset(x, size.height - h), Size(barWidth, h),
                androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()))
        }

        if (intensities.isNotEmpty()) {
            val path = Path()
            intensities.forEachIndexed { i, intensity ->
                val x = padding + barWidth / 2 + i * barWidth * 2
                val y = size.height - (intensity * size.height * 0.8f)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, AccentOrange, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(6.dp))
        Text(label, color = TextSecondary, fontSize = 12.sp)
    }
}

@Composable
fun SessionStatsCard(stats: SessionStats, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Assessment, null, tint = AccentYellow, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Statistiche Sessione", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("Allenamenti", "${stats.totalWorkouts}", AccentBlue)
                StatItem("Streak", "${stats.streakDays} giorni", AccentGreen)
                StatItem("Consistenza", "${stats.consistencyScore.toInt()}%", AccentPurple)
            }

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                stats.strongestMuscle?.let { StatItem("Più forte", it, AccentGreen) }
                stats.weakestMuscle?.let { StatItem("Da migliorare", it, AccentOrange) }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextSecondary, fontSize = 10.sp)
    }
}

// Helper functions
private fun getScoreColor(score: Float) = when {
    score >= 80 -> AccentGreen
    score >= 60 -> AccentBlue
    score >= 40 -> AccentYellow
    else -> AccentOrange
}

private fun getScoreLabel(score: Float) = when {
    score >= 80 -> "Eccellente"
    score >= 60 -> "Buono"
    score >= 40 -> "Discreto"
    else -> "Da Migliorare"
}

private fun getRecoveryColor(level: RecoveryLevel) = when (level) {
    RecoveryLevel.FULLY_RECOVERED -> AccentGreen
    RecoveryLevel.MOSTLY_RECOVERED -> AccentBlue
    RecoveryLevel.PARTIAL_RECOVERY -> AccentYellow
    RecoveryLevel.NEEDS_REST -> AccentRed
}

private fun getRecoveryLabel(level: RecoveryLevel) = when (level) {
    RecoveryLevel.FULLY_RECOVERED -> "Completamente recuperato"
    RecoveryLevel.MOSTLY_RECOVERED -> "Quasi pronto"
    RecoveryLevel.PARTIAL_RECOVERY -> "Recupero parziale"
    RecoveryLevel.NEEDS_REST -> "Riposo consigliato"
}

private fun getFatigueColor(fatigue: Float) = when {
    fatigue < 0.3f -> AccentGreen
    fatigue < 0.5f -> AccentYellow
    fatigue < 0.7f -> AccentOrange
    else -> AccentRed
}

private fun getBalanceColor(score: Float) = when {
    score >= 80 -> AccentGreen
    score >= 60 -> AccentBlue
    else -> AccentYellow
}

private fun formatWeight(value: Float): String {
    return if (value > 0f) String.format("%.1f kg", value) else "n/d"
}

private fun formatExerciseName(name: String): String {
    if (name.isBlank()) return name
    return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
