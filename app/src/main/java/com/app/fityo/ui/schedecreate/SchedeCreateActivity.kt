package com.app.fityo.ui.schedecreate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.app.fityo.R
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.repos.EsserciziRepository
import com.app.fityo.data_layer.repository.CoachProfileRepository
import com.app.fityo.data_layer.repository.SchedeRepository
import com.app.fityo.ui.factory.GenericViewModelFactory
import com.app.fityo.ui.schedecreate.compose.EsercissiListScreen
import com.app.fityo.ui.schedecreate.compose.ProfileOption
import com.app.fityo.ui.schedecreate.compose.RiepilogoScreen
import com.app.fityo.ui.schedecreate.compose.SchedeCreateTheme
import com.app.fityo.ui.schedecreate.compose.SchedeFormScreen

class SchedeCreateActivity : ComponentActivity() {

    companion object {
        const val EXTRA_COACH_PROFILE_ID = "extra_coach_profile_id"
        const val EXTRA_SCHEDA_ID = "extra_scheda_id"
        const val EXTRA_START_AT_EXERCISES = "extra_start_at_exercises"
    }

    private val viewModel: SchedeCreateViewModel by viewModels {
        val db = DbFit.getDatabase(application)
        val coachProfileId = intent.getIntExtra(EXTRA_COACH_PROFILE_ID, -1)
            .takeIf { it != -1 }
        val schedaId = intent.getIntExtra(EXTRA_SCHEDA_ID, -1)
            .takeIf { it != -1 }
        val startAtExercises = intent.getBooleanExtra(EXTRA_START_AT_EXERCISES, false)

        GenericViewModelFactory {
            SchedeCreateViewModel(
                application = application,
                schedeRepository = SchedeRepository(db.schedeDao()),
                esserciziRepository = EsserciziRepository(db.essercissiDao()),
                coachProfileRepository = CoachProfileRepository(db.coachProfileDao()),
                coachProfileId = coachProfileId,
                editSchedaId = schedaId,
                startAtExercises = startAtExercises
            )
        }
    }

    private val intensityOptions by lazy {
        resources.getStringArray(R.array.intensity_options).toList()
    }

    private val muscleGroupOptions by lazy {
        resources.getStringArray(R.array.muscle_group_options).toList()
    }

    private val equipmentOptions by lazy {
        resources.getStringArray(R.array.equipment_options).toList()
    }

    private val trainingStyleOptions by lazy {
        resources.getStringArray(R.array.training_style_options).toList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SchedeCreateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SchedeCreateNavHost(
                        viewModel = viewModel,
                        intensityOptions = intensityOptions,
                        muscleGroupOptions = muscleGroupOptions,
                        equipmentOptions = equipmentOptions,
                        trainingStyleOptions = trainingStyleOptions,
                        onFinish = { finish() }
                    )
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!viewModel.navigateBack()) {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }
}

@Composable
private fun SchedeCreateNavHost(
    viewModel: SchedeCreateViewModel,
    intensityOptions: List<String>,
    muscleGroupOptions: List<String>,
    equipmentOptions: List<String>,
    trainingStyleOptions: List<String>,
    onFinish: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val coachProfiles by viewModel.coachProfiles.collectAsState()
    val esercizi by viewModel.esercizi?.observeAsState(initial = emptyList()) ?: run {
        // Se esercizi e null, restituisci un default
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(emptyList<com.app.fityo.data_layer.db.EsserciziEntity>()) }
    }

    // Converti i profili in ProfileOption
    val profileOptions = coachProfiles.map { profile ->
        ProfileOption(
            id = profile.id,
            name = profile.name,
            avatarColor = profile.avatarColor
        )
    }

    // Osserva quando viene salvato
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onFinish()
        }
    }

    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
        when (state.currentStep) {
            SchedeCreateStep.Form -> {
                SchedeFormScreen(
                    formData = state.formData,
                    intensityOptions = intensityOptions,
                    muscleGroupOptions = muscleGroupOptions,
                    trainingStyleOptions = trainingStyleOptions,
                    profileOptions = profileOptions,
                    isLoading = state.isLoading,
                    loadingMessage = state.autoCompileMessage,
                    errorMessage = state.errorMessage,
                    onFormDataChanged = { viewModel.updateFormData(it) },
                    onNext = { viewModel.navigateToEsercizi() },
                    onAutoCompile = { viewModel.autoCompileScheda(it) },
                    onDismissError = { viewModel.clearError() },
                    onBack = { onFinish() }
                )
            }

            SchedeCreateStep.Esercizi -> {
                EsercissiListScreen(
                    esercizi = esercizi,
                    equipmentOptions = equipmentOptions,
                    onAddEsercizio = { viewModel.addEsercizio(it) },
                    onEditEsercizio = { viewModel.updateEsercizio(it) },
                    onDeleteEsercizio = { viewModel.deleteEsercizio(it) },
                    onNext = { viewModel.navigateToRiepilogo() },
                    onBack = { viewModel.navigateBack() }
                )
            }

            SchedeCreateStep.Riepilogo -> {
                state.schedeEntity?.let { scheda ->
                    RiepilogoScreen(
                        scheda = scheda,
                        esercizi = esercizi,
                        onSaveAndExit = { reminderTime ->
                            viewModel.saveAndExit(reminderTime)
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }
            }
        }
    }
}
