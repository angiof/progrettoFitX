package com.app.fityo.ui.coach

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.fityo.coach.ActiveCoachSession
import com.app.fityo.data_layer.db.CoachAppointmentEntity
import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.data_layer.db.dao.DaoCoachAppointment
import com.app.fityo.data_layer.db.dao.DaoSchede
import com.app.fityo.data_layer.repository.CoachProfileRepository
import com.app.fityo.ui.coach.compose.CoachProfileStats
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CoachViewModel(
    application: Application,
    private val coachRepository: CoachProfileRepository,
    private val schedeDao: DaoSchede,
    private val appointmentDao: DaoCoachAppointment? = null
) : AndroidViewModel(application) {

    // Stati UI
    private val _uiState = MutableStateFlow<CoachUiState>(CoachUiState.Loading)
    val uiState: StateFlow<CoachUiState> = _uiState.asStateFlow()

    // Profili
    val profiles: StateFlow<List<CoachProfileEntity>> = coachRepository.getAllProfiles()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Schede del profilo attivo
    private val _activeProfileSchede = MutableStateFlow<List<SchedeEntity>>(emptyList())
    val activeProfileSchede: StateFlow<List<SchedeEntity>> = _activeProfileSchede.asStateFlow()

    // Stats del profilo attivo
    private val _activeProfileStats = MutableStateFlow(CoachProfileStats())
    val activeProfileStats: StateFlow<CoachProfileStats> = _activeProfileStats.asStateFlow()

    // Appuntamenti del profilo attivo
    private val _activeProfileAppointments = MutableStateFlow<List<CoachAppointmentEntity>>(emptyList())
    val activeProfileAppointments: StateFlow<List<CoachAppointmentEntity>> = _activeProfileAppointments.asStateFlow()

    // Date con appuntamenti (per il calendario)
    private val _appointmentDates = MutableStateFlow<Set<String>>(emptySet())
    val appointmentDates: StateFlow<Set<String>> = _appointmentDates.asStateFlow()

    // Data selezionata nel calendario
    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate.asStateFlow()

    // Profilo in modifica
    private val _editingProfile = MutableStateFlow<CoachProfileEntity?>(null)
    val editingProfile: StateFlow<CoachProfileEntity?> = _editingProfile.asStateFlow()

    // ==================== DATI GLOBALI (TUTTI I PROFILI) ====================

    // Tutti gli appuntamenti di tutti i profili
    private val _allAppointments = MutableStateFlow<List<CoachAppointmentEntity>>(emptyList())
    val allAppointments: StateFlow<List<CoachAppointmentEntity>> = _allAppointments.asStateFlow()

    // Date con appuntamenti (globale)
    private val _globalAppointmentDates = MutableStateFlow<Set<String>>(emptySet())
    val globalAppointmentDates: StateFlow<Set<String>> = _globalAppointmentDates.asStateFlow()

    // Data selezionata nel calendario globale
    private val _globalSelectedDate = MutableStateFlow<String?>(null)
    val globalSelectedDate: StateFlow<String?> = _globalSelectedDate.asStateFlow()

    // Cache dei nomi profili
    private val profileNamesCache = mutableMapOf<Int, String>()

    init {
        loadProfiles()
        loadGlobalAppointments()
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            profiles.collect { list ->
                // Aggiorna cache nomi profili
                list.forEach { profile ->
                    profile.id?.let { id ->
                        profileNamesCache[id] = profile.name
                    }
                }
                if (_uiState.value is CoachUiState.Loading) {
                    _uiState.value = CoachUiState.ProfileList
                }
            }
        }
    }

    // ==================== METODI GLOBALI ====================

    private fun loadGlobalAppointments() {
        viewModelScope.launch {
            appointmentDao?.let { dao ->
                val currentMonth = YearMonth.now()
                val startDate = currentMonth.atDay(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
                val endDate = currentMonth.atEndOfMonth().format(DateTimeFormatter.ISO_LOCAL_DATE)

                val appointments = dao.getAllAppointmentsInRange(startDate, endDate)
                _allAppointments.value = appointments

                val dates = dao.getAllAppointmentDatesInRange(startDate, endDate)
                _globalAppointmentDates.value = dates.toSet()
            }
        }
    }

    fun loadGlobalAppointmentDatesForMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            appointmentDao?.let { dao ->
                val startDate = yearMonth.atDay(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
                val endDate = yearMonth.atEndOfMonth().format(DateTimeFormatter.ISO_LOCAL_DATE)

                val appointments = dao.getAllAppointmentsInRange(startDate, endDate)
                _allAppointments.value = appointments

                val dates = dao.getAllAppointmentDatesInRange(startDate, endDate)
                _globalAppointmentDates.value = dates.toSet()
            }
        }
    }

    fun selectGlobalDate(date: String) {
        _globalSelectedDate.value = date
    }

    fun getProfileName(profileId: Int): String? {
        return profileNamesCache[profileId]
    }

    fun deleteGlobalAppointment(appointment: CoachAppointmentEntity) {
        viewModelScope.launch {
            appointmentDao?.delete(appointment)
            loadGlobalAppointments()
        }
    }

    fun toggleGlobalAppointmentComplete(appointment: CoachAppointmentEntity) {
        viewModelScope.launch {
            appointmentDao?.let { dao ->
                appointment.id?.let { id ->
                    dao.setCompleted(id, !appointment.isCompleted)
                    loadGlobalAppointments()
                }
            }
        }
    }

    fun showCreateProfile() {
        _editingProfile.value = null
        _uiState.value = CoachUiState.EditingProfile
    }

    fun showEditProfile(profile: CoachProfileEntity) {
        _editingProfile.value = profile
        _uiState.value = CoachUiState.EditingProfile
    }

    fun backToProfileList() {
        _editingProfile.value = null
        _selectedDate.value = null
        _uiState.value = CoachUiState.ProfileList
    }

    fun saveProfile(profile: CoachProfileEntity) {
        viewModelScope.launch {
            if (profile.id == null) {
                coachRepository.insert(profile)
            } else {
                coachRepository.update(profile)
                // Aggiorna sessione attiva se necessario
                ActiveCoachSession.updateActiveProfile(profile)
            }
            backToProfileList()
        }
    }

    fun deleteProfile(profile: CoachProfileEntity) {
        viewModelScope.launch {
            profile.id?.let { id ->
                coachRepository.deleteById(id)
                // Se era il profilo attivo, esci dalla modalita coach
                if (ActiveCoachSession.activeProfileId == id) {
                    exitCoachMode()
                }
            }
        }
    }

    fun enterCoachMode(profile: CoachProfileEntity) {
        ActiveCoachSession.enterCoachMode(profile)
        _uiState.value = CoachUiState.ActiveSession
        _selectedDate.value = null

        // Carica schede, stats e appuntamenti per questo profilo
        viewModelScope.launch {
            profile.id?.let { profileId ->
                loadSchedeAndStats(profileId)
                loadAppointments(profileId)
            }
        }
    }

    private suspend fun loadSchedeAndStats(profileId: Int) {
        val schede = schedeDao.getSchedeByCoachProfile(profileId)
        _activeProfileSchede.value = schede

        val stats = CoachProfileStats(
            totalSchede = schedeDao.countSchedeByCoachProfile(profileId),
            favoriteSchede = schedeDao.countFavoriteSchedeByCoach(profileId),
            lastWorkout = schedeDao.getLastWorkoutDateByCoach(profileId)?.let { formatDate(it) },
            mostTrainedMuscle = schedeDao.getMostTrainedMuscleGroupByCoach(profileId)
        )
        _activeProfileStats.value = stats
    }

    private suspend fun loadAppointments(profileId: Int) {
        appointmentDao?.let { dao ->
            val appointments = dao.getAppointmentsByProfileSync(profileId)
            _activeProfileAppointments.value = appointments

            // Carica le date del mese corrente con appuntamenti
            val currentMonth = YearMonth.now()
            val startDate = currentMonth.atDay(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
            val endDate = currentMonth.atEndOfMonth().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val dates = dao.getAppointmentDatesInRange(profileId, startDate, endDate)
            _appointmentDates.value = dates.toSet()
        }
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun clearSelectedDate() {
        _selectedDate.value = null
    }

    fun saveAppointment(appointment: CoachAppointmentEntity) {
        viewModelScope.launch {
            appointmentDao?.let { dao ->
                if (appointment.id == null) {
                    dao.insert(appointment)
                } else {
                    dao.update(appointment)
                }
                // Refresh appointments
                ActiveCoachSession.activeProfileId?.let { loadAppointments(it) }
            }
        }
    }

    fun toggleAppointmentComplete(appointment: CoachAppointmentEntity) {
        viewModelScope.launch {
            appointmentDao?.let { dao ->
                appointment.id?.let { id ->
                    dao.setCompleted(id, !appointment.isCompleted)
                    // Refresh appointments
                    ActiveCoachSession.activeProfileId?.let { loadAppointments(it) }
                }
            }
        }
    }

    fun deleteAppointment(appointment: CoachAppointmentEntity) {
        viewModelScope.launch {
            appointmentDao?.let { dao ->
                dao.delete(appointment)
                // Refresh appointments
                ActiveCoachSession.activeProfileId?.let { loadAppointments(it) }
            }
        }
    }

    fun loadAppointmentDatesForMonth(yearMonth: YearMonth) {
        val profileId = ActiveCoachSession.activeProfileId ?: return
        viewModelScope.launch {
            appointmentDao?.let { dao ->
                val startDate = yearMonth.atDay(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
                val endDate = yearMonth.atEndOfMonth().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val dates = dao.getAppointmentDatesInRange(profileId, startDate, endDate)
                _appointmentDates.value = dates.toSet()
            }
        }
    }

    fun exitCoachMode() {
        ActiveCoachSession.exitCoachMode()
        _activeProfileSchede.value = emptyList()
        _activeProfileStats.value = CoachProfileStats()
        _activeProfileAppointments.value = emptyList()
        _appointmentDates.value = emptySet()
        _selectedDate.value = null
        _uiState.value = CoachUiState.ProfileList
    }

    fun refreshActiveProfileData() {
        val profileId = ActiveCoachSession.activeProfileId ?: return
        viewModelScope.launch {
            loadSchedeAndStats(profileId)
            loadAppointments(profileId)
        }
    }

    private fun formatDate(raw: String): String {
        return try {
            val parsed = LocalDate.parse(raw)
            parsed.format(DateTimeFormatter.ofPattern("dd/MM"))
        } catch (e: Exception) {
            raw
        }
    }
}

sealed class CoachUiState {
    data object Loading : CoachUiState()
    data object ProfileList : CoachUiState()
    data object EditingProfile : CoachUiState()
    data object ActiveSession : CoachUiState()
}
