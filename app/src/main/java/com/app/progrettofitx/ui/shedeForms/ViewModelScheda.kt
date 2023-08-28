package com.app.progrettofitx.ui.shedeForms

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.progrettofitx.db.DB.DbFit
import com.app.progrettofitx.db.EsserciziEntity
import com.app.progrettofitx.repos.EsserciziRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EsserciziViewModel(application: Application) : AndroidViewModel(application) {

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



    suspend fun getAllById(id: Int): LiveData<List<EsserciziEntity>> {
        return repository.getAllById(id)
    }





}
