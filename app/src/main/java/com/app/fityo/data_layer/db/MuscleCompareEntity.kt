package com.app.fityo.data_layer.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room per salvare i confronti muscolari.
 * Le foto sono salvate criptate localmente e qui si memorizzano i path.
 */
@Entity(tableName = "muscle_compare")
data class MuscleCompareEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    /** Timestamp di creazione del confronto */
    val createdAt: Long,

    /** Path della prima foto (criptata) */
    val photoAPath: String,

    /** Path della seconda foto (criptata) */
    val photoBPath: String,

    /** Variazione percentuale braccia (-100 a +100) */
    val armsVariation: Float,

    /** Variazione percentuale addominali (-100 a +100) */
    val absVariation: Float,

    /** Variazione percentuale gambe (-100 a +100) */
    val legsVariation: Float,

    /** Variazione percentuale glutei (-100 a +100) */
    val glutesVariation: Float,

    /** Note opzionali */
    val notes: String? = null,

    /** Data originale della foto A (opzionale, per storico) */
    val photoADate: String? = null,

    /** Data originale della foto B (opzionale, per storico) */
    val photoBDate: String? = null,

    /** Fattore di scala usato per foto A */
    val scaleFactorA: Float = 1.0f,

    /** Fattore di scala usato per foto B */
    val scaleFactorB: Float = 1.0f
)
