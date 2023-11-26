package com.app.progrettofitx.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.progrettofitx.dominio.GruppoMuscolarePercentuale
import com.app.progrettofitx.ui.shedeForms.SchedeRepository
import kotlinx.coroutines.launch

class DashViewModel(private val repository: SchedeRepository, application: Application) :
    AndroidViewModel(application) {


    private val _percentualiGruppiMuscolari = MutableLiveData<List<GruppoMuscolarePercentuale>>()
    val percentualiGruppiMuscolari: LiveData<List<GruppoMuscolarePercentuale>> =
        _percentualiGruppiMuscolari

    init {
        loadPercentualiGruppiMuscolari()
    }

    private fun loadPercentualiGruppiMuscolari() {
        viewModelScope.launch {
            val percentuali = repository.getPercentualePerGruppoMuscolare()
            _percentualiGruppiMuscolari.postValue(percentuali)
        }
    }
}
