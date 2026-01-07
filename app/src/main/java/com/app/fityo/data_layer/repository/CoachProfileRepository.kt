package com.app.fityo.data_layer.repository

import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.data_layer.db.dao.DaoCoachProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository per la gestione dei profili coach.
 * Fornisce un'astrazione sul DAO per operazioni CRUD e query.
 */
class CoachProfileRepository(private val dao: DaoCoachProfile) {

    /**
     * Ottiene tutti i profili coach come Flow per osservare i cambiamenti.
     */
    fun getAllProfiles(): Flow<List<CoachProfileEntity>> = dao.getAllProfiles()

    /**
     * Ottiene tutti i profili coach (versione sincrona).
     */
    suspend fun getAllProfilesSync(): List<CoachProfileEntity> = dao.getAllProfilesSync()

    /**
     * Ottiene un profilo per ID.
     */
    suspend fun getById(id: Int): CoachProfileEntity? = dao.getById(id)

    /**
     * Inserisce un nuovo profilo.
     * @return ID del profilo inserito
     */
    suspend fun insert(profile: CoachProfileEntity): Long = dao.insert(profile)

    /**
     * Aggiorna un profilo esistente.
     */
    suspend fun update(profile: CoachProfileEntity) = dao.update(profile)

    /**
     * Elimina un profilo.
     */
    suspend fun delete(profile: CoachProfileEntity) = dao.delete(profile)

    /**
     * Elimina un profilo per ID.
     */
    suspend fun deleteById(id: Int) = dao.deleteById(id)

    /**
     * Conta i profili esistenti.
     */
    suspend fun count(): Int = dao.count()

    /**
     * Verifica se esistono profili.
     */
    suspend fun hasProfiles(): Boolean = dao.hasProfiles()

    /**
     * Elimina tutti i profili.
     */
    suspend fun deleteAll() = dao.deleteAll()
}
