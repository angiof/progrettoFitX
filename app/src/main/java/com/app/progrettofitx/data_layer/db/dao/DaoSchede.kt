package com.app.progrettofitx.data_layer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.app.progrettofitx.data_layer.db.SchedeEntity
import com.app.progrettofitx.dominio.GruppoMuscolarePercentuale

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

    @Query("UPDATE schede SET ora = :time WHERE id = :id")
    suspend fun updateTime(id: Int, time: String)


    @Query("SELECT * FROM schede WHERE ora IS NOT NULL")
    suspend fun getSchedeWithTime(): List<SchedeEntity>


    @Query("""
        SELECT gruppoMuscolare, COUNT(*) * 100.0 / (SELECT COUNT(*) FROM schede) as percentuale 
        FROM schede 
        GROUP BY gruppoMuscolare
    """)
    suspend fun getPercentualePerGruppoMuscolare(): List<GruppoMuscolarePercentuale>

}
