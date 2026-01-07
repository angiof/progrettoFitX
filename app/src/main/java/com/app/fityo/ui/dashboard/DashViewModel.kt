package com.app.fityo.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.data_layer.db.dao.GruppoMuscolareIntensitaMedia
import com.app.fityo.data_layer.repository.CoachProfileRepository
import com.app.fityo.dominio.GruppoMuscolarePercentuale
import com.app.fityo.dominio.WeekdayWorkoutCount
import com.app.fityo.data_layer.repository.SchedeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DashViewModel(
    private val repository: SchedeRepository,
    private val coachRepository: CoachProfileRepository?,
    application: Application
) : AndroidViewModel(application) {

    val _statusData = MutableLiveData<String>()
    val statusDataLive: LiveData<String> get() = _statusData

    fun setStatusData(value: String) {
        _statusData.value = value
    }

    // Profilo selezionato per il filtro (null = tutti i dati)
    private val _selectedProfileId = MutableStateFlow<Int?>(null)
    val selectedProfileId: StateFlow<Int?> = _selectedProfileId.asStateFlow()

    // Nome profilo selezionato per UI
    private val _selectedProfileName = MutableLiveData<String?>(null)
    val selectedProfileName: LiveData<String?> = _selectedProfileName

    // Lista profili disponibili
    private val _coachProfiles = MutableLiveData<List<CoachProfileEntity>>(emptyList())
    val coachProfiles: LiveData<List<CoachProfileEntity>> = _coachProfiles

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
        loadCoachProfiles()
        loadPercentualiGruppiMuscolari()
        loadMediaIntensitaAll()
        loadWeekFrequencyAll()
        refreshStats()
    }

    fun loadCoachProfiles() {
        viewModelScope.launch(Dispatchers.IO) {
            coachRepository?.let { repo ->
                val profiles = repo.getAllProfilesSync()
                _coachProfiles.postValue(profiles)

                // Auto-seleziona il primo profilo se nessuno è selezionato
                if (_selectedProfileId.value == null && profiles.isNotEmpty()) {
                    val firstProfile = profiles.first()
                    _selectedProfileId.value = firstProfile.id
                    _selectedProfileName.postValue(firstProfile.name)
                    // Refresh data for the selected profile
                    viewModelScope.launch {
                        refreshAllDataForProfile()
                    }
                }
            }
        }
    }

    fun setSelectedProfile(profileId: Int?, profileName: String?) {
        _selectedProfileId.value = profileId
        _selectedProfileName.value = profileName
        refreshAllDataForProfile()
    }

    private fun refreshAllDataForProfile() {
        loadPercentualiGruppiMuscolari()
        loadMediaIntensitaAll()
        loadWeekFrequencyAll()
        refreshStats()
    }

    private fun loadPercentualiGruppiMuscolari() {
        viewModelScope.launch {
            val profileId = _selectedProfileId.value
            val percentuali = if (profileId != null) {
                repository.getPercentualeByCoachProfile(profileId)
            } else {
                repository.getPercentualePerGruppoMuscolare()
            }
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
            val profileId = _selectedProfileId.value
            val data = if (profileId != null) {
                repository.getMediaIntensitaByCoachProfile(profileId)
            } else {
                repository.getMediaIntensitaAll()
            }
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
            val profileId = _selectedProfileId.value
            val data = if (profileId != null) {
                repository.getWorkoutCountByWeekdayForCoach(profileId)
            } else {
                repository.getWorkoutCountByWeekdayAll()
            }
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
            val profileId = _selectedProfileId.value
            val totalSchede: Int
            val favSchede: Int
            val totalExercises: Int
            val lastWorkout: String?
            val daysSince: Int?
            val mostTrained: String?
            val avgPerWeek: Double?

            if (profileId != null) {
                totalSchede = repository.countSchedeByCoachProfile(profileId)
                favSchede = repository.countFavoriteSchedeByCoach(profileId)
                totalExercises = 0 // TODO: count exercises by coach if needed
                lastWorkout = repository.getLastWorkoutDateByCoach(profileId)?.let { formatDate(it) }
                daysSince = null // TODO: calculate days since for coach
                mostTrained = repository.getMostTrainedMuscleGroupByCoach(profileId)
                avgPerWeek = null // TODO: calculate avg for coach
            } else {
                totalSchede = repository.countSchede()
                favSchede = repository.countFavoriteSchede()
                totalExercises = repository.countTotalExercises()
                lastWorkout = repository.getLastWorkoutDate()?.let { formatDate(it) }
                daysSince = repository.getDaysSinceLastWorkout()
                mostTrained = repository.getMostTrainedMuscleGroup()
                avgPerWeek = repository.getAverageWorkoutsPerWeek()
            }

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

