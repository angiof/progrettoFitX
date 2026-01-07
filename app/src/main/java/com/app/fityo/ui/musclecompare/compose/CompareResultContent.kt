package com.app.fityo.ui.musclecompare.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.dominio.CompareResult
import com.app.fityo.dominio.DistrictResult
import com.app.fityo.dominio.WorkoutAnalysis
import kotlin.math.abs

@Composable
fun CompareResultContent(
    result: CompareResult,
    workoutAnalysis: WorkoutAnalysis? = null,
    showPixelDetails: Boolean = true
) {
    ResultHeader(result)

    Spacer(modifier = Modifier.height(24.dp))

    DistrictResultCard(
        title = "Braccia",
        icon = Icons.Default.FitnessCenter,
        result = result.armsResult,
        showPixelDetails = showPixelDetails
    )

    Spacer(modifier = Modifier.height(12.dp))

    DistrictResultCard(
        title = "Addominali",
        icon = Icons.Default.FitnessCenter,
        result = result.absResult,
        showPixelDetails = showPixelDetails
    )

    Spacer(modifier = Modifier.height(12.dp))

    DistrictResultCard(
        title = "Gambe",
        icon = Icons.Default.FitnessCenter,
        result = result.legsResult,
        showPixelDetails = showPixelDetails
    )

    Spacer(modifier = Modifier.height(12.dp))

    DistrictResultCard(
        title = "Glutei",
        icon = Icons.Default.FitnessCenter,
        result = result.glutesResult,
        showPixelDetails = showPixelDetails
    )

    if (workoutAnalysis != null) {
        Spacer(modifier = Modifier.height(24.dp))
        WorkoutAnalysisCard(
            analysis = workoutAnalysis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ResultHeader(result: CompareResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (result.notableCount > 0) AccentGreen.copy(alpha = 0.2f) else DarkCard
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (result.notableCount > 0) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Risultato Notevole!",
                    color = AccentGreen,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${result.notableCount} distretti con variazioni significative",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            } else {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Analisi Completata",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Continua ad allenarti per vedere progressi",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Media",
                    value = String.format("%.1f%%", result.averageVariation)
                )
                StatItem(
                    label = "Scala A",
                    value = String.format("%.2fx", result.scaleFactorA)
                )
                StatItem(
                    label = "Scala B",
                    value = String.format("%.2fx", result.scaleFactorB)
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun DistrictResultCard(
    title: String,
    icon: ImageVector,
    result: DistrictResult,
    showPixelDetails: Boolean
) {
    val isPositive = result.isIncrease
    val color = when {
        result.isNotable && isPositive -> AccentGreen
        result.isNotable && !isPositive -> AccentRed
        isPositive -> AccentGreen.copy(alpha = 0.6f)
        else -> AccentRed.copy(alpha = 0.6f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
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
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (showPixelDetails) {
                    Text(
                        "Da ${result.pixelsA} a ${result.pixelsB} pixel",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                if (result.isNotable) {
                    Text(
                        "Risultato notevole!",
                        color = AccentGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        String.format("%.1f%%", abs(result.variationPercent)),
                        color = color,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    if (isPositive) "Aumento" else "Diminuzione",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
