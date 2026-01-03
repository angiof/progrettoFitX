package com.app.fityo.dominio

/**
 * Stati della modalità Tutor per la gestione del flusso UI.
 */
sealed class TutorState {
    /** Stato iniziale - visualizza storico sessioni */
    data object Idle : TutorState()

    /** Selezione del tipo di esercizio */
    data object SelectingExercise : TutorState()

    /** Selezione della sorgente video (registra o carica) */
    data class SelectingVideoSource(val exerciseType: ExerciseType) : TutorState()

    /** Registrazione video in corso */
    data class Recording(val exerciseType: ExerciseType) : TutorState()

    /** Analisi video in corso */
    data class Analyzing(
        val exerciseType: ExerciseType,
        val videoPath: String,
        val progress: Float = 0f,
        val currentFrame: Int = 0,
        val totalFrames: Int = 0
    ) : TutorState()

    /** Risultati pronti per la visualizzazione */
    data class ResultReady(
        val exerciseType: ExerciseType,
        val videoPath: String,
        val errors: List<ExerciseError>,
        val overallScore: Float,
        val duration: Long
    ) : TutorState()

    /** Playback del video con errori evidenziati */
    data class Playback(
        val sessionId: Int,
        val exerciseType: ExerciseType,
        val videoPath: String,
        val errors: List<ExerciseError>,
        val overallScore: Float
    ) : TutorState()

    /** Errore durante l'elaborazione */
    data class Error(val message: String) : TutorState()
}

/**
 * Stati dello storico delle sessioni Tutor.
 */
sealed class TutorHistoryState {
    data object Loading : TutorHistoryState()
    data object Empty : TutorHistoryState()
    data class Success(val sessions: List<TutorHistoryItem>) : TutorHistoryState()
    data class Error(val message: String) : TutorHistoryState()
}

/**
 * Item per la lista dello storico.
 */
data class TutorHistoryItem(
    val id: Int,
    val createdAt: Long,
    val exerciseType: ExerciseType,
    val thumbnailPath: String?,
    val duration: Long,
    val totalErrors: Int,
    val overallScore: Float
)
