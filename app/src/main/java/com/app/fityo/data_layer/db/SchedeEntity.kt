package com.app.fityo.data_layer.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.fityo.data_layer.db.converters.Converters
import java.io.Serializable

@Entity(tableName = "schede")
@TypeConverters(Converters::class)
data class SchedeEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val gruppoMuscolare: String, // Mantenuto per retrocompatibilita
    val gruppiMuscolari: List<String>? = null, // Nuovo campo per gruppi multipli
    val intesita: String,
    val titolo: String,
    val data: String,
    val notes: String? = null,
    val ora: String? = null, // Formato "HH:mm"
    var favorite: Boolean = false,
    var completed: Boolean = false, // Stato completamento scheda
    var completedDate: String? = null, // Data completamento scheda
    var totalSteps: Int? = null, // Passi totali durante l'allenamento
    var avgHeartRate: Int? = null, // BPM medio durante l'allenamento
    var maxHeartRate: Int? = null, // BPM massimo durante l'allenamento
    val coachProfileId: Int? = null // FK a coach_profiles.id (null = scheda personale)
) : Serializable {
    // Helper per ottenere tutti i gruppi muscolari (sia singolo che multipli)
    fun getAllGruppiMuscolari(): List<String> {
        return gruppiMuscolari ?: listOf(gruppoMuscolare)
    }

    // Helper per ottenere il display dei gruppi muscolari
    fun getGruppiMuscolariDisplay(): String {
        return getAllGruppiMuscolari().joinToString(", ")
    }
}


