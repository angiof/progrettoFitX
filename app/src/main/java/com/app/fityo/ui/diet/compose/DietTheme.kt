package com.app.fityo.ui.diet.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colori base - Consistenti con il tema dell'app
val DarkBackground = Color(0xFF121212)
val DarkCard = Color(0xFF1E1E1E)
val DarkSurface = Color(0xFF2C2C2C)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB0B0B0)

// Colori Diet-specific
val AccentPurple = Color(0xFF9C27B0)
val AccentGreen = Color(0xFF4CAF50)
val AccentRed = Color(0xFFF44336)
val AccentOrange = Color(0xFFFF9800)
val AccentBlue = Color(0xFF2196F3)

// Macro colors - Principali
val ProteinColor = Color(0xFFE91E63)      // Rosa/Rosso per proteine
val CarbsColor = Color(0xFFFFEB3B)        // Giallo per carboidrati
val FatsColor = Color(0xFFFF9800)         // Arancione per grassi
val KcalColor = Color(0xFF4CAF50)         // Verde per calorie

// Macro colors - Secondari
val FibersColor = Color(0xFF8BC34A)       // Verde chiaro per fibre
val SugarsColor = Color(0xFFE040FB)       // Viola per zuccheri
val SaturatedFatsColor = Color(0xFFFF7043) // Arancione scuro per grassi saturi
val SaltColor = Color(0xFF90CAF9)         // Azzurro per sale

// Card colors
val ProductCardColor = Color(0xFF1E1E1E)
val MacroCardColor = Color(0xFF2C2C2C)
val HistoryItemColor = Color(0xFF252525)
val AdviceCardColor = Color(0xFF1A237E)   // Blu scuro per consiglio Gemma

// Button colors
val PrimaryButtonColor = Color(0xFF9C27B0)
val SecondaryButtonColor = Color(0xFF424242)
val ScanButtonColor = Color(0xFF00BCD4)   // Cyan per scanner
val SearchButtonColor = Color(0xFF2196F3) // Blu per ricerca
val OcrButtonColor = Color(0xFFFF5722)    // Arancione scuro per OCR

// Input colors
val InputBackgroundColor = Color(0xFF1E1E1E)
val InputBorderColor = Color(0xFF424242)
val InputFocusedBorderColor = Color(0xFF9C27B0)

// Shimmer
val ShimmerBaseColor = Color(0xFF2C2C2C)
val ShimmerHighlightColor = Color(0xFF424242)

private val DarkColorScheme = darkColorScheme(
    primary = AccentPurple,
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
fun DietTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
