package com.app.fityo.ui.schedecreate

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.fityo.R
import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.data_layer.db.repos.CustomValueRepository
import com.app.fityo.data_layer.db.repos.EsserciziRepository
import com.app.fityo.data_layer.excel.ColumnRole
import com.app.fityo.data_layer.excel.DayBlock
import com.app.fityo.data_layer.excel.ExcelImporter
import com.app.fityo.data_layer.excel.ExcelSheet
import com.app.fityo.data_layer.excel.ImportedRow
import com.app.fityo.data_layer.excel.SheetAnalyzer
import com.app.fityo.data_layer.excel.SheetLayout
import com.app.fityo.data_layer.excel.VariantGroup
import com.app.fityo.data_layer.excel.XlsxReader
import com.app.fityo.data_layer.repository.CoachProfileRepository
import com.app.fityo.data_layer.repository.SchedeRepository
import com.app.fityo.schede.ai.GemmaWorkoutPlanGenerator
import com.app.fityo.schede.ai.WorkoutPlanRequest
import com.app.fityo.ui.schedecreate.compose.EsercizioFormData
import com.app.fityo.ui.schedecreate.compose.SchedeFormData
import com.app.fityo.utils.ExportMetadata
import com.app.fityo.utils.PdfExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class SchedeCreateStep {
    object Form : SchedeCreateStep()
    object ImportPreview : SchedeCreateStep()
    object Esercizi : SchedeCreateStep()
    object Riepilogo : SchedeCreateStep()
    object PdfPreview : SchedeCreateStep()
}

data class ExcelImportState(
    val fileName: String,
    val sheets: List<ExcelSheet>,
    val selectedSheet: Int,
    val layout: SheetLayout?,
    val selectedDay: Int,
    val selectedVariant: Int,
    val rows: List<ImportedRow>,
    val knownEquipment: List<String>
) {
    val sheet: ExcelSheet get() = sheets[selectedSheet]
    val day: DayBlock? get() = layout?.days?.getOrNull(selectedDay)
    val variant: VariantGroup? get() = layout?.variants?.getOrNull(selectedVariant)
}

data class SchedeCreateState(
    val currentStep: SchedeCreateStep = SchedeCreateStep.Form,
    val formData: SchedeFormData = SchedeFormData(),
    val schedeEntity: SchedeEntity? = null,
    val esercizi: List<EsserciziEntity> = emptyList(),
    val isLoading: Boolean = false,
    val autoCompileMessage: String? = null,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val importState: ExcelImportState? = null,
    val pdfMeta: ExportMetadata = ExportMetadata()
)

