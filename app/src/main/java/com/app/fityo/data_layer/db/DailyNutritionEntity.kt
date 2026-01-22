package com.app.fityo.data_layer.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_nutrition",
    indices = [Index(value = ["date"], unique = true)]
)
data class DailyNutritionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val date: String,
    val totalProteins: Float,
    val totalCarbs: Float,
    val totalFats: Float,
    val totalKcal: Float,
    // Nuovi campi
    val totalFibers: Float = 0f,
    val totalSugars: Float = 0f,
    val totalSaturatedFats: Float = 0f,
    val totalSalt: Float = 0f,
    val updatedAt: Long
)
