package com.app.fityo.data_layer.db.repos

import androidx.lifecycle.LiveData
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.dao.DaoEssercissi

class EsserciziRepository(private val daoEssercissi: DaoEssercissi) {

    suspend fun insert(essercizi: EsserciziEntity) = daoEssercissi.insert(essercizi)
    suspend fun update(essercizi: EsserciziEntity) = daoEssercissi.update(essercizi)
    suspend fun delete(essercizi: EsserciziEntity) = daoEssercissi.delete(essercizi)



    //delate from id
    suspend fun delateFromId(id: Int) = daoEssercissi.deleteFromId(id = id)

    suspend fun deleteBySchedaId(schedaId: Int) = daoEssercissi.deleteBySchedaId(schedaId)

    // Function to insert EsserciziEntit into the database
    fun getAllById(id: Int): LiveData<List<EsserciziEntity>> =
        daoEssercissi.getEssercissiBySchedaId(id)

    suspend fun getAllByIdSync(schedaId: Int): List<EsserciziEntity> =
        daoEssercissi.getEserciziByschedaIdSync(schedaId)

    suspend fun getCountById(id: Int): Int = daoEssercissi.countEsserciziById(id)




}

