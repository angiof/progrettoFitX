package com.app.fityo.data_layer.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity Room per gli avatar 3D generati da Body Intelligence.
 * Memorizza mesh 3D, parametri shape e colori zone muscolari.
 */
@Entity(
    tableName = "avatar_3d",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class Avatar3DEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val userId: Int,
    val createdAt: Long,
    val meshDataPath: String,           // Path al file .glb generato
    val thumbnailPath: String?,         // Preview image
    val shapeParametersJson: String,    // JSON con body shape params
    val zoneColorsJson: String,         // JSON con colori zone muscolari
    val videoSourcePath: String?,       // Path al video 360 originale (opzionale)
    val processingDurationMs: Long,     // Durata elaborazione in ms
    val framesAnalyzed: Int,            // Numero frame analizzati
    val confidence: Float               // Confidenza ricostruzione (0-1)
)
