package com.app.progrettofitx.data_layer.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val title: String,
    val message: String,
    val timestamp: Long,
    val schedaId: Int? = null,
    val read: Boolean = false
) : Serializable
