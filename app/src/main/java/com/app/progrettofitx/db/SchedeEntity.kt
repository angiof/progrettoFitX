package com.app.progrettofitx.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "schede")
data class SchedeEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val gruppoMuscolare: String,
    val intesita: String,
    val titolo: String,
    val data: String,
    val notes: String? = null
) : Serializable
