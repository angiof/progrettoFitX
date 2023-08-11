package com.app.progrettofitx.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "essercissi")
data class EsserciziEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "nome")
    val nome: String,

    @ColumnInfo(name = "nRipetizione")
    val nRipetizione: Int,

    @ColumnInfo(name = "insometria")
    val insometria: Int?,

    @ColumnInfo(name = "intervallo")
    val intervallo: Int?
)

