package com.app.progrettofitx.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "essercissi",
    foreignKeys = [
        ForeignKey(
            entity = SchedeEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("schedaId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class EsserciziEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,

    @ColumnInfo(name = "nome")
    val nome: String,

    @ColumnInfo(name = "nRipetizione")
    val nRipetizione: Int,

    @ColumnInfo(name = "insometria")
    val insometria: Int?,

    @ColumnInfo(name = "intervallo")
    val intervallo: Int?,

    @ColumnInfo(name = "schedaId")
    val schedaId: Int // Foreign Key
) : Serializable
