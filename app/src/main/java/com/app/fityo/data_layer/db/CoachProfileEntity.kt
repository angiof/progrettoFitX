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
    val createdAt: Long,
    val updatedAt: Long
)
