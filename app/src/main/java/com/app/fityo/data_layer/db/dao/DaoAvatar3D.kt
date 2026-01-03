package com.app.fityo.data_layer.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.fityo.data_layer.db.Avatar3DEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO per la gestione degli avatar 3D.
 * Supporta operazioni CRUD e query per storico.
 */
@Dao
interface DaoAvatar3D {

    /**
     * Inserisce un nuovo avatar 3D.
     * @return ID dell'avatar inserito
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(avatar: Avatar3DEntity): Long

    /**
     * Elimina un avatar.
     */
    @Delete
    suspend fun delete(avatar: Avatar3DEntity)

    /**
     * Elimina un avatar per ID.
     */
    @Query("DELETE FROM avatar_3d WHERE id = :id")
    suspend fun deleteById(id: Int)

    /**
     * Ottiene un avatar per ID.
     */
    @Query("SELECT * FROM avatar_3d WHERE id = :id")
    suspend fun getById(id: Int): Avatar3DEntity?

    /**
     * Ottiene l'ultimo avatar creato per un utente.
     */
    @Query("SELECT * FROM avatar_3d WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestByUserId(userId: Int): Avatar3DEntity?

    /**
     * Ottiene tutti gli avatar di un utente ordinati per data (più recente prima).
     */
    @Query("SELECT * FROM avatar_3d WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAvatarsByUserId(userId: Int): Flow<List<Avatar3DEntity>>

    /**
     * Ottiene tutti gli avatar ordinati per data.
     */
    @Query("SELECT * FROM avatar_3d ORDER BY createdAt DESC")
    fun getAllAvatars(): Flow<List<Avatar3DEntity>>

    /**
     * Conta gli avatar per un utente.
     */
    @Query("SELECT COUNT(*) FROM avatar_3d WHERE userId = :userId")
    suspend fun countByUserId(userId: Int): Int

    /**
     * Elimina tutti gli avatar di un utente.
     */
    @Query("DELETE FROM avatar_3d WHERE userId = :userId")
    suspend fun deleteAllByUserId(userId: Int)

    /**
     * Elimina tutti gli avatar.
     */
    @Query("DELETE FROM avatar_3d")
    suspend fun deleteAll()
}
