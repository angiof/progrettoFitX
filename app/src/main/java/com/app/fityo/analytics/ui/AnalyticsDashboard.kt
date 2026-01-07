package com.app.fityo.analytics.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.analytics.AnalyticsUiState
import com.app.fityo.analytics.AnalyticsViewModel
import com.app.fityo.analytics.GemmaInsightsState
import com.app.fityo.analytics.GemmaLiveInsights
import com.app.fityo.analytics.WorkoutInsights
import com.app.fityo.analytics.SessionStats
import com.app.fityo.import_scheda.GemmaEngineType

private val DarkBackground = Color(0xFF0D0D0D)
private val AccentBlue = Color(0xFF00B4D8)
private val TextPrimary = Color(0xFFE8E8E8)
private val TextSecondary = Color(0xFFA0A0A0)

@Composable
fun AnalyticsDashboard(
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentEngine by viewModel.currentEngineType.collectAsState()

    when (val state = uiState) {
        is AnalyticsUiState.Loading -> LoadingContent()
        is AnalyticsUiState.Empty -> EmptyContent()
        is AnalyticsUiState.Error -> ErrorContent(state.message)
        is AnalyticsUiState.Success -> AnalyticsContent(
            insights = state.insights,
            volumes = state.volumes,
            intensities = state.intensities,
            sessionStats = state.sessionStats,
            gemmaInsights = state.gemmaInsights,
            gemmaState = state.gemmaState,
            currentEngine = currentEngine,
            onRefreshGemma = { viewModel.refreshGemmaInsights() },
            onEngineClick = { /* TODO: show engine dialog */ },
            modifier = modifier
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = AccentBlue, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(16.dp))
            Text("Analizzando i tuoi allenamenti...", color = TextSecondary, fontSize = 14.sp)
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Default.Analytics, null, tint = TextSecondary, modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text("Dati insufficienti", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Completa alcuni allenamenti per vedere le tue analytics", color = TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ErrorContent(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Errore: $message", color = TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(32.dp))
    }
}

@Composable
private fun AnalyticsContent(
    insights: WorkoutInsights,
    volumes: List<Float>,
    intensities: List<Float>,
    sessionStats: SessionStats,
    gemmaInsights: GemmaLiveInsights?,
    gemmaState: GemmaInsightsState,
    currentEngine: GemmaEngineType,
    onRefreshGemma: () -> Unit,
    onEngineClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Dashboard Analytics", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Analisi intelligente dei tuoi allenamenti", color = TextSecondary, fontSize = 14.sp)
        }

        // ========== CARDS GEMMA AI ==========

        // Card motivazionale con AI
        item {
            when (gemmaState) {
                is GemmaInsightsState.NotAvailable -> GemmaNotAvailableCard()
                else -> GemmaWeeklyMotivationCard(
                    insights = gemmaInsights,
                    isLoading = gemmaState is GemmaInsightsState.Loading,
                    engineType = currentEngine,
                    onRefresh = onRefreshGemma,
                    onEngineClick = onEngineClick
                )
            }
        }

        // Stats settimanali veloci
        gemmaInsights?.weeklyStats?.let { stats ->
            item { WeeklyStatsQuickCard(stats) }
        }

        // Prossimo allenamento consigliato
        gemmaInsights?.nextWorkoutFocus?.let { focus ->
            item { NextWorkoutFocusCard(focus) }
        }

        // Suggerimenti AI
        gemmaInsights?.suggestions?.let { suggestions ->
            if (suggestions.isNotEmpty()) {
                item { GemmaLiveSuggestionsCard(suggestions) }
            }
        }

        // Raccomandazione carichi
        item { LoadRecommendationCard(gemmaInsights?.loadRecommendation) }

        // Raccomandazione riposo
        item { RestRecommendationCard(gemmaInsights?.restRecommendation) }

        // Deload warning
        item {
            DeloadWarningCard(
                deloadNeeded = gemmaInsights?.deloadNeeded ?: false,
                deloadReason = gemmaInsights?.deloadReason
            )
        }

        // ========== CARDS ESISTENTI ==========

        item { PerformanceScoreCard(insights.performanceScore, insights.weeklyProgress, insights.monthlyProgress) }
        item { SessionStatsCard(sessionStats) }
        item { DigitalTwinCard(insights.digitalTwin) }
        item { RecoveryCard(insights.recoveryStatus, insights.overtrainingRisk, insights.fatigueScore, insights.optimalRestDays) }
        item { OptimalLoadsCard(insights.optimalLoads) }
        item { VolumeIntensityCard(volumes, intensities) }
        item { MuscleBalanceCard(insights.muscleBalance) }
        item { SuggestionsCard(insights.suggestions) }
        item { Spacer(Modifier.height(80.dp)) }
    }
}
