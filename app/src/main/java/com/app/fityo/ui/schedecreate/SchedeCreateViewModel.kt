package com.app.fityo.ui.schedecreate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.data_layer.db.repos.EsserciziRepository
import com.app.fityo.data_layer.repository.CoachProfileRepository
import com.app.fityo.data_layer.repository.SchedeRepository
import com.app.fityo.schede.ai.GemmaWorkoutPlanGenerator
import com.app.fityo.schede.ai.WorkoutPlanRequest
import com.app.fityo.ui.schedecreate.compose.EsercizioFormData
import com.app.fityo.ui.schedecreate.compose.SchedeFormData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class SchedeCreateStep {
    object Form : SchedeCreateStep()
    object Esercizi : SchedeCreateStep()
    object Riepilogo : SchedeCreateStep()
}

data class SchedeCreateState(
    val currentStep: SchedeCreateStep = SchedeCreateStep.Form,
    val formData: SchedeFormData = SchedeFormData(),
    val schedeEntity: SchedeEntity? = null,
    val isLoading: Boolean = false,
    val autoCompileMessage: String? = null,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

class SchedeCreateViewModel(
    application: Application,
    private val schedeRepository: SchedeRepository,
    private val esserciziRepository: EsserciziRepository,
    private val coachProfileRepository: CoachProfileRepository,
    private val coachProfileId: Int? = null,
    private val editSchedaId: Int? = null,
    private val startAtExercises: Boolean = false
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(SchedeCreateState())
    val state: StateFlow<SchedeCreateState> = _state.asStateFlow()
    private val workoutPlanGenerator = GemmaWorkoutPlanGenerator(application)

    private val _coachProfiles = MutableStateFlow<List<CoachProfileEntity>>(emptyList())
    val coachProfiles: StateFlow<List<CoachProfileEntity>> = _coachProfiles.asStateFlow()

    init {
        loadCoachProfiles()

        // Se c'è un editSchedaId, carica la scheda esistente
        if (editSchedaId != null) {
            loadExistingScheda(editSchedaId)
        } else if (coachProfileId != null) {
            // Se c'è un coachProfileId dall'intent, impostalo nel formData
            _state.value = _state.value.copy(
                formData = _state.value.formData.copy(coachProfileId = coachProfileId)
            )
        }
    }

    private fun loadExistingScheda(schedaId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val scheda = schedeRepository.getSchedeById(schedaId) ?: return@launch

            // Converti la scheda esistente in formData
            val formData = SchedeFormData(
                titolo = scheda.titolo,
                data = scheda.data,
                intensita = scheda.intesita,
                selectedMuscleGroups = scheda.getAllGruppiMuscolari().toMutableSet(),
                notes = scheda.notes ?: "",
                coachProfileId = scheda.coachProfileId
            )

            // Carica gli esercizi
            _esercizi = esserciziRepository.getAllById(schedaId)

            withContext(Dispatchers.Main) {
                _state.value = _state.value.copy(
                    formData = formData,
                    schedeEntity = scheda,
                    currentStep = if (startAtExercises) SchedeCreateStep.Esercizi else SchedeCreateStep.Form
                )
            }
        }
    }

    private fun loadCoachProfiles() {
        viewModelScope.launch(Dispatchers.IO) {
            val profiles = coachProfileRepository.getAllProfilesSync()
            _coachProfiles.value = profiles
        }
    }

    // Esercizi list - LiveData from repository
    private var _esercizi: LiveData<List<EsserciziEntity>>? = null
    val esercizi: LiveData<List<EsserciziEntity>>?
        get() = _esercizi

    fun updateFormData(formData: SchedeFormData) {
        _state.value = _state.value.copy(formData = formData)
    }

    fun navigateToEsercizi() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val schedeEntity = upsertScheda(_state.value.formData)
            _esercizi = esserciziRepository.getAllById(schedeEntity.id!!)

            _state.value = _state.value.copy(
                currentStep = SchedeCreateStep.Esercizi,
                schedeEntity = schedeEntity,
                isLoading = false
            )
        }
    }

    fun addEsercizio(formData: EsercizioFormData) {
        viewModelScope.launch(Dispatchers.IO) {
            val schedaId = _state.value.schedeEntity?.id ?: return@launch

            val esercizio = EsserciziEntity(
                nome = formData.nome,
                attrezzo = formData.attrezzo,
                nSerie = formData.nSerie,
                nRipetizione = formData.nRipetizioni,
                insometria = formData.isometria,
                intervallo = formData.intervallo,
                peso = formData.peso,
                wgerId = formData.wgerId,
                schedaId = schedaId
            )

            esserciziRepository.insert(esercizio)
        }
    }

    fun updateEsercizio(formData: EsercizioFormData) {
        viewModelScope.launch(Dispatchers.IO) {
            val schedaId = _state.value.schedeEntity?.id ?: return@launch

            val esercizio = EsserciziEntity(
                id = formData.id,
                nome = formData.nome,
                attrezzo = formData.attrezzo,
                nSerie = formData.nSerie,
                nRipetizione = formData.nRipetizioni,
                insometria = formData.isometria,
                intervallo = formData.intervallo,
                peso = formData.peso,
                wgerId = formData.wgerId,
                schedaId = schedaId
            )

            esserciziRepository.update(esercizio)
        }
    }

    fun deleteEsercizio(esercizio: EsserciziEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            esserciziRepository.delete(esercizio)
        }
    }

    fun navigateToRiepilogo() {
        _state.value = _state.value.copy(currentStep = SchedeCreateStep.Riepilogo)
    }

    fun autoCompileScheda(options: AutoCompileOptions) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                autoCompileMessage = "1/3 Generazione IA...",
                errorMessage = null
            )

            val updatedFormData = applyAutoDefaults(_state.value.formData, options)
            _state.value = _state.value.copy(formData = updatedFormData)

            val schedeEntity = upsertScheda(updatedFormData)

            val request = WorkoutPlanRequest(
                style = options.style,
                intensity = updatedFormData.intensita,
                muscleGroups = updatedFormData.selectedMuscleGroups.toList()
            )

            val planResult = workoutPlanGenerator.generate(request)
            if (planResult.isFailure) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    autoCompileMessage = null,
                    errorMessage = planResult.exceptionOrNull()?.message ?: "Errore generazione scheda"
                )
                return@launch
            }

            _state.value = _state.value.copy(autoCompileMessage = "2/3 Parsing risposta...")
            val exercises = planResult.getOrThrow().exercises
            _state.value = _state.value.copy(autoCompileMessage = "3/3 Creazione esercizi...")

            withContext(Dispatchers.IO) {
                esserciziRepository.deleteBySchedaId(schedeEntity.id!!)
            }

            exercises.forEachIndexed { index, plan ->
                val label = plan.name.take(40)
                _state.value = _state.value.copy(
                    autoCompileMessage = "3/3 Aggiungo: $label (${index + 1}/${exercises.size})"
                )
                withContext(Dispatchers.IO) {
                    val esercizio = EsserciziEntity(
                        nome = plan.name,
                        attrezzo = plan.equipment.orEmpty(),
                        nSerie = plan.sets.coerceAtLeast(1),
                        nRipetizione = plan.reps.coerceAtLeast(1),
                        insometria = null,
                        intervallo = plan.restSeconds,
                        peso = plan.weightKg,
                        notes = plan.notes,
                        schedaId = schedeEntity.id!!
                    )
                    esserciziRepository.insert(esercizio)
                }
            }

            _esercizi = esserciziRepository.getAllById(schedeEntity.id!!)
            _state.value = _state.value.copy(
                currentStep = SchedeCreateStep.Esercizi,
                schedeEntity = schedeEntity,
                isLoading = false,
                autoCompileMessage = null
            )
        }
    }

    fun navigateBack(): Boolean {
        return when (_state.value.currentStep) {
            SchedeCreateStep.Form -> false
            SchedeCreateStep.Esercizi -> {
                // If we started at exercises (e.g., adding to existing scheda), 
                // don't allow going back to Form step - close the activity instead
                if (startAtExercises) {
                    false
                } else {
                    _state.value = _state.value.copy(currentStep = SchedeCreateStep.Form)
                    true
                }
            }
            SchedeCreateStep.Riepilogo -> {
                _state.value = _state.value.copy(currentStep = SchedeCreateStep.Esercizi)
                true
            }
        }
    }

    fun saveAndExit(reminderTime: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val scheda = _state.value.schedeEntity ?: return@launch

            if (reminderTime != null) {
                schedeRepository.updateTime(scheda.id!!, reminderTime)
            }

            withContext(Dispatchers.Main) {
                _state.value = _state.value.copy(isSaved = true)
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    private suspend fun upsertScheda(formData: SchedeFormData): SchedeEntity {
        val existingScheda = _state.value.schedeEntity
        val schedeEntity = SchedeEntity(
            id = existingScheda?.id,
            titolo = formData.titolo,
            data = formData.data,
            intesita = formData.intensita,
            gruppoMuscolare = formData.selectedMuscleGroups.firstOrNull() ?: "",
            gruppiMuscolari = if (formData.selectedMuscleGroups.size > 1)
                formData.selectedMuscleGroups.toList()
            else
                null,
            notes = formData.notes.ifBlank { null },
            coachProfileId = formData.coachProfileId
        )

        withContext(Dispatchers.IO) {
            if (existingScheda == null) {
                val newId = schedeRepository.insert(schedeEntity).toInt()
                schedeEntity.id = newId
            } else {
                schedeRepository.update(schedeEntity)
            }
        }

        return schedeEntity
    }

    private fun applyAutoDefaults(formData: SchedeFormData, options: AutoCompileOptions): SchedeFormData {
        val title = if (formData.titolo.isNotBlank()) {
            formData.titolo
        } else {
            val groupLabel = options.muscleGroups.firstOrNull() ?: "Full Body"
            "${options.style} - $groupLabel"
        }

        val date = if (formData.data.isNotBlank()) {
            formData.data
        } else {
            java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
        }

        return formData.copy(
            titolo = title,
            data = date,
            intensita = options.intensity,
            selectedMuscleGroups = options.muscleGroups
        )
    }
}
