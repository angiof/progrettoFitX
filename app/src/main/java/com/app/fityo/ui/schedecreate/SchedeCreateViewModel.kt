package com.app.fityo.ui.schedecreate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.data_layer.db.repos.EsserciziRepository
import com.app.fityo.data_layer.repository.SchedeRepository
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
    val isSaved: Boolean = false
)

class SchedeCreateViewModel(
    application: Application,
    private val schedeRepository: SchedeRepository,
    private val esserciziRepository: EsserciziRepository,
    private val coachProfileId: Int? = null
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(SchedeCreateState())
    val state: StateFlow<SchedeCreateState> = _state.asStateFlow()

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

            val formData = _state.value.formData
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
                coachProfileId = coachProfileId
            )

            withContext(Dispatchers.IO) {
                if (existingScheda == null) {
                    val newId = schedeRepository.insert(schedeEntity).toInt()
                    schedeEntity.id = newId
                } else {
                    schedeRepository.update(schedeEntity)
                }
            }

            // Osserva esercizi
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

    fun navigateBack(): Boolean {
        return when (_state.value.currentStep) {
            SchedeCreateStep.Form -> false
            SchedeCreateStep.Esercizi -> {
                _state.value = _state.value.copy(currentStep = SchedeCreateStep.Form)
                true
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
}
