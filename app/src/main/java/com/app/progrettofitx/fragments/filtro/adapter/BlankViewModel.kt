package com.app.progrettofitx.fragments.filtro.adapter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.progrettofitx.data_layer.db.DB.DbFit
import com.app.progrettofitx.data_layer.db.SchedeEntity
import com.app.progrettofitx.ui.shedeForms.SchedeRepository
import kotlinx.coroutines.launch

class BlankViewModel(application: Application) : AndroidViewModel(application) {

  private val daoSchede = DbFit.getDatabase(application).schedeDao()
  private val repository = SchedeRepository(daoSchede)

  private val _schede = MutableLiveData<List<SchedeEntity>>()
  val schede: LiveData<List<SchedeEntity>> = _schede

  init {
    loadSchede()
  }

  fun loadSchede() {
    viewModelScope.launch {
      _schede.value = repository.getAllSchede()
    }
  }

  fun loadSchedeInDateRange(start: Long, end: Long) {
    viewModelScope.launch {
      _schede.value = repository.getSchedeInDateRange(start, end)
    }
  }
}