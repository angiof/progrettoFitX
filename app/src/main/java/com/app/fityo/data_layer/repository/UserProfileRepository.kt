package com.app.fityo.data_layer.repository

import com.app.fityo.data_layer.db.UserProfileEntity
import com.app.fityo.data_layer.db.dao.DaoUserProfile
import com.app.fityo.dominio.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository per la gestione del profilo utente.
 * Fornisce un'interfaccia pulita per le operazioni CRUD.
 */
class UserProfileRepository(private val dao: DaoUserProfile) {

    /**
     * Ottiene il profilo attivo come Flow.
     */
    fun getActiveProfileFlow(): Flow<UserProfile?> {
        return dao.getActiveProfileFlow().map { entity ->
            entity?.toDomainModel()
        }
    }

    /**
     * Ottiene il profilo attivo.
     */
    suspend fun getActiveProfile(): UserProfile? {
        return dao.getActiveProfile()?.toDomainModel()
    }

    /**
     * Ottiene un profilo per ID.
     */
    suspend fun getById(id: Int): UserProfile? {
        return dao.getById(id)?.toDomainModel()
    }

    /**
     * Ottiene tutti i profili.
     */
    fun getAllProfiles(): Flow<List<UserProfile>> {
        return dao.getAllProfiles().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * Crea un nuovo profilo.
     * @return ID del profilo creato
     */
    suspend fun createProfile(profile: UserProfile): Long {
        val entity = UserProfileEntity.fromDomainModel(profile)
        return dao.insert(entity)
    }

    /**
     * Aggiorna un profilo esistente.
     */
    suspend fun updateProfile(profile: UserProfile) {
        val entity = UserProfileEntity.fromDomainModel(
            profile.copy(updatedAt = System.currentTimeMillis())
        )
        dao.update(entity)
    }

    /**
     * Elimina un profilo.
     */
    suspend fun deleteProfile(profile: UserProfile) {
        val entity = UserProfileEntity.fromDomainModel(profile)
        dao.delete(entity)
    }

    /**
     * Elimina un profilo per ID.
     */
    suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }

    /**
     * Elimina tutti i profili.
     */
    suspend fun deleteAll() {
        dao.deleteAll()
    }

    /**
     * Verifica se esiste almeno un profilo.
     */
    suspend fun hasProfile(): Boolean {
        return dao.hasProfile()
    }

    /**
     * Conta i profili esistenti.
     */
    suspend fun count(): Int {
        return dao.count()
    }
}
