package com.app.fityo.data_layer.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room per salvare le sessioni di analisi Tutor.
 * I video sono salvati localmente e qui si memorizzano i path e i risultati.
 */
@Entity(tableName = "tutor_sessions")
data class TutorSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    /** Timestamp di creazione della sessione */
    val createdAt: Long,

    /** Tipo di esercizio: "SQUAT", "LUNGES", "DEADLIFT" */
    val exerciseType: String,

    /** Path del video analizzato */
    val videoPath: String,

    /** Path della thumbnail (opzionale) */
    val thumbnailPath: String? = null,

    /** Durata del video in millisecondi */
    val duration: Long,

    /** Numero totale di errori rilevati */
    val totalErrors: Int,

    /** Punteggio complessivo (0-100) */
    val overallScore: Float,

    /** JSON array degli errori rilevati */
    val errorsJson: String
)
