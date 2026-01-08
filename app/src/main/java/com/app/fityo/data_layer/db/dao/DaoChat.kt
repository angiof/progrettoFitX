package com.app.fityo.data_layer.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.fityo.data_layer.db.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO per la gestione dei messaggi della chat AI.
 */
@Dao
interface DaoChat {

    @Insert
    suspend fun insert(message: ChatMessageEntity): Long

    /**
     * Recupera tutti i messaggi per un profilo specifico o globali (profileId IS NULL).
     * Ordinati per timestamp crescente (dal piu vecchio al piu recente).
     */
    @Query("""
        SELECT * FROM chat_messages
        WHERE profileId = :profileId OR (profileId IS NULL AND :profileId IS NULL)
        ORDER BY timestamp ASC
    """)
    fun getMessagesByProfile(profileId: Int?): Flow<List<ChatMessageEntity>>

    /**
     * Recupera tutti i messaggi di tutte le sessioni, ordinati per timestamp.
     */
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    /**
     * Recupera gli ultimi N messaggi (per preview o caricamento iniziale).
     */
    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(limit: Int = 50): List<ChatMessageEntity>

    /**
     * Recupera messaggi di una sessione specifica.
     */
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesBySession(sessionId: String): Flow<List<ChatMessageEntity>>

    /**
     * Elimina messaggi piu vecchi di un certo timestamp (pulizia periodica).
     */
    @Query("DELETE FROM chat_messages WHERE timestamp < :beforeTimestamp")
    suspend fun deleteOlderThan(beforeTimestamp: Long)

    /**
     * Elimina tutti i messaggi (reset chat).
     */
    @Query("DELETE FROM chat_messages")
    suspend fun clearAll()

    /**
     * Elimina messaggi di un profilo specifico.
     */
    @Query("DELETE FROM chat_messages WHERE profileId = :profileId")
    suspend fun clearByProfile(profileId: Int)

    /**
     * Conta il numero totale di messaggi.
     */
    @Query("SELECT COUNT(*) FROM chat_messages")
    suspend fun count(): Int

    /**
     * Conta messaggi per profilo.
     */
    @Query("SELECT COUNT(*) FROM chat_messages WHERE profileId = :profileId OR (profileId IS NULL AND :profileId IS NULL)")
    suspend fun countByProfile(profileId: Int?): Int
}
