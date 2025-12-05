package com.app.progrettofitx.data_layer.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "essercissi",
    foreignKeys = [ForeignKey(
        entity = SchedeEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("schedaId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [androidx.room.Index(value = ["schedaId"])]
)
data class EsserciziEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int? = null,

    @ColumnInfo(name = "nome") val nome: String,
    @ColumnInfo(name = "attrezzo") val attrezzo: String,

    @ColumnInfo(name = "nRipetizione") val nRipetizione: Int,
    @ColumnInfo(name = "nSerie") val nSerie: Int,

    @ColumnInfo(name = "insometria") val insometria: Int?,

    @ColumnInfo(name = "intervallo") val intervallo: Int?,

    @ColumnInfo(name = "peso") val peso: Float? = null, // Campo peso in kg (opzionale)

    @ColumnInfo(name = "schedaId") val schedaId: Int // Foreign Key
) : Serializable
