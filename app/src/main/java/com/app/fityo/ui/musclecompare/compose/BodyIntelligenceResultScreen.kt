package com.app.fityo.ui.musclecompare.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.dominio.FfmiEvaluation
import com.app.fityo.mediapipe.BodyIntelligenceAnalyzer

/**
 * Schermata dei risultati Body Intelligence.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyIntelligenceResultScreen(
    result: BodyIntelligenceAnalyzer.AnalysisResult,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onBack: () -> Unit,
    onGenerateAvatar3D: (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Body Intelligence") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header con score complessivo
            OverallScoreCard(result.overallEvaluation, result.profile.discipline.displayName)

            Spacer(modifier = Modifier.height(16.dp))

            // Body Metrics Card
            BodyMetricsCard(result.bodyMetrics, result.profile.sex.displayName)

            Spacer(modifier = Modifier.height(16.dp))

            // Zone Analysis
            ZoneAnalysisCard(result.bodyZoneAnalysis)

            Spacer(modifier = Modifier.height(16.dp))

            // Punti di forza e aree da migliorare
            StrengthsAndImprovementsCard(result.overallEvaluation)

            Spacer(modifier = Modifier.height(16.dp))

            // Raccomandazioni
            if (result.recommendations.isNotEmpty()) {
                RecommendationsCard(result.recommendations)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Messaggio motivazionale
            MotivationalCard(result.overallEvaluation.motivationalMessage)

            Spacer(modifier = Modifier.height(24.dp))

            // Avatar 3D Button
            if (onGenerateAvatar3D != null) {
                Button(
                    onClick = onGenerateAvatar3D,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        Icons.Default.ViewInAr,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Genera Avatar 3D",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDiscard,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextSecondary
                    )
                ) {
                    Text("Scarta")
                }

                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGreen
                    )
                ) {
                    Text("Salva Analisi")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun OverallScoreCard(
    evaluation: BodyIntelligenceAnalyzer.OverallEvaluation,
    disciplineName: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AccentBlue.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Score circle
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    getScoreColor(evaluation.score).copy(alpha = 0.3f),
                                    getScoreColor(evaluation.score).copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${evaluation.score.toInt()}",
                            color = TextPrimary,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "/100",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = evaluation.category,
                    color = getScoreColor(evaluation.score),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Discipline match
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Match $disciplineName: ${evaluation.disciplineMatch.toInt()}%",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BodyMetricsCard(
    metrics: BodyIntelligenceAnalyzer.BodyMetrics,
    sexDisplay: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Analytics,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Metriche Corporee",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid di metriche
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    value = String.format("%.1f", metrics.bmi),
                    label = "BMI",
                    color = getBmiColor(metrics.bmi)
                )
                MetricItem(
                    value = String.format("%.1f%%", metrics.estimatedBodyFatPercent),
                    label = "Body Fat",
                    color = getBodyFatColor(metrics.estimatedBodyFatPercent)
                )
                MetricItem(
                    value = String.format("%.1f", metrics.ffmi),
                    label = "FFMI",
                    color = getFfmiColor(metrics.ffmiEvaluation)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Massa magra e grassa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    value = String.format("%.1f kg", metrics.leanMassKg),
                    label = "Massa Magra",
                    color = AccentGreen
                )
                MetricItem(
                    value = String.format("%.1f kg", metrics.fatMassKg),
                    label = "Massa Grassa",
                    color = AccentOrange
                )
            }

            // Rapporti corporei se disponibili
            if (metrics.shoulderToWaistRatio != null || metrics.waistToHipRatio != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    metrics.shoulderToWaistRatio?.let { ratio ->
                        MetricItem(
                            value = String.format("%.2f", ratio),
                            label = "Spalle/Vita",
                            color = if (ratio > 1.4f) AccentGreen else AccentOrange
                        )
                    }
                    metrics.waistToHipRatio?.let { ratio ->
                        MetricItem(
                            value = String.format("%.2f", ratio),
                            label = "Vita/Fianchi",
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // FFMI Evaluation badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(getFfmiColor(metrics.ffmiEvaluation).copy(alpha = 0.15f))
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = getFfmiColor(metrics.ffmiEvaluation),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FFMI: ${metrics.ffmiEvaluation.displayName}",
                        color = getFfmiColor(metrics.ffmiEvaluation),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ZoneAnalysisCard(zones: List<BodyIntelligenceAnalyzer.BodyZoneAnalysis>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Analisi Zone Corporee",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            zones.forEach { zone ->
                ZoneProgressItem(zone)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ZoneProgressItem(zone: BodyIntelligenceAnalyzer.BodyZoneAnalysis) {
    val color = Color(zone.evaluation.colorHex)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = zone.zone.displayName,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${zone.percentageScore.toInt()}%",
                    color = color,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { zone.percentageScore / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f),
            strokeCap = StrokeCap.Round
        )

        if (zone.suggestion != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = zone.suggestion,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun StrengthsAndImprovementsCard(evaluation: BodyIntelligenceAnalyzer.OverallEvaluation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Punti di forza
            if (evaluation.strengths.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Punti di Forza",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                evaluation.strengths.forEach { strength ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strength,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (evaluation.strengths.isNotEmpty() && evaluation.areasToImprove.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Aree da migliorare
            if (evaluation.areasToImprove.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = AccentOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Aree da Migliorare",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                evaluation.areasToImprove.forEach { area ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = AccentOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = area,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (evaluation.strengths.isEmpty() && evaluation.areasToImprove.isEmpty()) {
                Text(
                    "L'analisi non ha rilevato zone particolarmente forti o deboli. Continua con il tuo allenamento!",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun RecommendationsCard(recommendations: List<BodyIntelligenceAnalyzer.Recommendation>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = AccentYellow,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Raccomandazioni",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            recommendations.forEachIndexed { index, recommendation ->
                RecommendationItem(recommendation)
                if (index < recommendations.lastIndex) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun RecommendationItem(recommendation: BodyIntelligenceAnalyzer.Recommendation) {
    val priorityColor = when (recommendation.priority) {
        1 -> AccentRed
        2 -> AccentOrange
        else -> AccentBlue
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .padding(12.dp)
    ) {
        Row {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(priorityColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = recommendation.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = recommendation.description,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun MotivationalCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AccentBlue.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = AccentYellow,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

// Helper functions for colors
private fun getScoreColor(score: Float): Color = when {
    score >= 80 -> AccentGreen
    score >= 60 -> Color(0xFF8BC34A)
    score >= 40 -> AccentYellow
    score >= 20 -> AccentOrange
    else -> AccentRed
}

private fun getBmiColor(bmi: Float): Color = when {
    bmi in 18.5f..24.9f -> AccentGreen
    bmi in 17f..18.5f || bmi in 25f..29.9f -> AccentYellow
    else -> AccentOrange
}

private fun getBodyFatColor(bf: Float): Color = when {
    bf <= 15f -> AccentGreen
    bf <= 20f -> Color(0xFF8BC34A)
    bf <= 25f -> AccentYellow
    bf <= 30f -> AccentOrange
    else -> AccentRed
}

private fun getFfmiColor(evaluation: FfmiEvaluation): Color = when (evaluation) {
    FfmiEvaluation.ELITE -> Color(0xFF9C27B0) // Purple
    FfmiEvaluation.EXCELLENT -> AccentBlue
    FfmiEvaluation.OPTIMAL -> AccentGreen
    FfmiEvaluation.DEVELOPING -> Color(0xFFFF9800) // Orange
    FfmiEvaluation.BELOW_AVERAGE -> Color(0xFFFF5722) // Deep Orange
}
