package com.app.fityo.data_layer.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.fityo.data_layer.db.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO per la gestione del profilo utente.
 * Supporta operazioni CRUD complete.
 */
@Dao
interface DaoUserProfile {

    /**
     * Inserisce un nuovo profilo.
     * @return ID del profilo inserito
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfileEntity): Long

    /**
     * Aggiorna un profilo esistente.
     */
    @Update
    suspend fun update(profile: UserProfileEntity)

    /**
     * Elimina un profilo.
     */
    @Delete
    suspend fun delete(profile: UserProfileEntity)

    /**
     * Elimina un profilo per ID.
     */
    @Query("DELETE FROM user_profile WHERE id = :id")
    suspend fun deleteById(id: Int)

    /**
     * Ottiene il profilo attivo (il più recente).
     * Assumiamo un solo profilo attivo alla volta.
     */
    @Query("SELECT * FROM user_profile ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getActiveProfile(): UserProfileEntity?

    /**
     * Ottiene il profilo attivo come Flow per osservare i cambiamenti.
     */
    @Query("SELECT * FROM user_profile ORDER BY updatedAt DESC LIMIT 1")
    fun getActiveProfileFlow(): Flow<UserProfileEntity?>

    /**
     * Ottiene un profilo per ID.
     */
    @Query("SELECT * FROM user_profile WHERE id = :id")
    suspend fun getById(id: Int): UserProfileEntity?

    /**
     * Ottiene tutti i profili (per supporto multi-profilo futuro).
     */
    @Query("SELECT * FROM user_profile ORDER BY updatedAt DESC")
    fun getAllProfiles(): Flow<List<UserProfileEntity>>

    /**
     * Conta i profili esistenti.
     */
    @Query("SELECT COUNT(*) FROM user_profile")
    suspend fun count(): Int

    /**
     * Verifica se esiste almeno un profilo.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM user_profile LIMIT 1)")
    suspend fun hasProfile(): Boolean

    /**
     * Elimina tutti i profili.
     */
    @Query("DELETE FROM user_profile")
    suspend fun deleteAll()
}
