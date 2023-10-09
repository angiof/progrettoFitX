package com.app.progrettofitx.dominio

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.progrettofitx.data_layer.db.EsserciziEntity
import com.app.progrettofitx.data_layer.db.repos.EsserciziRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UsesCasesEssercissi(private val repository: EsserciziRepository) {

    suspend fun getAllById(id: Int): LiveData<List<EsserciziEntity>> {
        return repository.getAllById(id)
    }


   suspend fun insert(essercizi: EsserciziEntity)  {
        repository.insert(essercizi)
    }

    suspend fun getTotalEss(id: Int): Int {
        return repository.getCountById(id)
    }
}
