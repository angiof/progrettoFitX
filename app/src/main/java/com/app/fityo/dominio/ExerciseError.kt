package com.app.fityo.dominio

/**
 * Rappresenta un errore rilevato durante l'analisi di un esercizio.
 */
data class ExerciseError(
    /** Timestamp in millisecondi dall'inizio del video */
    val timestampMs: Long,

    /** Timestamp di fine dell'errore (se persistente) */
    val endTimestampMs: Long,

    /** Tipo di errore specifico */
    val errorType: ExerciseErrorType,

    /** Severità dell'errore */
    val severity: ErrorSeverity,

    /** Messaggio descrittivo dell'errore */
    val message: String,

    /** Indici dei landmark MediaPipe coinvolti */
    val affectedLandmarks: List<Int>,

    /** Suggerimento per correggere l'errore */
    val correctionHint: String
)

/**
 * Tipi di errori rilevabili per ogni esercizio.
 */
enum class ExerciseErrorType(val displayName: String) {
    // Squat errors
    SQUAT_DEPTH_INSUFFICIENT("Profondità insufficiente"),
    SQUAT_BACK_CURVED("Schiena curva"),
    SQUAT_HEELS_RAISED("Talloni sollevati"),
    SQUAT_KNEES_CAVE_IN("Ginocchia verso l'interno"),

    // Lunges errors
    LUNGES_HIP_ASYMMETRY("Asimmetria delle anche"),
    LUNGES_KNEE_ANGLE_WRONG("Angolo ginocchio errato"),
    LUNGES_BACK_KNEE_HIGH("Ginocchio posteriore troppo alto"),
    LUNGES_TORSO_LEAN("Busto inclinato eccessivamente"),

    // Deadlift errors
    DEADLIFT_BACK_ROUNDED("Schiena arrotondata"),
    DEADLIFT_BAR_DRIFT("Bilanciere non verticale"),
    DEADLIFT_HIP_HINGE_WRONG("Hip hinge errato"),
    DEADLIFT_LOCKOUT_INCOMPLETE("Lockout incompleto"),

    // Bench Press errors (all variants)
    BENCH_ELBOW_FLARE("Gomiti troppo larghi"),
    BENCH_BAR_PATH_WRONG("Traiettoria bilanciere errata"),
    BENCH_ARCH_COLLAPSED("Arco lombare collassato"),
    BENCH_WRIST_BENT("Polsi piegati"),
    BENCH_UNEVEN_PRESS("Spinta asimmetrica"),
    BENCH_LOCKOUT_INCOMPLETE("Lockout incompleto"),
    BENCH_DEPTH_INSUFFICIENT("ROM insufficiente"),
    BENCH_SHOULDER_PROTRACTION("Scapole non retratte"),

    // Video validation errors
    VIDEO_WRONG_EXERCISE("Esercizio sbagliato nel video"),
    VIDEO_NO_PERSON_DETECTED("Nessuna persona rilevata"),
    VIDEO_POOR_VISIBILITY("Visibilità scarsa"),
    VIDEO_WRONG_ANGLE("Angolazione video errata")
}

/**
 * Livelli di severità degli errori.
 */
enum class ErrorSeverity(val displayName: String, val colorWeight: Float) {
    WARNING("Attenzione", 0.5f),
    ERROR("Errore", 0.75f),
    CRITICAL("Critico", 1.0f)
}
