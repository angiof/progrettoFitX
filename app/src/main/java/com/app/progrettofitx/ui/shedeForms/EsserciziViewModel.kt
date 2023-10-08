package com.app.progrettofitx.ui.shedeForms

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.progrettofitx.data_layer.db.DB.DbFit
import com.app.progrettofitx.data_layer.db.EsserciziEntity
import com.app.progrettofitx.data_layer.db.repos.EsserciziRepository
import com.app.progrettofitx.dominio.UsesCasesEssercissi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EsserciziViewModel(private val getEserciziByIdUseCase: UsesCasesEssercissi, application: Application) : ViewModel() {

    private val repository: EsserciziRepository
    // Your other ViewModel code and LiveData variables

    init {
        val esserciziDao = DbFit.getDatabase(application).essercissiDao()
        repository = EsserciziRepository(esserciziDao)
    }

    // ViewModel functions that use the repository
    fun insert(essercizi: EsserciziEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(essercizi)
    }

    suspend fun getTotalEss(id: Int): Int {
        return repository.getCountById(id)
    }

    suspend fun getAllById(id: Int): LiveData<List<EsserciziEntity>> {
      return  getEserciziByIdUseCase.getAllById(id = id)
    }


}
