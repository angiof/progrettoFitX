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



    @Query("""
    SELECT gruppoMuscolare, COUNT(*) * 100.0 / (
        SELECT COUNT(*) 
        FROM schede 
        WHERE data BETWEEN :startDate AND :endDate
    ) as percentuale 
    FROM schede 
    WHERE data BETWEEN :startDate AND :endDate
    GROUP BY gruppoMuscolare
""")
    suspend fun getPercentualePerGruppoMuscolareInDateRange(
        startDate: String,
        endDate: String
    ): List<GruppoMuscolarePercentuale>


    @Query("""
    SELECT gruppoMuscolare, AVG(
        CASE intesita
            WHEN 'Bassa' THEN 5
            WHEN 'Media' THEN 10
            WHEN 'Alta' THEN 15
            ELSE 0 
        END
    ) AS mediaIntensita
    FROM schede
    WHERE data BETWEEN :startDate AND :endDate
    GROUP BY gruppoMuscolare
""")
    suspend fun getMediaIntensitaPerGruppoMuscolareDateRange(
        startDate: String,
        endDate: String
    ): List<GruppoMuscolareIntensitaMedia>



    @Query("""
    SELECT * FROM schede
    WHERE date(data) BETWEEN date(:start) AND date(:end)
    ORDER BY date(data) DESC""")
    suspend fun getSchedeInDateRange(start: String, end: String): List<SchedeEntity>



    @Query("UPDATE schede SET favorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: Int, isFav: Boolean)



}
