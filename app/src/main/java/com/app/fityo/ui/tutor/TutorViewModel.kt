package com.app.fityo.ui.tutor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.fityo.data_layer.db.TutorSessionEntity
import com.app.fityo.data_layer.repository.TutorRepository
import com.app.fityo.dominio.ExerciseError
import com.app.fityo.dominio.ExerciseType
import com.app.fityo.dominio.TutorHistoryState
import com.app.fityo.dominio.TutorState
import com.app.fityo.tutor.analysis.VideoAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class TutorViewModel(
    application: Application,
    private val repository: TutorRepository
) : AndroidViewModel(application) {

    // State flows
    private val _tutorState = MutableStateFlow<TutorState>(TutorState.Idle)
    val tutorState: StateFlow<TutorState> = _tutorState.asStateFlow()

    private val _historyState = MutableStateFlow<TutorHistoryState>(TutorHistoryState.Loading)
    val historyState: StateFlow<TutorHistoryState> = _historyState.asStateFlow()

    // TTS enabled state
    private val _ttsEnabled = MutableStateFlow(true)
    val ttsEnabled: StateFlow<Boolean> = _ttsEnabled.asStateFlow()

    // Current playback position for error highlighting
    private val _currentPlaybackPosition = MutableStateFlow(0L)
    val currentPlaybackPosition: StateFlow<Long> = _currentPlaybackPosition.asStateFlow()

    // Video analyzer
    private var videoAnalyzer: VideoAnalyzer? = null
    private var isAnalyzerInitialized = false

    init {
        loadHistory()
        initializeAnalyzer()
    }

    private fun initializeAnalyzer() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                videoAnalyzer = VideoAnalyzer(getApplication())
                videoAnalyzer?.initialize()
                isAnalyzerInitialized = true
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _tutorState.value = TutorState.Error("Errore inizializzazione: ${e.message}")
                }
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _historyState.value = TutorHistoryState.Loading
            try {
                repository.getAllSessionsAsHistoryItems().collectLatest { items ->
                    _historyState.value = if (items.isEmpty()) {
                        TutorHistoryState.Empty
                    } else {
                        TutorHistoryState.Success(items)
                    }
                }
            } catch (e: Exception) {
                _historyState.value = TutorHistoryState.Error(e.message ?: "Errore sconosciuto")
            }
        }
    }

    /**
     * Inizia una nuova analisi - mostra selezione esercizio.
     */
    fun startNewAnalysis() {
        _tutorState.value = TutorState.SelectingExercise
    }

    /**
     * Seleziona il tipo di esercizio.
     */
    fun selectExercise(exerciseType: ExerciseType) {
        _tutorState.value = TutorState.SelectingVideoSource(exerciseType)
    }

    /**
     * Inizia la registrazione video.
     */
    fun startRecording(exerciseType: ExerciseType) {
        _tutorState.value = TutorState.Recording(exerciseType)
    }

    /**
     * Analizza un video (da registrazione o galleria).
     */
    fun analyzeVideo(exerciseType: ExerciseType, videoPath: String) {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                _tutorState.value = TutorState.Analyzing(
                    exerciseType = exerciseType,
                    videoPath = videoPath,
                    progress = 0f,
                    currentFrame = 0,
                    totalFrames = 0
                )
            }

            try {
                // Attendi inizializzazione se necessario
                if (!isAnalyzerInitialized) {
                    videoAnalyzer = VideoAnalyzer(getApplication())
                    videoAnalyzer?.initialize()
                    isAnalyzerInitialized = true
                }

                val analyzer = videoAnalyzer ?: throw Exception("Analizzatore non disponibile")

                // Esegui l'analisi reale
                val result = analyzer.analyzeVideoSync(
                    videoPath = videoPath,
                    exerciseType = exerciseType,
                    onProgress = { progress, currentFrame, totalFrames ->
                        _tutorState.value = TutorState.Analyzing(
                            exerciseType = exerciseType,
                            videoPath = videoPath,
                            progress = progress,
                            currentFrame = currentFrame,
                            totalFrames = totalFrames
                        )
                    }
                )

                // Mostra risultati
                withContext(Dispatchers.Main) {
                    if (result.framesWithPose == 0) {
                        _tutorState.value = TutorState.Error(
                            "Nessuna posa rilevata nel video. Assicurati che il corpo sia completamente visibile."
                        )
                    } else {
                        _tutorState.value = TutorState.ResultReady(
                            exerciseType = result.exerciseType,
                            videoPath = videoPath,
                            errors = result.errors,
                            overallScore = result.score,
                            duration = result.durationMs
                        )
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _tutorState.value = TutorState.Error("Errore durante l'analisi: ${e.message}")
                }
            }
        }
    }

    /**
     * Aggiorna il progresso dell'analisi.
     */
    fun updateAnalysisProgress(progress: Float, currentFrame: Int, totalFrames: Int) {
        val currentState = _tutorState.value
        if (currentState is TutorState.Analyzing) {
            _tutorState.value = currentState.copy(
                progress = progress,
                currentFrame = currentFrame,
                totalFrames = totalFrames
            )
        }
    }

    /**
     * Salva il risultato dell'analisi.
     */
    fun saveAnalysisResult(
        exerciseType: ExerciseType,
        videoPath: String,
        errors: List<ExerciseError>,
        overallScore: Float,
        duration: Long,
        thumbnailPath: String? = null
    ) {
        viewModelScope.launch {
            try {
                val entity = TutorSessionEntity(
                    createdAt = System.currentTimeMillis(),
                    exerciseType = exerciseType.name,
                    videoPath = videoPath,
                    thumbnailPath = thumbnailPath,
                    duration = duration,
                    totalErrors = errors.size,
                    overallScore = overallScore,
                    errorsJson = repository.errorsToJson(errors)
                )
                repository.insert(entity)
                resetToIdle()
                loadHistory()
            } catch (e: Exception) {
                _tutorState.value = TutorState.Error("Errore nel salvataggio: ${e.message}")
            }
        }
    }

    /**
     * Apre una sessione salvata per il playback.
     */
    fun openSession(sessionId: Int) {
        viewModelScope.launch {
            try {
                val session = repository.getById(sessionId)
                if (session != null) {
                    val errors = repository.jsonToErrors(session.errorsJson)
                    _tutorState.value = TutorState.Playback(
                        sessionId = session.id ?: 0,
                        exerciseType = ExerciseType.valueOf(session.exerciseType),
                        videoPath = session.videoPath,
                        errors = errors,
                        overallScore = session.overallScore
                    )
                } else {
                    _tutorState.value = TutorState.Error("Sessione non trovata")
                }
            } catch (e: Exception) {
                _tutorState.value = TutorState.Error("Errore: ${e.message}")
            }
        }
    }

    /**
     * Elimina una sessione.
     */
    fun deleteSession(id: Int) {
        viewModelScope.launch {
            try {
                val session = repository.getById(id)
                session?.let {
                    // Elimina il file video
                    try {
                        val videoFile = File(it.videoPath)
                        if (videoFile.exists()) {
                            videoFile.delete()
                        }
                        // Elimina thumbnail se presente
                        it.thumbnailPath?.let { thumbPath ->
                            val thumbFile = File(thumbPath)
                            if (thumbFile.exists()) {
                                thumbFile.delete()
                            }
                        }
                    } catch (e: Exception) {
                        // Ignora errori di eliminazione file
                    }
                }
                repository.deleteById(id)
            } catch (e: Exception) {
                _historyState.value = TutorHistoryState.Error("Errore nell'eliminazione: ${e.message}")
            }
        }
    }

    /**
     * Aggiorna la posizione di playback corrente.
     */
    fun updatePlaybackPosition(positionMs: Long) {
        _currentPlaybackPosition.value = positionMs
    }

    /**
     * Abilita/disabilita TTS.
     */
    fun toggleTts() {
        _ttsEnabled.value = !_ttsEnabled.value
    }

    /**
     * Torna allo stato iniziale.
     */
    fun resetToIdle() {
        _tutorState.value = TutorState.Idle
    }

    /**
     * Gestisce il back button.
     */
    fun onBackPressed(): Boolean {
        return when (_tutorState.value) {
            is TutorState.Idle -> false // Non gestito, chiude l'activity
            is TutorState.SelectingExercise -> {
                resetToIdle()
                true
            }
            is TutorState.SelectingVideoSource -> {
                _tutorState.value = TutorState.SelectingExercise
                true
            }
            is TutorState.Recording -> {
                val state = _tutorState.value as TutorState.Recording
                _tutorState.value = TutorState.SelectingVideoSource(state.exerciseType)
                true
            }
            is TutorState.ResultReady -> {
                resetToIdle()
                true
            }
            is TutorState.Playback -> {
                resetToIdle()
                true
            }
            is TutorState.Error -> {
                resetToIdle()
                true
            }
            else -> {
                resetToIdle()
                true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        videoAnalyzer?.close()
        videoAnalyzer = null
    }
}
