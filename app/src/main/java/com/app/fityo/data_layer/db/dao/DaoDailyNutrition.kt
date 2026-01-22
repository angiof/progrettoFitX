package com.app.fityo.data_layer.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.fityo.data_layer.db.DailyNutritionEntity

@Dao
interface DaoDailyNutrition {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: DailyNutritionEntity): Long

    @Query("SELECT * FROM daily_nutrition WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): DailyNutritionEntity?

    @Query("SELECT * FROM daily_nutrition ORDER BY date DESC")
    suspend fun getAll(): List<DailyNutritionEntity>

    @Query("DELETE FROM daily_nutrition WHERE date = :date")
    suspend fun deleteByDate(date: String)
}
