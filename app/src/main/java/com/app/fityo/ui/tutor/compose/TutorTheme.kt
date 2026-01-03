package com.app.fityo.ui.tutor.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colori Tutor Mode
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

// Exercise type colors
val SquatColor = Color(0xFF2196F3)      // Blue
val LungesColor = Color(0xFF9C27B0)     // Purple
val DeadliftColor = Color(0xFFFF9800)   // Orange
val BenchPressColor = Color(0xFFE91E63) // Pink
val DeclineBenchColor = Color(0xFF00BCD4) // Cyan
val ChainBenchColor = Color(0xFF795548) // Brown
val ChaosPressColor = Color(0xFF607D8B) // Blue Grey

// Score colors
val ScoreExcellent = Color(0xFF4CAF50)  // 90-100
val ScoreGood = Color(0xFF8BC34A)       // 75-89
val ScoreFair = Color(0xFFFFEB3B)       // 60-74
val ScorePoor = Color(0xFFFF9800)       // 40-59
val ScoreBad = Color(0xFFF44336)        // 0-39

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
fun TutorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}

/**
 * Restituisce il colore appropriato in base al punteggio.
 */
fun getScoreColor(score: Float): Color {
    return when {
        score >= 90f -> ScoreExcellent
        score >= 75f -> ScoreGood
        score >= 60f -> ScoreFair
        score >= 40f -> ScorePoor
        else -> ScoreBad
    }
}

/**
 * Restituisce il colore per un tipo di esercizio.
 */
fun getExerciseColor(exerciseType: com.app.fityo.dominio.ExerciseType): Color {
    return when (exerciseType) {
        com.app.fityo.dominio.ExerciseType.SQUAT -> SquatColor
        com.app.fityo.dominio.ExerciseType.LUNGES -> LungesColor
        com.app.fityo.dominio.ExerciseType.DEADLIFT -> DeadliftColor
        com.app.fityo.dominio.ExerciseType.BENCH_PRESS -> BenchPressColor
        com.app.fityo.dominio.ExerciseType.DECLINE_BENCH_PRESS -> DeclineBenchColor
        com.app.fityo.dominio.ExerciseType.CHAIN_BENCH_PRESS -> ChainBenchColor
        com.app.fityo.dominio.ExerciseType.CHAOS_PRESS -> ChaosPressColor
    }
}
