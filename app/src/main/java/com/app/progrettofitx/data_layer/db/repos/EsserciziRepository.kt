package com.app.progrettofitx.data_layer.db.repos

import androidx.lifecycle.LiveData
import com.app.progrettofitx.data_layer.db.EsserciziEntity
import com.app.progrettofitx.data_layer.db.dao.DaoEssercissi

class EsserciziRepository(private val daoEssercissi: DaoEssercissi) {

    suspend fun insert(essercizi: EsserciziEntity) = daoEssercissi.insert(essercizi)
    suspend fun update(essercizi: EsserciziEntity) = daoEssercissi.update(essercizi)
    suspend fun delete(essercizi: EsserciziEntity) = daoEssercissi.delete(essercizi)

    // Function to insert EsserciziEntity into the database
    fun getAllById(id: Int): LiveData<List<EsserciziEntity>> =
        daoEssercissi.getEssercissiBySchedaId(id)

    suspend fun getCountById(id: Int): Int = daoEssercissi.countEsserciziById(id)


}
