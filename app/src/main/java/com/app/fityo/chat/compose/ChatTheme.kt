package com.app.fityo.chat.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colori Chat - Consistenti con il tema dell'app
val DarkBackground = Color(0xFF121212)
val DarkCard = Color(0xFF1E1E1E)
val DarkSurface = Color(0xFF2C2C2C)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB0B0B0)

// Chat-specific colors
val UserBubbleColor = Color(0xFF9C27B0)      // AccentPurple per messaggi utente
val BotBubbleColor = Color(0xFF2C2C2C)       // Superficie scura per messaggi bot
val SuggestionChipColor = Color(0xFF1E1E1E)
val InputBackgroundColor = Color(0xFF1E1E1E)
val SendButtonColor = Color(0xFF9C27B0)
val SendButtonDisabledColor = Color(0xFF666666)

// Status colors
val AccentGreen = Color(0xFF4CAF50)
val AccentRed = Color(0xFFF44336)
val AccentOrange = Color(0xFFFF9800)
val AccentBlue = Color(0xFF2196F3)

// Loading indicator
val LoadingDotColor = Color(0xFF9C27B0)

private val DarkColorScheme = darkColorScheme(
    primary = UserBubbleColor,
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
fun ChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
