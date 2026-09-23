package com.app.fityo.ui.dashboard.compose

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Dark theme colors consistent with app design
val DashboardBackground = Color(0xFF0C1420)
val DashboardSurface = Color(0xFF1A222A)
val DashboardCard = Color(0xFF172332)
val DashboardAccentBlue = Color(0xFF40C4FF)
val DashboardAccentGreen = Color(0xFF65D6AF)
val DashboardAccentPurple = Color(0xFF9C27B0)
val DashboardTextPrimary = Color(0xFFECF0F1)
val DashboardTextSecondary = Color(0xFFB0BEC5)
val DashboardTextMuted = Color(0xFF7A8CA0)
val DashboardError = Color(0xFFFF6F61)

/** Linee di griglia e assi: visibili sul fondo scuro senza rubare scena ai dati. */
val DashboardGrid = Color(0x33B0BEC5)
val DashboardTooltip = Color(0xFF223245)

/** Sfondo della schermata: un blu che si scurisce scendendo, non una tinta piatta. */
val DashboardBackgroundBrush = Brush.verticalGradient(
    listOf(Color(0xFF101E30), Color(0xFF0C1420), Color(0xFF080E16))
)

/** Superficie delle card, con un accenno di luce in alto a sinistra. */
val DashboardCardBrush = Brush.linearGradient(
    listOf(Color(0xFF1B2A3C), Color(0xFF141F2D))
)

val DashboardHeroBrush = Brush.linearGradient(
    listOf(Color(0xFF1D3B63), Color(0xFF16324F), Color(0xFF122A43))
)

val DashboardCardBorder = Color(0x1FFFFFFF)

/**
 * Coppie di colori per i grafici: ogni serie prende un gradiente, non un colore
 * piatto, cosi le barre e gli spicchi restano distinguibili anche in piccolo.
 */
val DashboardChartGradients: List<List<Color>> = listOf(
    listOf(Color(0xFF00C9FF), Color(0xFF4F7BFF)),
    listOf(Color(0xFF65D6AF), Color(0xFF16A085)),
    listOf(Color(0xFFB06AF7), Color(0xFF7A5CFF)),
    listOf(Color(0xFFFFC371), Color(0xFFFF7E5F)),
    listOf(Color(0xFFFF6FA5), Color(0xFFE94057)),
    listOf(Color(0xFF4DD0E1), Color(0xFF2F80ED)),
    listOf(Color(0xFFA8E063), Color(0xFF56AB2F)),
    listOf(Color(0xFFF7C948), Color(0xFFF0932B))
)

/** Il primo colore di ogni gradiente, per legende e pallini. */
val DashboardChartColors: List<Color> = DashboardChartGradients.map { it.first() }
