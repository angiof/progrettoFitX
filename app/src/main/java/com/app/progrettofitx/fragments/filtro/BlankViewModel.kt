package com.app.progrettofitx.fragments.filtro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.progrettofitx.data_layer.db.DB.DbFit
import com.app.progrettofitx.data_layer.db.SchedeEntity
import com.app.progrettofitx.ui.shedeForms.SchedeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BlankViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = SchedeRepository(DbFit.getDatabase(application).schedeDao())

    private val _schede = MutableLiveData<List<SchedeEntity>>()
    val schede: LiveData<List<SchedeEntity>> = _schede

    init {
        loadSchede()
    }

    fun loadSchede() = viewModelScope.launch {
        _schede.postValue(repo.getAllSchede())
    }

    fun loadSchedeInDateRange(start: Long, end: Long) = viewModelScope.launch {
        _schede.postValue(repo.getSchedeInDateRange(start, end))
    }

    fun deleteScheda(scheda: SchedeEntity) = viewModelScope.launch {
        repo.delete(scheda)
        loadSchede() // Ricarica le schede dopo la cancellazione
    }


    fun setFavorite(id: Int, isFav: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repo.setFavorite(id, isFav)
    }

    suspend fun getSchedeCount(): Int = withContext(Dispatchers.IO) {
        repo.countSchede()
    }
}