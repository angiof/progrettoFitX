package com.app.progrettofitx.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.progrettofitx.data_layer.db.dao.GruppoMuscolareIntensitaMedia
import com.app.progrettofitx.dominio.GruppoMuscolarePercentuale
import com.app.progrettofitx.ui.shedeForms.SchedeRepository
import kotlinx.coroutines.launch

class DashViewModel(private val repository: SchedeRepository, application: Application) :
    AndroidViewModel(application) {


     val _statusData = MutableLiveData<String>()
     val statusDataLive: LiveData<String> get() = _statusData

    fun setStatusData(value: String) {
        _statusData.value = value
    }


    private val _mediaIntensita = MutableLiveData<List<GruppoMuscolareIntensitaMedia>>()
    val mediaIntensita: LiveData<List<GruppoMuscolareIntensitaMedia>> = _mediaIntensita

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


    fun loadPercentualiGruppiMuscolariInDateRange(startDate: String, endDate: String) {
        viewModelScope.launch {
            val percentuali = repository.getPercentualePerGruppoMuscolareInDateRange(startDate, endDate)
            _percentualiGruppiMuscolari.postValue(percentuali)
        }
    }



    fun loadMediaIntensita(startDate: String, endDate: String) {
        viewModelScope.launch {
            val mediaIntensitaList = repository.getMediaIntensitaPerGruppoMuscolareDateRange(startDate, endDate)
            _mediaIntensita.postValue(mediaIntensitaList)
        }
    }

}
