package com.app.progrettofitx.repos

import androidx.lifecycle.LiveData
import com.app.progrettofitx.db.EsserciziEntity
import com.app.progrettofitx.db.dao.DaoEssercissi

class EsserciziRepository(private val daoEssercissi: DaoEssercissi) {

    // Function to insert EsserciziEntity into the database
    suspend fun insert(essercizi: EsserciziEntity) {
        daoEssercissi.insert(essercizi)
    }

    // Function to update an existing EsserciziEntity in the database
    suspend fun update(essercizi: EsserciziEntity) {
        daoEssercissi.update(essercizi)
    }

    // Function to delete an EsserciziEntity from the database
    suspend fun delete(essercizi: EsserciziEntity) {
        daoEssercissi.delete(essercizi)
    }



    suspend fun getAllById(id: Int): LiveData<List<EsserciziEntity>> {
        return daoEssercissi.getEssercissiBySchedaId(id)
    }
}
