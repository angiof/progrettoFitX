package com.app.progrettofitx.db.dao

import androidx.room.*
import com.app.progrettofitx.db.SchedeEntity

@Dao
interface DaoSchede {

    @Insert
    suspend fun insert(schede: SchedeEntity): Long

    @Update
    suspend fun update(scheda: SchedeEntity)

    @Delete
    suspend fun delete(scheda: SchedeEntity)

    @Query("SELECT * FROM schede")
    suspend fun getAllSchede(): List<SchedeEntity>

    @Query("SELECT * FROM schede WHERE id = :id")
    suspend fun getSchedeById(id: Int): SchedeEntity?

    @Query("SELECT * FROM schede WHERE gruppoMuscolare = :gruppoMuscolare")
    suspend fun getSchedeByGruppoMuscolare(gruppoMuscolare: String): List<SchedeEntity>

    // Aggiungere altre query come necessario
}
