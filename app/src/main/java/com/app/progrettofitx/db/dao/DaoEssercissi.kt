package com.app.progrettofitx.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.app.progrettofitx.db.EsserciziEntity

@Dao
interface DaoEssercissi {
    @Insert
    suspend fun insert(essercizi: EsserciziEntity)

    @Update
    suspend fun update(essercizi: EsserciziEntity)

    @Delete
    suspend fun delete(essercizi: EsserciziEntity)

    @Query("SELECT * FROM essercissi")
    suspend fun getAllEssercissi(): List<EsserciziEntity>

    @Query("SELECT * FROM essercissi WHERE id = :id")
    suspend fun getEssercissiById(id: Int): EsserciziEntity?
}
