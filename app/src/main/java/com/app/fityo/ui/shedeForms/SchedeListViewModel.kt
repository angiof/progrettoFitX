package com.app.fityo.ui.shedeForms

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.fityo.R
import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.data_layer.db.repos.CustomValueRepository
import com.app.fityo.data_layer.repository.CoachProfileRepository
import com.app.fityo.data_layer.repository.SchedeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SchedeListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SchedeRepository(DbFit.getDatabase(application).schedeDao())
    private val coachRepository = CoachProfileRepository(DbFit.getDatabase(application).coachProfileDao())
    private val customValueRepository =
        CustomValueRepository(DbFit.getDatabase(application).customValueDao())

    private val _schede = MutableLiveData<List<SchedeEntity>>()
    val schede: LiveData<List<SchedeEntity>> = _schede

    private val _coachProfiles = MutableLiveData<List<CoachProfileEntity>>()
    val coachProfiles: LiveData<List<CoachProfileEntity>> = _coachProfiles

    private val _selectedProfileId = MutableLiveData<Int?>(null)
    val selectedProfileId: LiveData<Int?> = _selectedProfileId

    private val _selectedProfileName = MutableLiveData<String?>(null)
    val selectedProfileName: LiveData<String?> = _selectedProfileName

    // Serve al form esercizio condiviso con il flusso di creazione.
    private val _equipmentOptions = MutableLiveData<List<String>>(emptyList())
    val equipmentOptions: LiveData<List<String>> = _equipmentOptions

    init {
        loadCoachProfiles()
        loadSchede()
        loadEquipmentOptions()
    }

    private fun loadEquipmentOptions() = viewModelScope.launch(Dispatchers.IO) {
        val standard = getApplication<Application>().resources
            .getStringArray(R.array.equipment_options)
            .toList()
        _equipmentOptions.postValue(customValueRepository.getAttrezziWith(standard))
    }

    fun saveAttrezzo(value: String, onResult: (String) -> Unit) = viewModelScope.launch {
        val added = withContext(Dispatchers.IO) { customValueRepository.addAttrezzo(value) }
        if (added) loadEquipmentOptions()
        onResult(if (added) "Attrezzo aggiunto" else "Attrezzo gia presente")
    }

    fun saveEsercizio(value: String, onResult: (String) -> Unit) = viewModelScope.launch {
        val added = withContext(Dispatchers.IO) { customValueRepository.addEsercizio(value) }
        onResult(if (added) "Esercizio aggiunto" else "Esercizio gia presente")
    }

    fun loadCoachProfiles() = viewModelScope.launch(Dispatchers.IO) {
        val profiles = coachRepository.getAllProfilesSync()
        _coachProfiles.postValue(profiles)
    }

    fun setSelectedProfile(profileId: Int?, profileName: String?) {
        _selectedProfileId.value = profileId
        _selectedProfileName.value = profileName
        loadSchede()
    }

    fun loadSchede() = viewModelScope.launch(Dispatchers.IO) {
        val profileId = _selectedProfileId.value
        val data = if (profileId != null) {
            repository.getSchedeByCoachProfile(profileId)
        } else {
            repository.getAllSchede()
        }
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

    fun assignSchedeToProfile(schedeIds: List<Int>, profileId: Int?) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateProfileForSchede(schedeIds, profileId)
        loadSchede()
    }
}

