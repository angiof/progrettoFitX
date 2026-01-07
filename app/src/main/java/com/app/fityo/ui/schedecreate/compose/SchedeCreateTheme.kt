package com.app.fityo.ui.schedecreate.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colori Schede Create Mode - Stessi del Tutor per consistenza
val DarkBackground = Color(0xFF121212)
val DarkCard = Color(0xFF1E1E1E)
val DarkSurface = Color(0xFF2C2C2C)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB0B0B0)

// Accent colors
val AccentBlue = Color(0xFF2196F3)
val AccentGreen = Color(0xFF4CAF50)
val AccentRed = Color(0xFFF44336)
val AccentOrange = Color(0xFFFF9800)
val AccentYellow = Color(0xFFFFEB3B)
val AccentPurple = Color(0xFF9C27B0)
val AccentCyan = Color(0xFF00BCD4)

// Step colors
val StepActive = AccentBlue
val StepCompleted = AccentGreen
val StepPending = TextSecondary

// Intensity colors
val IntensityHigh = AccentRed
val IntensityMedium = AccentOrange
val IntensityLow = AccentGreen
val IntensityCardio = AccentCyan

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    secondary = AccentGreen,
    tertiary = AccentOrange,
    background = DarkBackground,
    surface = DarkCard,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = AccentRed
)

@Composable
fun SchedeCreateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}

/**
 * Restituisce il colore per un livello di intensita.
 */
fun getIntensityColor(intensity: String): Color {
    return when {
        intensity.contains("Alta", ignoreCase = true) -> IntensityHigh
        intensity.contains("Media", ignoreCase = true) -> IntensityMedium
        intensity.contains("Bassa", ignoreCase = true) -> IntensityLow
        intensity.contains("Cardio", ignoreCase = true) -> IntensityCardio
        else -> TextSecondary
    }
}

/**
 * Restituisce il colore per un gruppo muscolare.
 */
fun getMuscleGroupColor(group: String): Color {
    return when (group.lowercase()) {
        "dorsali" -> Color(0xFF3F51B5)      // Indigo
        "petorali" -> Color(0xFFE91E63)     // Pink
        "gambe" -> Color(0xFF4CAF50)        // Green
        "spalle" -> Color(0xFFFF9800)       // Orange
        "bicipiti" -> Color(0xFF2196F3)     // Blue
        "tricipiti" -> Color(0xFF9C27B0)    // Purple
        "addominali" -> Color(0xFFFFEB3B)   // Yellow
        "cardio" -> Color(0xFF00BCD4)       // Cyan
        "full body" -> Color(0xFFF44336)    // Red
        else -> AccentBlue
    }
}
