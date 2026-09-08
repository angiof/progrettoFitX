package com.app.fityo.ui.shedeForms

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.fityo.R
import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.data_layer.db.repos.CustomValueRepository
import com.app.fityo.data_layer.repository.CoachProfileRepository
import com.app.fityo.data_layer.repository.SchedeRepository
import com.app.fityo.ui.schedecreate.LibraryPrompt
import com.app.fityo.ui.schedecreate.LogoUiState
import com.app.fityo.utils.LogoStore
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

    // Qui non c'e un "fine scheda" come nella creazione: il momento in cui ha senso chiedere
    // e il salvataggio dell'esercizio, che e l'unico salvataggio esplicito di questa schermata.
    private val _libraryPrompt = MutableLiveData<LibraryPrompt?>(null)
    val libraryPrompt: LiveData<LibraryPrompt?> = _libraryPrompt

    // Stesso logo del flusso di creazione: LogoStore e uno solo per tutta l'app.
    private val _logo = MutableLiveData(LogoUiState())
    val logo: LiveData<LogoUiState> = _logo

    init {
        loadCoachProfiles()
        loadSchede()
        loadEquipmentOptions()
        loadLogo()
    }

    /** Pubblica: il logo puo essere cambiato dal flusso di creazione mentre questa vive. */
    fun loadLogo() = viewModelScope.launch {
        val context = getApplication<Application>()
        val bitmap = withContext(Dispatchers.IO) { LogoStore.bitmap(context) }
        _logo.value = LogoUiState(bitmap = bitmap, placement = LogoStore.placement(context))
    }

    fun importLogo(uri: Uri, onResult: (String) -> Unit) = viewModelScope.launch {
        val context = getApplication<Application>()
        val imported = withContext(Dispatchers.IO) { LogoStore.import(context, uri) }
        if (imported) loadLogo()
        onResult(if (imported) "Logo importato" else "Immagine non valida")
    }

    fun removeLogo() = viewModelScope.launch {
        val context = getApplication<Application>()
        withContext(Dispatchers.IO) { LogoStore.remove(context) }
        _logo.value = _logo.value?.copy(bitmap = null)
    }

    fun updateLogoPlacement(placement: LogoStore.Placement) {
        _logo.value = _logo.value?.copy(placement = placement) ?: LogoUiState(placement = placement)
    }

    /** Solo a fine trascinamento, per non riscrivere le preferenze a ogni pixel. */
    fun saveLogoPlacement() {
        _logo.value?.let { LogoStore.savePlacement(getApplication(), it.placement) }
    }

    private fun loadEquipmentOptions() = viewModelScope.launch(Dispatchers.IO) {
        val standard = getApplication<Application>().resources
            .getStringArray(R.array.equipment_options)
            .toList()
        _equipmentOptions.postValue(customValueRepository.getAttrezziWith(standard))
    }

    /** Dopo aver salvato un esercizio: se ha portato voci nuove le offriamo alla libreria. */
    fun checkLibrary(esercizio: EsserciziEntity) = viewModelScope.launch {
        val attrezzo = esercizio.attrezzo.trim()
        val nome = esercizio.nome.trim()

        val prompt = withContext(Dispatchers.IO) {
            val standard = getApplication<Application>().resources
                .getStringArray(R.array.equipment_options)
                .toList()
            val attrezziNoti = customValueRepository.getAttrezziWith(standard)
                .map { it.trim().lowercase() }.toSet()
            val eserciziNoti = customValueRepository.getEsercizi()
                .map { it.trim().lowercase() }.toSet()

            LibraryPrompt(
                attrezzi = listOf(attrezzo)
                    .filter { it.isNotEmpty() && it.lowercase() !in attrezziNoti },
                esercizi = listOf(nome)
                    .filter { it.isNotEmpty() && it.lowercase() !in eserciziNoti },
                reminderTime = null
            )
        }

        if (prompt.attrezzi.isNotEmpty() || prompt.esercizi.isNotEmpty()) {
            _libraryPrompt.value = prompt
        }
    }

    fun confirmLibrary(attrezzi: List<String>, esercizi: List<String>) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            attrezzi.forEach { customValueRepository.addAttrezzo(it) }
            esercizi.forEach { customValueRepository.addEsercizio(it) }
        }
        if (attrezzi.isNotEmpty()) loadEquipmentOptions()
        _libraryPrompt.value = null
    }

    fun dismissLibraryPrompt() {
        _libraryPrompt.value = null
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

