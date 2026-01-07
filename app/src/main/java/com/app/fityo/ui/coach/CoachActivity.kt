package com.app.fityo.ui.coach

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.app.fityo.coach.ActiveCoachSession
import com.app.fityo.data_layer.db.CoachAppointmentEntity
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.data_layer.repository.CoachProfileRepository
import com.app.fityo.ui.coach.compose.*
import com.app.fityo.ui.compose.SchedeListActivity
import com.app.fityo.ui.schedecreate.SchedeCreateActivity

class CoachActivity : ComponentActivity() {

    private val viewModel: CoachViewModel by viewModels {
        val db = DbFit.getDatabase(application)
        CoachViewModelFactory(
            application = application,
            coachRepository = CoachProfileRepository(db.coachProfileDao()),
            schedeDao = db.schedeDao(),
            appointmentDao = db.coachAppointmentDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CoachTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    val uiState by viewModel.uiState.collectAsState()
                    val profiles by viewModel.profiles.collectAsState()
                    val editingProfile by viewModel.editingProfile.collectAsState()
                    val activeProfile by ActiveCoachSession.activeProfile.collectAsState()
                    val activeSchede by viewModel.activeProfileSchede.collectAsState()
                    val activeStats by viewModel.activeProfileStats.collectAsState()
                    val activeAppointments by viewModel.activeProfileAppointments.collectAsState()
                    val appointmentDates by viewModel.appointmentDates.collectAsState()
                    val selectedDate by viewModel.selectedDate.collectAsState()

                    var showAppointmentDialog by remember { mutableStateOf(false) }
                    var editingAppointment by remember { mutableStateOf<CoachAppointmentEntity?>(null) }

                    val allAppointments by viewModel.allAppointments.collectAsState()
                    val globalAppointmentDates by viewModel.globalAppointmentDates.collectAsState()
                    val globalSelectedDate by viewModel.globalSelectedDate.collectAsState()

                    when (uiState) {
                        is CoachUiState.Loading -> {
                            // Loading state - potrebbe mostrare un indicatore
                        }

                        is CoachUiState.ProfileList -> {
                            CoachMainScreen(
                                profiles = profiles,
                                allAppointments = allAppointments,
                                appointmentDates = globalAppointmentDates,
                                selectedDate = globalSelectedDate,
                                onBack = { finish() },
                                onProfileClick = { viewModel.enterCoachMode(it) },
                                onAddProfile = { viewModel.showCreateProfile() },
                                onEditProfile = { viewModel.showEditProfile(it) },
                                onDeleteProfile = { viewModel.deleteProfile(it) },
                                onDateSelected = { viewModel.selectGlobalDate(it) },
                                onMonthChanged = { viewModel.loadGlobalAppointmentDatesForMonth(it) },
                                onAppointmentClick = { /* Potrebbe navigare al dettaglio */ },
                                onToggleAppointmentComplete = { viewModel.toggleGlobalAppointmentComplete(it) },
                                onDeleteAppointment = { viewModel.deleteGlobalAppointment(it) },
                                getProfileName = { viewModel.getProfileName(it) }
                            )
                        }

                        is CoachUiState.EditingProfile -> {
                            CoachProfileFormScreen(
                                existingProfile = editingProfile,
                                onSave = { viewModel.saveProfile(it) },
                                onBack = { viewModel.backToProfileList() }
                            )
                        }

                        is CoachUiState.ActiveSession -> {
                            activeProfile?.let { profile ->
                                AthleteDetailScreen(
                                    profile = profile,
                                    schede = activeSchede,
                                    appointments = activeAppointments,
                                    appointmentDates = appointmentDates,
                                    stats = activeStats,
                                    selectedDate = selectedDate,
                                    onBack = { viewModel.exitCoachMode() },
                                    onCreateScheda = { navigateToCreateScheda() },
                                    onOpenSchedeList = { navigateToSchedeList(profile.id) },
                                    onSchedaClick = { scheda -> navigateToViewScheda(scheda) },
                                    onAddAppointment = {
                                        editingAppointment = null
                                        showAppointmentDialog = true
                                    },
                                    onDateSelected = { viewModel.selectDate(it) },
                                    onAppointmentClick = { appointment ->
                                        editingAppointment = appointment
                                        showAppointmentDialog = true
                                    },
                                    onToggleAppointmentComplete = { viewModel.toggleAppointmentComplete(it) },
                                    onDeleteAppointment = { viewModel.deleteAppointment(it) }
                                )

                                // Dialog per aggiungere/modificare appuntamento
                                if (showAppointmentDialog) {
                                    AddAppointmentDialog(
                                        profileId = profile.id ?: return@let,
                                        initialDate = selectedDate,
                                        existingAppointment = editingAppointment,
                                        onDismiss = {
                                            showAppointmentDialog = false
                                            editingAppointment = null
                                        },
                                        onSave = { appointment ->
                                            viewModel.saveAppointment(appointment)
                                            showAppointmentDialog = false
                                            editingAppointment = null
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh dati quando si torna dall'activity scheda
        if (ActiveCoachSession.isCoachModeActive) {
            viewModel.refreshActiveProfileData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Esci dalla modalita coach quando l'activity viene distrutta
        ActiveCoachSession.exitCoachMode()
    }

    private fun navigateToCreateScheda() {
        val intent = Intent(this, SchedeCreateActivity::class.java).apply {
            // Passa l'ID del profilo coach attivo
            ActiveCoachSession.activeProfileId?.let {
                putExtra(EXTRA_COACH_PROFILE_ID, it)
            }
        }
        startActivity(intent)
    }

    private fun navigateToSchedeList(profileId: Int?) {
        val intent = Intent(this, SchedeListActivity::class.java).apply {
            profileId?.let {
                putExtra(EXTRA_COACH_PROFILE_ID, it)
            }
        }
        startActivity(intent)
    }

    private fun navigateToViewScheda(scheda: SchedeEntity) {
        // Apre la schermata Apri Schede filtrata per questo profilo
        val intent = Intent(this, SchedeListActivity::class.java).apply {
            ActiveCoachSession.activeProfileId?.let {
                putExtra(EXTRA_COACH_PROFILE_ID, it)
            }
            putExtra(EXTRA_SCHEDA_ID, scheda.id)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_COACH_PROFILE_ID = "extra_coach_profile_id"
        const val EXTRA_SCHEDA_ID = "extra_scheda_id"
    }
}
