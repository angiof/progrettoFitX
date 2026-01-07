package com.app.fityo.data_layer.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.fityo.data_layer.db.CoachProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO per la gestione dei profili coach.
 * Supporta operazioni CRUD complete e query per statistiche.
 */
@Dao
interface DaoCoachProfile {

    /**
     * Inserisce un nuovo profilo coach.
     * @return ID del profilo inserito
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: CoachProfileEntity): Long

    /**
     * Aggiorna un profilo esistente.
     */
    @Update
    suspend fun update(profile: CoachProfileEntity)

    /**
     * Elimina un profilo.
     */
    @Delete
    suspend fun delete(profile: CoachProfileEntity)

    /**
     * Elimina un profilo per ID.
     */
    @Query("DELETE FROM coach_profiles WHERE id = :id")
    suspend fun deleteById(id: Int)

    /**
     * Ottiene tutti i profili coach ordinati per nome.
     */
    @Query("SELECT * FROM coach_profiles ORDER BY name ASC")
    fun getAllProfiles(): Flow<List<CoachProfileEntity>>

    /**
     * Ottiene tutti i profili coach (versione sincrona).
     */
    @Query("SELECT * FROM coach_profiles ORDER BY name ASC")
    suspend fun getAllProfilesSync(): List<CoachProfileEntity>

    /**
     * Ottiene un profilo per ID.
     */
    @Query("SELECT * FROM coach_profiles WHERE id = :id")
    suspend fun getById(id: Int): CoachProfileEntity?

    /**
     * Conta i profili esistenti.
     */
    @Query("SELECT COUNT(*) FROM coach_profiles")
    suspend fun count(): Int

    /**
     * Verifica se esiste almeno un profilo.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM coach_profiles LIMIT 1)")
    suspend fun hasProfiles(): Boolean

    /**
     * Elimina tutti i profili.
     */
    @Query("DELETE FROM coach_profiles")
    suspend fun deleteAll()
}
