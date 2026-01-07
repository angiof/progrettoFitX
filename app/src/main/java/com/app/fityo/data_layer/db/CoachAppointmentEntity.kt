package com.app.fityo.data_layer.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity per gli appuntamenti di allenamento nel Coach Mode.
 * Ogni appuntamento rappresenta una sessione di allenamento programmata per un atleta.
 */
@Entity(
    tableName = "coach_appointments",
    foreignKeys = [
        ForeignKey(
            entity = CoachProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("profileId")]
)
data class CoachAppointmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val profileId: Int,
    val date: String, // Formato ISO: yyyy-MM-dd
    val time: String? = null, // Formato: HH:mm
    val title: String,
    val notes: String? = null,
    val isCompleted: Boolean = false,
    val schedeId: Int? = null, // Riferimento opzionale a una scheda
    val createdAt: Long = System.currentTimeMillis()
)
