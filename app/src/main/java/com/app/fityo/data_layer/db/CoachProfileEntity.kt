package com.app.fityo.data_layer.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room per i profili coach.
 * Ogni profilo rappresenta un atleta gestito dal coach.
 * Le schede possono essere associate a un profilo specifico tramite coachProfileId.
 */
@Entity(tableName = "coach_profiles")
data class CoachProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val avatarColor: Int,
    val notes: String? = null,

    // Anagrafica facoltativa: serve al coach per avere il contatto sottomano, non e mai
    // richiesta per creare il profilo.
    val telefono: String? = null,
    val email: String? = null,
    val disciplina: String? = null,
    val livello: String? = null,

    val createdAt: Long,
    val updatedAt: Long
)
