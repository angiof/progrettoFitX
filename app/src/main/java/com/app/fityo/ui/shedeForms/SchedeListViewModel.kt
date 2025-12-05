package com.app.fityo.ui.shedeForms

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.data_layer.repository.SchedeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SchedeListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SchedeRepository(DbFit.getDatabase(application).schedeDao())

    private val _schede = MutableLiveData<List<SchedeEntity>>()
    val schede: LiveData<List<SchedeEntity>> = _schede

    init {
        loadSchede()
    }

    fun loadSchede() = viewModelScope.launch(Dispatchers.IO) {
        val data = repository.getAllSchede()
        _schede.postValue(data)
    }

    fun loadSchedeInDateRange(start: Long, end: Long) = viewModelScope.launch(Dispatchers.IO) {
        val data = repository.getSchedeInDateRange(start, end)
        _schede.postValue(data)
    }

    fun deleteScheda(scheda: SchedeEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(scheda)
        loadSchede()
    }

    fun toggleFavorite(id: Int, newValue: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.setFavorite(id, newValue)
        loadSchede()
    }

    fun completeScheda(id: Int, isCompleted: Boolean, completedDate: String?) = viewModelScope.launch(Dispatchers.IO) {
        repository.setCompleted(id, isCompleted, completedDate)
        loadSchede()
    }

    suspend fun countSchede(): Int = withContext(Dispatchers.IO) {
        repository.countSchede()
    }
}

