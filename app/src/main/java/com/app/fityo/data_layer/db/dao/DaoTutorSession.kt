package com.app.fityo.data_layer.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.app.fityo.data_layer.db.TutorSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO per operazioni CRUD sulle sessioni Tutor.
 */
@Dao
interface DaoTutorSession {

    /**
     * Inserisce una nuova sessione.
     * @return L'ID della sessione inserita
     */
    @Insert
    suspend fun insert(session: TutorSessionEntity): Long

    /**
     * Aggiorna una sessione esistente.
     */
    @Update
    suspend fun update(session: TutorSessionEntity)

    /**
     * Elimina una sessione.
     */
    @Delete
    suspend fun delete(session: TutorSessionEntity)

    /**
     * Elimina una sessione per ID.
     */
    @Query("DELETE FROM tutor_sessions WHERE id = :id")
    suspend fun deleteById(id: Int)

    /**
     * Ottiene tutte le sessioni ordinate per data (più recenti prima).
     */
    @Query("SELECT * FROM tutor_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<TutorSessionEntity>>

    /**
     * Ottiene una sessione per ID.
     */
    @Query("SELECT * FROM tutor_sessions WHERE id = :id")
    suspend fun getById(id: Int): TutorSessionEntity?

    /**
     * Ottiene l'ultima sessione effettuata.
     */
    @Query("SELECT * FROM tutor_sessions ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatest(): TutorSessionEntity?

    /**
     * Conta il numero totale di sessioni.
     */
    @Query("SELECT COUNT(*) FROM tutor_sessions")
    suspend fun getCount(): Int

    /**
     * Ottiene sessioni filtrate per tipo di esercizio.
     */
    @Query("SELECT * FROM tutor_sessions WHERE exerciseType = :exerciseType ORDER BY createdAt DESC")
    fun getSessionsByExerciseType(exerciseType: String): Flow<List<TutorSessionEntity>>

    /**
     * Ottiene sessioni in un range di date.
     */
    @Query("SELECT * FROM tutor_sessions WHERE createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    fun getSessionsByDateRange(startDate: Long, endDate: Long): Flow<List<TutorSessionEntity>>

    /**
     * Elimina tutte le sessioni (per pulizia completa).
     */
    @Query("DELETE FROM tutor_sessions")
    suspend fun deleteAll()

    /**
     * Ottiene il punteggio medio per un tipo di esercizio.
     */
    @Query("SELECT AVG(overallScore) FROM tutor_sessions WHERE exerciseType = :exerciseType")
    suspend fun getAverageScoreByExercise(exerciseType: String): Float?

    /**
     * Ottiene il punteggio medio complessivo.
     */
    @Query("SELECT AVG(overallScore) FROM tutor_sessions")
    suspend fun getOverallAverageScore(): Float?

    /**
     * Ottiene il conteggio delle sessioni per tipo di esercizio.
     */
    @Query("SELECT COUNT(*) FROM tutor_sessions WHERE exerciseType = :exerciseType")
    suspend fun getCountByExercise(exerciseType: String): Int
}
