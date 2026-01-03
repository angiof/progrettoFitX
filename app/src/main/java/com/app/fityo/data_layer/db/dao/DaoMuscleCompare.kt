package com.app.fityo.data_layer.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.app.fityo.data_layer.db.MuscleCompareEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO per operazioni CRUD sui confronti muscolari.
 */
@Dao
interface DaoMuscleCompare {

    /**
     * Inserisce un nuovo confronto.
     * @return L'ID del confronto inserito
     */
    @Insert
    suspend fun insert(compare: MuscleCompareEntity): Long

    /**
     * Aggiorna un confronto esistente.
     */
    @Update
    suspend fun update(compare: MuscleCompareEntity)

    /**
     * Elimina un confronto.
     */
    @Delete
    suspend fun delete(compare: MuscleCompareEntity)

    /**
     * Elimina un confronto per ID.
     */
    @Query("DELETE FROM muscle_compare WHERE id = :id")
    suspend fun deleteById(id: Int)

    /**
     * Ottiene tutti i confronti ordinati per data (più recenti prima).
     */
    @Query("SELECT * FROM muscle_compare ORDER BY createdAt DESC")
    fun getAllCompares(): Flow<List<MuscleCompareEntity>>

    /**
     * Ottiene un confronto per ID.
     */
    @Query("SELECT * FROM muscle_compare WHERE id = :id")
    suspend fun getById(id: Int): MuscleCompareEntity?

    /**
     * Ottiene l'ultimo confronto effettuato.
     */
    @Query("SELECT * FROM muscle_compare ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatest(): MuscleCompareEntity?

    /**
     * Conta il numero totale di confronti.
     */
    @Query("SELECT COUNT(*) FROM muscle_compare")
    suspend fun getCount(): Int

    /**
     * Ottiene confronti in un range di date.
     */
    @Query("SELECT * FROM muscle_compare WHERE createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    fun getComparesByDateRange(startDate: Long, endDate: Long): Flow<List<MuscleCompareEntity>>

    /**
     * Elimina tutti i confronti (per pulizia completa).
     */
    @Query("DELETE FROM muscle_compare")
    suspend fun deleteAll()

    /**
     * Ottiene la media delle variazioni per le braccia.
     */
    @Query("SELECT AVG(armsVariation) FROM muscle_compare")
    suspend fun getAverageArmsVariation(): Float?

    /**
     * Ottiene la media delle variazioni per gli addominali.
     */
    @Query("SELECT AVG(absVariation) FROM muscle_compare")
    suspend fun getAverageAbsVariation(): Float?

    /**
     * Ottiene la media delle variazioni per le gambe.
     */
    @Query("SELECT AVG(legsVariation) FROM muscle_compare")
    suspend fun getAverageLegsVariation(): Float?

    /**
     * Ottiene la media delle variazioni per i glutei.
     */
    @Query("SELECT AVG(glutesVariation) FROM muscle_compare")
    suspend fun getAverageGlutesVariation(): Float?
}
