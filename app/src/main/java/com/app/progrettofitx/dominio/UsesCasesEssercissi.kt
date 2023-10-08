package com.app.progrettofitx.dominio

import androidx.lifecycle.LiveData
import com.app.progrettofitx.data_layer.db.EsserciziEntity
import com.app.progrettofitx.data_layer.db.repos.EsserciziRepository


class UsesCasesEssercissi(private val repository: EsserciziRepository) {

    suspend fun getAllById(id: Int): LiveData<List<EsserciziEntity>> {
        return repository.getAllById(id)
    }

}
