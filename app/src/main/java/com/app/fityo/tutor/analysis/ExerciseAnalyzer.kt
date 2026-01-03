package com.app.fityo.tutor.analysis

import com.app.fityo.dominio.ExerciseError
import com.app.fityo.dominio.ExerciseType
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

/**
 * Interfaccia per gli analizzatori di esercizi.
 * Ogni esercizio ha il proprio analizzatore che implementa questa interfaccia.
 */
interface ExerciseAnalyzer {

    /**
     * Il tipo di esercizio che questo analizzatore gestisce.
     */
    val exerciseType: ExerciseType

    /**
     * Analizza un singolo frame e restituisce eventuali errori rilevati.
     *
     * @param result Il risultato del PoseLandmarker per questo frame
     * @param timestampMs Il timestamp del frame in millisecondi
     * @param previousResults I risultati dei frame precedenti (per rilevare pattern)
     * @return Lista di errori rilevati in questo frame
     */
    fun analyzeFrame(
        result: PoseLandmarkerResult,
        timestampMs: Long,
        previousResults: List<FrameAnalysisResult>
    ): List<ExerciseError>

    /**
     * Finalizza l'analisi e aggrega gli errori.
     * Chiamato dopo l'analisi di tutti i frame.
     *
     * @param allErrors Tutti gli errori rilevati durante l'analisi
     * @return Lista di errori aggregati e filtrati
     */
    fun finalizeAnalysis(allErrors: List<ExerciseError>): List<ExerciseError>

    /**
     * Calcola il punteggio complessivo basato sugli errori.
     *
     * @param errors Lista di errori dopo finalizeAnalysis
     * @param totalFrames Numero totale di frame analizzati
     * @return Punteggio da 0 a 100
     */
    fun calculateScore(errors: List<ExerciseError>, totalFrames: Int): Float
}

/**
 * Risultato dell'analisi di un singolo frame.
 */
data class FrameAnalysisResult(
    val timestampMs: Long,
    val result: PoseLandmarkerResult,
    val errors: List<ExerciseError>,
    val metrics: FrameMetrics
)

/**
 * Metriche estratte da un frame.
 */
data class FrameMetrics(
    val leftKneeAngle: Float? = null,
    val rightKneeAngle: Float? = null,
    val leftHipAngle: Float? = null,
    val rightHipAngle: Float? = null,
    val backAngle: Float? = null,
    val hipAsymmetry: Float? = null,
    val leftHeelRaised: Boolean = false,
    val rightHeelRaised: Boolean = false
)

/**
 * Factory per creare l'analizzatore appropriato per ogni tipo di esercizio.
 */
object ExerciseAnalyzerFactory {

    fun create(exerciseType: ExerciseType): ExerciseAnalyzer {
        return when (exerciseType) {
            ExerciseType.SQUAT -> SquatAnalyzer()
            ExerciseType.LUNGES -> LungesAnalyzer()
            ExerciseType.DEADLIFT -> DeadliftAnalyzer()
            ExerciseType.BENCH_PRESS -> BenchPressAnalyzer(ExerciseType.BENCH_PRESS)
            ExerciseType.DECLINE_BENCH_PRESS -> BenchPressAnalyzer(ExerciseType.DECLINE_BENCH_PRESS)
            ExerciseType.CHAIN_BENCH_PRESS -> BenchPressAnalyzer(ExerciseType.CHAIN_BENCH_PRESS)
            ExerciseType.CHAOS_PRESS -> BenchPressAnalyzer(ExerciseType.CHAOS_PRESS)
        }
    }
}
