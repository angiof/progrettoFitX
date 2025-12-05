package com.app.fityo.ui.shedeForms

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.dominio.UsesCasesEssercissi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EsserciziViewModel(private val useCase: UsesCasesEssercissi) : ViewModel() {

    fun insert(essercizi: EsserciziEntity) = viewModelScope.launch(Dispatchers.IO) {
        useCase.insert(essercizi)
    }

    suspend fun getTotalEss(id: Int): Int = useCase.getTotalEss(id)

    fun getAllById(id: Int): LiveData<List<EsserciziEntity>> = useCase.getAllById(id)

    fun delete(essercizi: EsserciziEntity) = viewModelScope.launch(Dispatchers.IO) {
        useCase.delateEss(essercizi)
    }

    fun deleteById(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        useCase.delateEss(id)
    }

    fun update(item: EsserciziEntity) = viewModelScope.launch(Dispatchers.IO) {
        useCase.update(item)
    }
}

