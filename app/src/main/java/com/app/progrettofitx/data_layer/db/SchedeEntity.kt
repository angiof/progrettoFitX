package com.app.progrettofitx.data_layer.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.progrettofitx.data_layer.db.converters.Converters
import java.io.Serializable

@Entity(tableName = "schede")
@TypeConverters(Converters::class)
data class SchedeEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val gruppoMuscolare: String, // Mantenuto per retrocompatibilità
    val gruppiMuscolari: List<String>? = null, // Nuovo campo per gruppi multipli
    val intesita: String,
    val titolo: String,
    val data: String,
    val notes: String? = null,
    val ora: String? = null, // Formato "HH:mm"
    var favorite: Boolean = false
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

