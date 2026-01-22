package com.app.fityo.data_layer.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.fityo.data_layer.db.DailyNutritionItemEntity

@Dao
interface DaoDailyNutritionItem {

    @Insert
    suspend fun insert(item: DailyNutritionItemEntity): Long

    @Query("SELECT * FROM daily_nutrition_items WHERE date = :date ORDER BY createdAt DESC")
    suspend fun getByDate(date: String): List<DailyNutritionItemEntity>

    @Query("DELETE FROM daily_nutrition_items WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM daily_nutrition_items WHERE date = :date")
    suspend fun deleteByDate(date: String)
}
