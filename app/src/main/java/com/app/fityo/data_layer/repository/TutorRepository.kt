package com.app.fityo.data_layer.repository

import com.app.fityo.data_layer.db.TutorSessionEntity
import com.app.fityo.data_layer.db.dao.DaoTutorSession
import com.app.fityo.dominio.ExerciseError
import com.app.fityo.dominio.ExerciseType
import com.app.fityo.dominio.TutorHistoryItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository per la gestione delle sessioni Tutor.
 */
class TutorRepository(private val dao: DaoTutorSession) {

    private val gson = Gson()

    /**
     * Inserisce una nuova sessione.
     */
    suspend fun insert(session: TutorSessionEntity): Long {
        return dao.insert(session)
    }

    /**
     * Aggiorna una sessione esistente.
     */
    suspend fun update(session: TutorSessionEntity) {
        dao.update(session)
    }

    /**
     * Elimina una sessione per ID.
     */
    suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }

    /**
     * Ottiene una sessione per ID.
     */
    suspend fun getById(id: Int): TutorSessionEntity? {
        return dao.getById(id)
    }

    /**
     * Ottiene tutte le sessioni come HistoryItems.
     */
    fun getAllSessionsAsHistoryItems(): Flow<List<TutorHistoryItem>> {
        return dao.getAllSessions().map { sessions ->
            sessions.map { entity ->
                TutorHistoryItem(
                    id = entity.id ?: 0,
                    createdAt = entity.createdAt,
                    exerciseType = ExerciseType.valueOf(entity.exerciseType),
                    thumbnailPath = entity.thumbnailPath,
                    duration = entity.duration,
                    totalErrors = entity.totalErrors,
                    overallScore = entity.overallScore
                )
            }
        }
    }

    /**
     * Converte una lista di errori in JSON per il salvataggio.
     */
    fun errorsToJson(errors: List<ExerciseError>): String {
        return gson.toJson(errors)
    }

    /**
     * Converte JSON in lista di errori.
     */
    fun jsonToErrors(json: String): List<ExerciseError> {
        val type = object : TypeToken<List<ExerciseError>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Ottiene il punteggio medio per un tipo di esercizio.
     */
    suspend fun getAverageScoreByExercise(exerciseType: ExerciseType): Float? {
        return dao.getAverageScoreByExercise(exerciseType.name)
    }

    /**
     * Ottiene il numero di sessioni.
     */
    suspend fun getCount(): Int {
        return dao.getCount()
    }
}
