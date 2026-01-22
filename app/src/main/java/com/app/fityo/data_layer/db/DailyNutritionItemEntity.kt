package com.app.fityo.data_layer.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_nutrition_items",
    indices = [Index(value = ["date"])]
)
data class DailyNutritionItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val date: String,
    val name: String,
    val grams: Float,
    val proteins: Float,
    val carbs: Float,
    val fats: Float,
    val kcal: Float,
    // Nuovi campi
    val fibers: Float = 0f,
    val sugars: Float = 0f,
    val saturatedFats: Float = 0f,
    val salt: Float = 0f,
    val source: String,
    val createdAt: Long
)
