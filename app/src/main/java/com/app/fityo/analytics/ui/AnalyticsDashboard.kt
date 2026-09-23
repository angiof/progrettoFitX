package com.app.fityo.analytics.ui

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.fityo.analytics.AnalyticsUiState
import com.app.fityo.analytics.AnalyticsViewModel
import com.app.fityo.analytics.SessionStats
import com.app.fityo.analytics.WorkoutInsights
import com.app.fityo.dominio.GruppoMuscolarePercentuale
import com.app.fityo.ui.dashboard.compose.DashboardAccentBlue
import com.app.fityo.ui.dashboard.compose.DashboardBackgroundBrush
import com.app.fityo.ui.dashboard.compose.DashboardCardBorder
import com.app.fityo.ui.dashboard.compose.DashboardError
import com.app.fityo.ui.dashboard.compose.DashboardHeroBrush
import com.app.fityo.ui.dashboard.compose.DashboardTextMuted
import com.app.fityo.ui.dashboard.compose.DashboardTextPrimary
import com.app.fityo.ui.dashboard.compose.DashboardTextSecondary

@Composable
fun AnalyticsDashboard(
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.background(DashboardBackgroundBrush)) {
        when (val state = uiState) {
            is AnalyticsUiState.Loading -> LoadingContent()
            is AnalyticsUiState.Empty -> EmptyContent()
            is AnalyticsUiState.Error -> ErrorContent(state.message)
            is AnalyticsUiState.Success -> AnalyticsContent(
                insights = state.insights,
                volumes = state.volumes,
                intensities = state.intensities,
                sessionStats = state.sessionStats,
                muscleDistribution = state.muscleDistribution,
                profileName = state.profileName
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = DashboardAccentBlue, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Sto leggendo i tuoi allenamenti...",
                color = DashboardTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Analytics,
                null,
                tint = DashboardTextMuted,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Dati insufficienti",
                color = DashboardTextPrimary,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Completa qualche allenamento e qui compaiono i grafici.",
                color = DashboardTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorContent(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Errore: $message",
            color = DashboardError,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )
    }
}

@Composable
private fun AnalyticsContent(
    insights: WorkoutInsights,
    volumes: List<Float>,
    intensities: List<Float>,
    sessionStats: SessionStats,
    muscleDistribution: List<GruppoMuscolarePercentuale>,
    profileName: String?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { AnalyticsHero(sessionStats = sessionStats, profileName = profileName) }

        item {
            PerformanceGaugeCard(
                score = insights.performanceScore,
                weeklyProgress = insights.weeklyProgress,
                monthlyProgress = insights.monthlyProgress
            )
        }

        item { VolumeTrendCard(volumes = volumes) }

        item { IntensityBarsCard(intensities = intensities) }

        item { MuscleDistributionDonutCard(distribution = muscleDistribution) }

        item { MuscleBalanceRadarCard(balance = insights.muscleBalance) }

        item { RecoveryByMuscleCard(recoveryStatus = insights.recoveryStatus) }

        item {
            RecoveryCard(
                recoveryStatus = insights.recoveryStatus,
                overtrainingRisk = insights.overtrainingRisk,
                fatigueScore = insights.fatigueScore,
                optimalRestDays = insights.optimalRestDays
            )
        }

        item { OptimalLoadsCard(optimalLoads = insights.optimalLoads) }

        item { SuggestionsCard(suggestions = insights.suggestions) }

        item { ProjectionCard(prediction = insights.digitalTwin) }

        item { SessionStatsCard(stats = sessionStats) }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun AnalyticsHero(sessionStats: SessionStats, profileName: String?) {
    val workouts by animateIntAsState(
        targetValue = sessionStats.totalWorkouts,
        animationSpec = tween(durationMillis = 700),
        label = "workouts"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(DashboardHeroBrush)
            .border(1.dp, DashboardCardBorder, RoundedCornerShape(28.dp))
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = profileName ?: "Tutti i dati",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = DashboardAccentBlue,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = workouts.toString(),
                style = MaterialTheme.typography.displayMedium.copy(
                    color = DashboardTextPrimary,
                    fontWeight = FontWeight.Black
                )
            )
            Text(
                text = "Schede analizzate",
                style = MaterialTheme.typography.titleSmall.copy(color = DashboardTextSecondary)
            )
            Text(
                text = "Tutti i numeri qui sotto arrivano da query sul tuo database.",
                style = MaterialTheme.typography.bodySmall.copy(color = DashboardTextMuted)
            )
        }
    }
}
