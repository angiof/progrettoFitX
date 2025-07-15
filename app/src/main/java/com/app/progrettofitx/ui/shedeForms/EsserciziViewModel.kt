package com.app.progrettofitx.ui.shedeForms

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.progrettofitx.data_layer.db.EsserciziEntity
import com.app.progrettofitx.dominio.UsesCasesEssercissi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EsserciziViewModel(private val getEserciziByIdUseCase: UsesCasesEssercissi) : ViewModel() {


    fun insert(essercizi: EsserciziEntity) = viewModelScope.launch(Dispatchers.IO) {
        getEserciziByIdUseCase.insert(essercizi)
    }

    suspend fun getTotalEss(id: Int): Int {
        return getEserciziByIdUseCase.getTotalEss(id)
    }

    suspend fun getAllById(id: Int): LiveData<List<EsserciziEntity>> {
        return getEserciziByIdUseCase.getAllById(id = id)
    }

    suspend fun delateEsser(essercizi: EsserciziEntity) {
        getEserciziByIdUseCase.delateEss(essercizi = essercizi)
    }

    suspend fun delateEsser(id: Int) {
        getEserciziByIdUseCase.delateEss(id)
    }


    fun update(item: EsserciziEntity) = viewModelScope.launch(Dispatchers.IO) {
        getEserciziByIdUseCase.update(item)
    }





}
