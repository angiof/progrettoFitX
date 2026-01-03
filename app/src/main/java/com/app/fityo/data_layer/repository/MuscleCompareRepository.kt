package com.app.fityo.data_layer.repository

import com.app.fityo.data_layer.db.MuscleCompareEntity
import com.app.fityo.data_layer.db.dao.DaoMuscleCompare
import com.app.fityo.dominio.CompareHistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository per i confronti muscolari.
 * Gestisce l'accesso ai dati e le trasformazioni.
 */
class MuscleCompareRepository(private val dao: DaoMuscleCompare) {

    /**
     * Ottiene tutti i confronti come Flow per aggiornamenti reattivi.
     */
    fun getAllCompares(): Flow<List<MuscleCompareEntity>> = dao.getAllCompares()

    /**
     * Ottiene tutti i confronti come lista di CompareHistoryItem.
     */
    fun getAllComparesAsHistoryItems(): Flow<List<CompareHistoryItem>> {
        return dao.getAllCompares().map { entities ->
            entities.map { entity ->
                CompareHistoryItem(
                    id = entity.id ?: 0,
                    createdAt = entity.createdAt,
                    armsVariation = entity.armsVariation,
                    absVariation = entity.absVariation,
                    legsVariation = entity.legsVariation,
                    glutesVariation = entity.glutesVariation,
                    photoAPath = entity.photoAPath,
                    photoBPath = entity.photoBPath
                )
            }
        }
    }

    /**
     * Inserisce un nuovo confronto.
     */
    suspend fun insert(compare: MuscleCompareEntity): Long = dao.insert(compare)

    /**
     * Aggiorna un confronto esistente.
     */
    suspend fun update(compare: MuscleCompareEntity) = dao.update(compare)

    /**
     * Elimina un confronto.
     */
    suspend fun delete(compare: MuscleCompareEntity) = dao.delete(compare)

    /**
     * Elimina un confronto per ID.
     */
    suspend fun deleteById(id: Int) = dao.deleteById(id)

    /**
     * Ottiene un confronto per ID.
     */
    suspend fun getById(id: Int): MuscleCompareEntity? = dao.getById(id)

    /**
     * Ottiene l'ultimo confronto.
     */
    suspend fun getLatest(): MuscleCompareEntity? = dao.getLatest()

    /**
     * Conta il numero totale di confronti.
     */
    suspend fun getCount(): Int = dao.getCount()

    /**
     * Ottiene confronti in un range di date.
     */
    fun getComparesByDateRange(startDate: Long, endDate: Long): Flow<List<MuscleCompareEntity>> {
        return dao.getComparesByDateRange(startDate, endDate)
    }

    /**
     * Elimina tutti i confronti.
     */
    suspend fun deleteAll() = dao.deleteAll()

    /**
     * Ottiene le medie delle variazioni per tutti i distretti.
     */
    suspend fun getAverageVariations(): AverageVariations {
        return AverageVariations(
            arms = dao.getAverageArmsVariation() ?: 0f,
            abs = dao.getAverageAbsVariation() ?: 0f,
            legs = dao.getAverageLegsVariation() ?: 0f,
            glutes = dao.getAverageGlutesVariation() ?: 0f
        )
    }

    /**
     * Data class per le medie delle variazioni.
     */
    data class AverageVariations(
        val arms: Float,
        val abs: Float,
        val legs: Float,
        val glutes: Float
    )
}
