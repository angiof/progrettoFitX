package com.app.progrettofitx.dominio

import androidx.lifecycle.LiveData
import com.app.progrettofitx.data_layer.db.EsserciziEntity
import com.app.progrettofitx.data_layer.db.repos.EsserciziRepository

class UsesCasesEssercissi(private val repository: EsserciziRepository) {

    fun getAllById(id: Int): LiveData<List<EsserciziEntity>> = repository.getAllById(id)

    suspend fun insert(essercizi: EsserciziEntity) {
        repository.insert(essercizi)
    }

    suspend fun getTotalEss(id: Int): Int = repository.getCountById(id)

    suspend fun delateEss(essercizi: EsserciziEntity) {
        repository.delete(essercizi)
    }

    suspend fun delateEss(id: Int) {
        repository.delateFromId(id)
    }

    suspend fun update(item: EsserciziEntity) = repository.update(item)
}
