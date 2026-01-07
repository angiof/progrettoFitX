package com.app.fityo.ui.coach.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colori tema scuro per Coach Mode
val DarkBackground = Color(0xFF10161B)
val DarkSurface = Color(0xFF1A222A)
val DarkCard = Color(0xFF222C35)
val TextPrimary = Color(0xFFECF0F1)
val TextSecondary = Color(0xFFB0BEC5)
val AccentPurple = Color(0xFF9C27B0)
val AccentGreen = Color(0xFF4CAF50)
val AccentRed = Color(0xFFF44336)
val AccentBlue = Color(0xFF2196F3)
val AccentOrange = Color(0xFFFF9800)

// Palette colori avatar
val AvatarColors = listOf(
    Color(0xFF2196F3),  // Blue
    Color(0xFF4CAF50),  // Green
    Color(0xFFF44336),  // Red
    Color(0xFF9C27B0),  // Purple
    Color(0xFFFF9800),  // Orange
    Color(0xFF00BCD4),  // Cyan
    Color(0xFFE91E63),  // Pink
    Color(0xFF795548)   // Brown
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentPurple,
    secondary = AccentGreen,
    tertiary = AccentOrange,
    background = DarkBackground,
    surface = DarkSurface,
    error = AccentRed,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onError = TextPrimary
)

@Composable
fun CoachTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
