package com.app.fityo.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.fityo.data_layer.db.dao.GruppoMuscolareIntensitaMedia
import com.app.fityo.dominio.GruppoMuscolarePercentuale
import com.app.fityo.dominio.WeekdayWorkoutCount
import com.app.fityo.data_layer.repository.SchedeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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

    private val _weekFrequency = MutableLiveData<List<WeekdayWorkoutCount>>()
    val weekFrequency: LiveData<List<WeekdayWorkoutCount>> = _weekFrequency

    private val _dashboardStats = MutableLiveData<DashboardStats>()
    val dashboardStats: LiveData<DashboardStats> = _dashboardStats

    init {
        loadPercentualiGruppiMuscolari()
        loadMediaIntensitaAll()
        loadWeekFrequencyAll()
        refreshStats()
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

    fun refreshPercentuali() {
        loadPercentualiGruppiMuscolari()
    }



    fun loadMediaIntensita(startDate: String, endDate: String) {
        viewModelScope.launch {
            val mediaIntensitaList = repository.getMediaIntensitaPerGruppoMuscolareDateRange(startDate, endDate)
            _mediaIntensita.postValue(mediaIntensitaList)
        }
    }

    fun loadMediaIntensitaAll() {
        viewModelScope.launch {
            val data = repository.getMediaIntensitaAll()
            _mediaIntensita.postValue(data)
        }
    }

    fun loadWeekFrequency(startDate: String, endDate: String) {
        viewModelScope.launch {
            val data = repository.getWorkoutCountByWeekday(startDate, endDate)
            _weekFrequency.postValue(data)
        }
    }

    fun loadWeekFrequencyAll() {
        viewModelScope.launch {
            val data = repository.getWorkoutCountByWeekdayAll()
            _weekFrequency.postValue(data)
        }
    }

    fun refreshChartsForRange(start: String, end: String) {
        loadPercentualiGruppiMuscolariInDateRange(start, end)
        loadMediaIntensita(start, end)
        loadWeekFrequency(start, end)
    }

    fun refreshStats() {
        viewModelScope.launch(Dispatchers.IO) {
            val totalSchede = repository.countSchede()
            val favSchede = repository.countFavoriteSchede()
            val totalExercises = repository.countTotalExercises()
            val lastWorkout = repository.getLastWorkoutDate()?.let { formatDate(it) }
            val daysSince = repository.getDaysSinceLastWorkout()
            val mostTrained = repository.getMostTrainedMuscleGroup()
            val avgPerWeek = repository.getAverageWorkoutsPerWeek()
            _dashboardStats.postValue(
                DashboardStats(
                    totalSchede = totalSchede,
                    favoriteSchede = favSchede,
                    totalExercises = totalExercises,
                    lastWorkoutDate = lastWorkout,
                    daysSinceLastWorkout = daysSince,
                    mostTrainedMuscle = mostTrained,
                    avgWorkoutsPerWeek = avgPerWeek
                )
            )
        }
    }

    private fun formatDate(raw: String): String {
        return try {
            val parsed = LocalDate.parse(raw)
            parsed.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()))
        } catch (ex: Exception) {
            raw
        }
    }

}

data class DashboardStats(
    val totalSchede: Int,
    val favoriteSchede: Int,
    val totalExercises: Int,
    val lastWorkoutDate: String?,
    val daysSinceLastWorkout: Int?,
    val mostTrainedMuscle: String?,
    val avgWorkoutsPerWeek: Double?
)