class SchedeCreateViewModel(
    application: Application,
    private val schedeRepository: SchedeRepository,
    private val esserciziRepository: EsserciziRepository,
    private val coachProfileRepository: CoachProfileRepository,
    private val customValueRepository: CustomValueRepository,
    private val coachProfileId: Int? = null,
    private val editSchedaId: Int? = null,
    private val startAtExercises: Boolean = false
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(SchedeCreateState())
    val state: StateFlow<SchedeCreateState> = _state.asStateFlow()
    private val workoutPlanGenerator = GemmaWorkoutPlanGenerator(application)

    private val _coachProfiles = MutableStateFlow<List<CoachProfileEntity>>(emptyList())
    val coachProfiles: StateFlow<List<CoachProfileEntity>> = _coachProfiles.asStateFlow()

    // Voci standard di strings.xml piu quelle salvate dall'utente: il menu attrezzi le mostra insieme.
    private val _equipmentOptions = MutableStateFlow<List<String>>(emptyList())
    val equipmentOptions: StateFlow<List<String>> = _equipmentOptions.asStateFlow()

    // La scheda vive in memoria finche l'utente non conferma nel riepilogo: questi campi
    // tengono traccia di cosa esiste davvero su DB, per capire cosa inserire/aggiornare/cancellare.
    private var persistedSchedaId: Int? = null
    private var persistedEsercizioIds: Set<Int> = emptySet()

    // Gli esercizi non ancora salvati hanno bisogno di un id stabile per la LazyColumn e per
    // edit/delete: usiamo id negativi cosi non collidono mai con quelli veri di Room.
    private var nextDraftId = -1

    init {
        loadCoachProfiles()
        refreshEquipmentOptions()

        if (editSchedaId != null) {
            loadExistingScheda(editSchedaId)
        } else if (coachProfileId != null) {
            _state.value = _state.value.copy(
                formData = _state.value.formData.copy(coachProfileId = coachProfileId)
            )
        }
    }

    private fun loadExistingScheda(schedaId: Int) {
        viewModelScope.launch {
            val scheda = withContext(Dispatchers.IO) {
                schedeRepository.getSchedeById(schedaId)
            } ?: return@launch

            val esercizi = withContext(Dispatchers.IO) {
                esserciziRepository.getAllByIdSync(schedaId)
            }

            persistedSchedaId = scheda.id
            persistedEsercizioIds = esercizi.mapNotNull { it.id }.toSet()

            _state.value = _state.value.copy(
                formData = SchedeFormData(
                    titolo = scheda.titolo,
                    data = scheda.data,
                    intensita = scheda.intesita,
                    selectedMuscleGroups = scheda.getAllGruppiMuscolari().toMutableSet(),
                    notes = scheda.notes ?: "",
                    coachProfileId = scheda.coachProfileId
                ),
                schedeEntity = scheda,
                esercizi = esercizi,
                currentStep = if (startAtExercises) SchedeCreateStep.Esercizi else SchedeCreateStep.Form
            )
        }
    }

    private fun loadCoachProfiles() {
        viewModelScope.launch(Dispatchers.IO) {
            _coachProfiles.value = coachProfileRepository.getAllProfilesSync()
        }
    }

    fun updateFormData(formData: SchedeFormData) {
        _state.value = _state.value.copy(formData = formData)
    }

    fun navigateToEsercizi() {
        _state.value = _state.value.copy(
            currentStep = SchedeCreateStep.Esercizi,
            schedeEntity = buildSchedaEntity(_state.value.formData)
        )
    }

    fun addEsercizio(formData: EsercizioFormData) {
        val esercizio = formData.toEntity(id = nextDraftId--, existing = null)
        _state.value = _state.value.copy(esercizi = _state.value.esercizi + esercizio)
    }

    fun updateEsercizio(formData: EsercizioFormData) {
        val id = formData.id ?: return
        _state.value = _state.value.copy(
            esercizi = _state.value.esercizi.map { existing ->
                if (existing.id == id) formData.toEntity(id, existing) else existing
            }
        )
    }

    fun deleteEsercizio(esercizio: EsserciziEntity) {
        _state.value = _state.value.copy(
            esercizi = _state.value.esercizi.filterNot { it.id == esercizio.id }
        )
    }

    fun navigateToRiepilogo() {
        _state.value = _state.value.copy(
            currentStep = SchedeCreateStep.Riepilogo,
            schedeEntity = buildSchedaEntity(_state.value.formData)
        )
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

            val drafts = exercises.map { plan ->
                EsserciziEntity(
                    id = nextDraftId--,
                    nome = plan.name,
                    attrezzo = plan.equipment.orEmpty(),
                    nSerie = plan.sets.coerceAtLeast(1),
                    nRipetizione = plan.reps.coerceAtLeast(1),
                    insometria = null,
                    intervallo = plan.restSeconds,
                    peso = plan.weightKg,
                    notes = plan.notes,
                    schedaId = DRAFT_SCHEDA_ID
                )
            }

            _state.value = _state.value.copy(
                currentStep = SchedeCreateStep.Esercizi,
                schedeEntity = buildSchedaEntity(updatedFormData),
                esercizi = drafts,
                isLoading = false,
                autoCompileMessage = null
            )
        }
    }

    fun importExcel(uri: Uri, fileName: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            val result = runCatching {
                withContext(Dispatchers.IO) {
                    val sheets = getApplication<Application>().contentResolver
                        .openInputStream(uri)
                        ?.use { XlsxReader.readSheets(it) }
                        ?: throw IllegalStateException("Impossibile aprire il file selezionato")

                    sheets to knownEquipment()
                }
            }

            result.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Errore nella lettura del file Excel"
                )
            }.onSuccess { (sheets, equipment) ->
                // Partiamo dal primo foglio che sembra un allenamento: negli export reali i fogli
                // di dieta o misurazioni vengono prima o dopo senza un ordine affidabile.
                val index = sheets.indexOfFirst { SheetAnalyzer.analyze(it.rows) != null }
                    .takeIf { it >= 0 } ?: 0

                _state.value = _state.value.copy(
                    isLoading = false,
                    currentStep = SchedeCreateStep.ImportPreview,
                    importState = buildImportState(
                        fileName = fileName,
                        sheets = sheets,
                        sheetIndex = index,
                        dayIndex = 0,
                        variantIndex = 0,
                        equipment = equipment
                    )
                )
            }
        }
    }

    fun selectSheet(index: Int) {
        val current = _state.value.importState ?: return
        _state.value = _state.value.copy(
            importState = buildImportState(
                fileName = current.fileName,
                sheets = current.sheets,
                sheetIndex = index,
                dayIndex = 0,
                variantIndex = 0,
                equipment = current.knownEquipment
            )
        )
    }

    fun selectDay(index: Int) = reimport(dayIndex = index)

    fun selectVariant(index: Int) = reimport(variantIndex = index)

    private fun reimport(dayIndex: Int? = null, variantIndex: Int? = null) {
        val current = _state.value.importState ?: return
        _state.value = _state.value.copy(
            importState = buildImportState(
                fileName = current.fileName,
                sheets = current.sheets,
                sheetIndex = current.selectedSheet,
                dayIndex = dayIndex ?: current.selectedDay,
                variantIndex = variantIndex ?: current.selectedVariant,
                equipment = current.knownEquipment
            )
        )
    }

    private fun buildImportState(
        fileName: String,
        sheets: List<ExcelSheet>,
        sheetIndex: Int,
        dayIndex: Int,
        variantIndex: Int,
        equipment: List<String>
    ): ExcelImportState {
        val sheet = sheets[sheetIndex]
        val layout = SheetAnalyzer.analyze(sheet.rows)
        val day = layout?.days?.getOrNull(dayIndex)
        val variant = layout?.variants?.getOrNull(variantIndex)

        val rows = if (layout != null && day != null && variant != null) {
            ExcelImporter.import(sheet.rows, layout, day, variant, equipment)
        } else {
            emptyList()
        }

        return ExcelImportState(
            fileName = fileName,
            sheets = sheets,
            selectedSheet = sheetIndex,
            layout = layout,
            selectedDay = dayIndex,
            selectedVariant = variantIndex,
            rows = rows,
            knownEquipment = equipment
        )
    }

    fun updateImportedRow(row: ImportedRow) {
        val current = _state.value.importState ?: return
        val equipment = current.knownEquipment.map { it.trim().lowercase() }.toSet()

        // L'utente ha corretto i valori a mano: restano solo gli avvisi ancora veri, cioe
        // un attrezzo che continua a non essere tra quelli conosciuti.
        val remaining = row.issues.filter { issue ->
            issue.role == ColumnRole.ATTREZZO &&
                row.attrezzo.isNotBlank() &&
                row.attrezzo.trim().lowercase() !in equipment
        }

        _state.value = _state.value.copy(
            importState = current.copy(
                rows = current.rows.map { existing ->
                    if (existing.sheetRow == row.sheetRow) row.copy(issues = remaining) else existing
                }
            )
        )
    }

    fun addCustomEquipment(value: String) {
        val current = _state.value.importState ?: return
        viewModelScope.launch {
            withContext(Dispatchers.IO) { customValueRepository.addAttrezzo(value) }

            val equipment = withContext(Dispatchers.IO) { knownEquipment() }
            _equipmentOptions.value = equipment
            val known = equipment.map { it.trim().lowercase() }.toSet()

            _state.value = _state.value.copy(
                importState = current.copy(
                    knownEquipment = equipment,
                    rows = current.rows.map { row ->
                        row.copy(
                            issues = row.issues.filterNot { issue ->
                                issue.role == ColumnRole.ATTREZZO &&
                                    row.attrezzo.trim().lowercase() in known
                            }
                        )
                    }
                )
            )
        }
    }

    fun confirmImport() {
        val current = _state.value.importState ?: return
        val drafts = current.rows.map { row ->
            EsserciziEntity(
                id = nextDraftId--,
                nome = row.nome,
                attrezzo = row.attrezzo,
                nSerie = row.nSerie,
                nRipetizione = row.nRipetizioni,
                insometria = row.isometria,
                intervallo = row.intervallo,
                peso = row.peso,
                notes = row.note,
                schedaId = DRAFT_SCHEDA_ID
            )
        }

        _state.value = _state.value.copy(
            currentStep = SchedeCreateStep.Esercizi,
            schedeEntity = buildSchedaEntity(_state.value.formData),
            esercizi = _state.value.esercizi + drafts,
            importState = null
        )
    }

    fun cancelImport() {
        _state.value = _state.value.copy(
            currentStep = SchedeCreateStep.Form,
            importState = null
        )
    }

    private suspend fun knownEquipment(): List<String> {
        val standard = getApplication<Application>().resources
            .getStringArray(R.array.equipment_options)
            .toList()
        val custom = customValueRepository.getAttrezzi()
        val known = standard.map { it.trim().lowercase() }.toSet()
        return standard + custom.filterNot { it.trim().lowercase() in known }
    }

    private fun refreshEquipmentOptions() {
        viewModelScope.launch {
            _equipmentOptions.value = withContext(Dispatchers.IO) { knownEquipment() }
        }
    }

    /** Promuove il nome dell'esercizio ad attrezzo riutilizzabile; il messaggio finisce in un Toast. */
    fun saveAttrezzo(nome: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val added = withContext(Dispatchers.IO) { customValueRepository.addAttrezzo(nome) }
            if (added) refreshEquipmentOptions()
            onResult(if (added) "Attrezzo aggiunto" else "Attrezzo gia presente")
        }
    }

    fun saveNomeComeEsercizio(nome: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val added = withContext(Dispatchers.IO) { customValueRepository.addEsercizio(nome) }
            onResult(if (added) "Esercizio aggiunto" else "Esercizio gia presente")
        }
    }

    fun navigateBack(): Boolean {
        return when (_state.value.currentStep) {
            SchedeCreateStep.Form -> false
            SchedeCreateStep.ImportPreview -> {
                cancelImport()
                true
            }
            SchedeCreateStep.Esercizi -> {
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
            SchedeCreateStep.PdfPreview -> {
                _state.value = _state.value.copy(currentStep = SchedeCreateStep.Riepilogo)
                true
            }
        }
    }

    fun saveAndExit(reminderTime: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val drafts = _state.value.esercizi
            val scheda = buildSchedaEntity(_state.value.formData)

            withContext(Dispatchers.IO) {
                val schedaId = persistedSchedaId?.also {
                    schedeRepository.update(scheda)
                } ?: schedeRepository.insert(scheda).toInt()

                persistedSchedaId = schedaId
                scheda.id = schedaId

                val keptIds = drafts.mapNotNull { it.id }.filter { it > 0 }.toSet()
                (persistedEsercizioIds - keptIds).forEach { esserciziRepository.delateFromId(it) }

                drafts.forEach { draft ->
                    if (draft.id != null && draft.id > 0) {
                        esserciziRepository.update(draft.copy(schedaId = schedaId))
                    } else {
                        esserciziRepository.insert(draft.copy(id = null, schedaId = schedaId))
                    }
                }

                if (reminderTime != null) {
                    schedeRepository.updateTime(schedaId, reminderTime)
                }
            }

            _state.value = _state.value.copy(isLoading = false, isSaved = true)
        }
    }

    /**
     * L'anteprima parte dai dati gia inseriti: titolo e data della scheda, coach dal profilo
     * assegnato. Sono comunque tutti modificabili prima di generare il file.
     */
    fun openPdfPreview() {
        val formData = _state.value.formData
        val meta = _state.value.pdfMeta
        val coach = _coachProfiles.value.firstOrNull { it.id == formData.coachProfileId }?.name

        _state.value = _state.value.copy(
            currentStep = SchedeCreateStep.PdfPreview,
            schedeEntity = buildSchedaEntity(formData),
            pdfMeta = meta.copy(
                customTitle = meta.customTitle.ifBlank { formData.titolo },
                coachName = meta.coachName.ifBlank { coach.orEmpty() },
                startDate = meta.startDate.ifBlank { formData.data }
            )
        )
    }

    fun updatePdfMeta(meta: ExportMetadata) {
        _state.value = _state.value.copy(pdfMeta = meta)
    }

    /** Le correzioni fatte nell'anteprima valgono anche per la scheda salvata: la lista e una sola. */
    fun updateEsercizioEntity(esercizio: EsserciziEntity) {
        _state.value = _state.value.copy(
            esercizi = _state.value.esercizi.map { existing ->
                if (existing.id == esercizio.id) esercizio else existing
            }
        )
    }

    fun exportPdf(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val scheda = buildSchedaEntity(_state.value.formData)
            val result = PdfExporter.exportScheda(
                context = getApplication(),
                scheda = scheda,
                esercizi = _state.value.esercizi,
                meta = _state.value.pdfMeta
            )
            onResult(
                result.fold(
                    onSuccess = { "PDF salvato in Documenti: ${it.name}" },
                    onFailure = { "Errore nell'export PDF: ${it.message}" }
                )
            )
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    private fun buildSchedaEntity(formData: SchedeFormData) = SchedeEntity(
        id = persistedSchedaId,
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

    private fun EsercizioFormData.toEntity(id: Int?, existing: EsserciziEntity?) = EsserciziEntity(
        id = id,
        nome = nome,
        attrezzo = attrezzo,
        nSerie = nSerie,
        nRipetizione = nRipetizioni,
        insometria = isometria,
        intervallo = intervallo,
        peso = peso,
        completed = existing?.completed ?: false,
        notes = existing?.notes,
        wgerId = wgerId,
        schedaId = existing?.schedaId ?: DRAFT_SCHEDA_ID
    )

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

    private companion object {
        // Placeholder finche non conosciamo l'id reale della scheda, assegnato solo al salvataggio.
        const val DRAFT_SCHEDA_ID = 0
    }
}
