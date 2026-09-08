package com.app.fityo.ui.schedecreate

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
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
import com.app.fityo.utils.FitxImportExport
import com.app.fityo.utils.LogoStore
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
    val fileUri: String,
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

data class LogoUiState(
    val bitmap: Bitmap? = null,
    val placement: LogoStore.Placement = LogoStore.Placement()
)

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
    val pdfMeta: ExportMetadata = ExportMetadata(),
    val showExitDialog: Boolean = false,
    val closeRequested: Boolean = false,

    // Modalita avanzata: una scheda semplice e semplicemente una scheda con una settimana
    // e un giorno, quindi questi campi non cambiano cosa viene salvato, solo cosa si vede.
    val advancedMode: Boolean = false,
    val weekCount: Int = 1,
    val dayCount: Int = 1,
    val selectedWeek: Int = 1,
    val selectedDay: Int = 1,
    val source: SchedaSource? = null,

    // Un'altra scheda nata dallo stesso file: lo diciamo prima che l'utente ne crei un doppione.
    val duplicateSourceWarning: String? = null,
    val esportazione: EsportazioneState = EsportazioneState(),
    val libraryPrompt: LibraryPrompt? = null
) {
    /** Gli esercizi del giorno aperto; in modalita semplice sono tutti. */
    val visibleEsercizi: List<EsserciziEntity>
        get() = if (!advancedMode) {
            esercizi.sortedBy { it.ordine }
        } else {
            esercizi
                .filter { it.settimana == selectedWeek && it.giorno == selectedDay }
                .sortedBy { it.ordine }
        }
}

/** Una settimana ha sette giorni: il resto e un errore di battitura, non una scheda. */
const val MAX_GIORNI_SETTIMANA = 7

/** Ritocchi rapidi applicati a tutta la settimana appena copiata. */
enum class Progressione { PESO, RIPETIZIONI, PERCENTUALE }

/** Le uscite possibili della scheda: tutte passano dallo stesso pannello. */
enum class EsportazioneTipo { PDF, PDF_CONDIVISO, FITX }

/**
 * Una esportazione alla volta, con l'esito che resta visibile nel pannello: un Toast che
 * scompare non basta quando l'operazione puo fallire per permessi o spazio.
 */
data class EsportazioneState(
    val inCorso: EsportazioneTipo? = null,
    val esito: String? = null,
    val errore: Boolean = false
)

/**
 * Nomi e attrezzi comparsi nella scheda che non sono ancora nella libreria dell'utente.
 * Si chiede una volta sola alla fine, invece di salvare di nascosto mentre si compila.
 */
data class LibraryPrompt(
    val attrezzi: List<String>,
    val esercizi: List<String>,
    val reminderTime: String?
)

/** Da dove arriva la scheda, quando arriva da un foglio Excel. */
data class SchedaSource(
    val fileName: String,
    val uri: String,
    val sheet: Int
)

class SchedeCreateViewModel(
    application: Application,
    private val schedeRepository: SchedeRepository,
    private val esserciziRepository: EsserciziRepository,
    private val coachProfileRepository: CoachProfileRepository,
    private val customValueRepository: CustomValueRepository,
    private val coachProfileId: Int? = null,
    private val editSchedaId: Int? = null,
    private val duplicateSchedaId: Int? = null,
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

    // Logo del PDF: resta quello di default finche l'utente non lo cambia o lo toglie.
    private val _logo = MutableStateFlow(LogoUiState())
    val logo: StateFlow<LogoUiState> = _logo.asStateFlow()

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
        loadLogo()

        if (editSchedaId != null) {
            loadExistingScheda(editSchedaId)
        } else if (duplicateSchedaId != null) {
            loadSchedaAsCopy(duplicateSchedaId)
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
                currentStep = if (startAtExercises) SchedeCreateStep.Esercizi else SchedeCreateStep.Form,
                source = scheda.sourceUri?.let { uri ->
                    SchedaSource(
                        fileName = scheda.sourceFile.orEmpty(),
                        uri = uri,
                        sheet = scheda.sourceSheet ?: 0
                    )
                }
            ).withStructureFrom(esercizi)
        }
    }

    /**
     * Nuova scheda che parte da una esistente: copiamo contenuto ed esercizi ma NON gli id,
     * cosi il salvataggio finale crea una scheda nuova invece di sovrascrivere l'originale.
     */
    private fun loadSchedaAsCopy(schedaId: Int) {
        viewModelScope.launch {
            val scheda = withContext(Dispatchers.IO) {
                schedeRepository.getSchedeById(schedaId)
            } ?: return@launch

            val esercizi = withContext(Dispatchers.IO) {
                esserciziRepository.getAllByIdSync(schedaId)
            }

            _state.value = _state.value.copy(
                formData = SchedeFormData(
                    titolo = "Copia di ${scheda.titolo}",
                    data = scheda.data,
                    intensita = scheda.intesita,
                    selectedMuscleGroups = scheda.getAllGruppiMuscolari().toMutableSet(),
                    notes = scheda.notes ?: "",
                    coachProfileId = coachProfileId ?: scheda.coachProfileId
                ),
                // La copia non eredita la provenienza: e una scheda nuova, e riscrivere nel
                // foglio del cliente originale sarebbe esattamente la cosa sbagliata.
                esercizi = esercizi.map { esercizio ->
                    esercizio.copy(
                        id = nextDraftId--,
                        completed = false,
                        schedaId = DRAFT_SCHEDA_ID,
                        sourceRow = null,
                        sourceVariant = null
                    )
                },
                source = null
            ).withStructureFrom(esercizi)
        }
    }

    /**
     * Riaprendo una scheda gia complessa i tab devono esserci subito: la struttura si deduce
     * dagli esercizi salvati, non serve un flag sulla scheda.
     */
    private fun SchedeCreateState.withStructureFrom(esercizi: List<EsserciziEntity>): SchedeCreateState {
        val weeks = esercizi.maxOfOrNull { it.settimana } ?: 1
        val days = (esercizi.maxOfOrNull { it.giorno } ?: 1).coerceAtMost(MAX_GIORNI_SETTIMANA)
        return copy(
            weekCount = weeks,
            dayCount = days,
            advancedMode = weeks > 1 || days > 1 || esercizi.any { it.supersetGroup != null }
        )
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
        val current = _state.value
        val week = if (current.advancedMode) current.selectedWeek else 1
        val day = if (current.advancedMode) current.selectedDay else 1
        val previous = current.esercizi
            .filter { it.settimana == week && it.giorno == day }
            .maxByOrNull { it.ordine }

        val (esercizi, group) = linkToPrevious(current.esercizi, previous, formData.legaAlPrecedente)

        val esercizio = formData.toEntity(id = nextDraftId--, existing = null).copy(
            settimana = week,
            giorno = day,
            ordine = (previous?.ordine ?: -1) + 1,
            supersetGroup = group
        )

        _state.value = current.copy(esercizi = esercizi + esercizio)
    }

    fun updateEsercizio(formData: EsercizioFormData) {
        val id = formData.id ?: return
        val current = _state.value
        val existing = current.esercizi.firstOrNull { it.id == id } ?: return

        val siblings = current.esercizi
            .filter { it.settimana == existing.settimana && it.giorno == existing.giorno }
            .sortedBy { it.ordine }
        val previous = siblings.getOrNull(siblings.indexOfFirst { it.id == id } - 1)

        val (esercizi, group) = linkToPrevious(current.esercizi, previous, formData.legaAlPrecedente)

        _state.value = current.copy(
            esercizi = esercizi.map { entity ->
                if (entity.id == id) {
                    formData.toEntity(id, existing).copy(
                        settimana = existing.settimana,
                        giorno = existing.giorno,
                        ordine = existing.ordine,
                        supersetGroup = group
                    )
                } else {
                    entity
                }
            }
        )
    }

    /**
     * Il superset e un numero condiviso dagli esercizi consecutivi: se il precedente non ne ha
     * ancora uno glielo assegniamo qui, cosi la coppia risulta legata da entrambi i lati.
     */
    private fun linkToPrevious(
        esercizi: List<EsserciziEntity>,
        previous: EsserciziEntity?,
        link: Boolean
    ): Pair<List<EsserciziEntity>, Int?> {
        if (!link || previous == null) return esercizi to null

        val group = previous.supersetGroup
            ?: ((esercizi.mapNotNull { it.supersetGroup }.maxOrNull() ?: 0) + 1)

        val updated = if (previous.supersetGroup == null) {
            esercizi.map { if (it.id == previous.id) it.copy(supersetGroup = group) else it }
        } else {
            esercizi
        }
        return updated to group
    }

    fun deleteEsercizio(esercizio: EsserciziEntity) {
        _state.value = _state.value.copy(
            esercizi = _state.value.esercizi.filterNot { it.id == esercizio.id }
        )
    }

    fun setAdvancedMode(enabled: Boolean) {
        val current = _state.value
        // Si puo tornare semplice solo se non c'e niente da nascondere.
        if (!enabled && (current.weekCount > 1 || current.dayCount > 1)) return
        _state.value = current.copy(advancedMode = enabled)
    }

    fun selectWeek(week: Int) {
        _state.value = _state.value.copy(selectedWeek = week)
    }

    /** Nota: selectDay() e gia il giorno del foglio Excel, questo e il giorno della scheda. */
    fun selectTrainingDay(day: Int) {
        _state.value = _state.value.copy(selectedDay = day)
    }

    /**
     * La settimana nuova nasce come copia di quella corrente: al trainer resta da ritoccare i
     * carichi, non da riscrivere gli esercizi. I superset vengono rinumerati per non condividere
     * il gruppo con la settimana di partenza.
     */
    fun addWeek() {
        val current = _state.value
        val source = current.esercizi.filter { it.settimana == current.selectedWeek }
        val newWeek = current.weekCount + 1

        var nextGroup = (current.esercizi.mapNotNull { it.supersetGroup }.maxOrNull() ?: 0) + 1
        val groupMap = mutableMapOf<Int, Int>()

        val copies = source.map { esercizio ->
            esercizio.copy(
                id = nextDraftId--,
                settimana = newWeek,
                completed = false,
                supersetGroup = esercizio.supersetGroup?.let { old ->
                    groupMap.getOrPut(old) { nextGroup++ }
                }
            )
        }

        _state.value = current.copy(
            esercizi = current.esercizi + copies,
            weekCount = newWeek,
            selectedWeek = newWeek,
            advancedMode = true
        )
    }

    fun addDay() {
        val current = _state.value
        if (current.dayCount >= MAX_GIORNI_SETTIMANA) return

        val newDay = current.dayCount + 1
        _state.value = current.copy(
            dayCount = newDay,
            selectedDay = newDay,
            advancedMode = true
        )
    }

    /** Settimana nuova senza esercizi, per chi non vuole partire da una copia. */
    fun addEmptyWeek() {
        val current = _state.value
        val newWeek = current.weekCount + 1
        _state.value = current.copy(
            weekCount = newWeek,
            selectedWeek = newWeek,
            advancedMode = true
        )
    }

    fun deleteWeek(week: Int) {
        val current = _state.value
        if (current.weekCount <= 1) return

        val remaining = current.esercizi
            .filterNot { it.settimana == week }
            .map { if (it.settimana > week) it.copy(settimana = it.settimana - 1) else it }

        _state.value = current.copy(
            esercizi = remaining,
            weekCount = current.weekCount - 1,
            selectedWeek = current.selectedWeek.coerceAtMost(current.weekCount - 1)
        )
    }

    fun deleteDay(day: Int) {
        val current = _state.value
        if (current.dayCount <= 1) return

        val remaining = current.esercizi
            .filterNot { it.giorno == day }
            .map { if (it.giorno > day) it.copy(giorno = it.giorno - 1) else it }

        _state.value = current.copy(
            esercizi = remaining,
            dayCount = current.dayCount - 1,
            selectedDay = current.selectedDay.coerceAtMost(current.dayCount - 1)
        )
    }

    /** Applica un ritocco a tutti gli esercizi della settimana aperta. */
    fun applyProgressione(tipo: Progressione) {
        val current = _state.value
        _state.value = current.copy(
            esercizi = current.esercizi.map { esercizio ->
                if (esercizio.settimana != current.selectedWeek) {
                    esercizio
                } else {
                    when (tipo) {
                        Progressione.PESO ->
                            esercizio.peso?.let { esercizio.copy(peso = it + 2.5f) } ?: esercizio
                        Progressione.RIPETIZIONI ->
                            esercizio.copy(nRipetizione = esercizio.nRipetizione + 1)
                        Progressione.PERCENTUALE ->
                            esercizio.peso?.let {
                                esercizio.copy(peso = Math.round(it * 1.05f * 10f) / 10f)
                            } ?: esercizio
                    }
                }
            }
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

            val drafts = exercises.mapIndexed { index, plan ->
                EsserciziEntity(
                    id = nextDraftId--,
                    ordine = index,
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
            keepAccessTo(uri)

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
                        fileUri = uri.toString(),
                        sheets = sheets,
                        sheetIndex = index,
                        dayIndex = 0,
                        variantIndex = 0,
                        equipment = equipment
                    )
                )

                checkExistingImport(uri.toString(), index)
            }
        }
    }

    /**
     * L'Uri del picker vale solo per questa esecuzione: senza permesso persistente il
     * riferimento salvato sulla scheda sarebbe inutilizzabile al riavvio. Chiediamo anche la
     * scrittura, che servira per riscrivere il foglio, ma senza pretenderla: parecchi provider
     * (Drive in testa) concedono solo lettura.
     */
    private fun keepAccessTo(uri: Uri) {
        val resolver = getApplication<Application>().contentResolver
        val readWrite = Intent.FLAG_GRANT_READ_URI_PERMISSION or
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        runCatching { resolver.takePersistableUriPermission(uri, readWrite) }
            .onFailure {
                runCatching {
                    resolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }
    }

    /**
     * Lo stesso foglio importato due volte creerebbe due schede identiche senza dire niente:
     * qui non blocchiamo, avvisiamo e basta, perche puo anche essere voluto.
     */
    private fun checkExistingImport(uri: String, sheet: Int) {
        viewModelScope.launch {
            val existing = withContext(Dispatchers.IO) {
                schedeRepository.getSchedeBySource(uri, sheet)
            }.filter { it.id != persistedSchedaId }

            _state.value = _state.value.copy(
                duplicateSourceWarning = existing.firstOrNull()?.let {
                    "Questo foglio e gia stato importato nella scheda \"${it.titolo}\""
                }
            )
        }
    }

    fun selectSheet(index: Int) {
        val current = _state.value.importState ?: return
        checkExistingImport(current.fileUri, index)
        _state.value = _state.value.copy(
            importState = buildImportState(
                fileName = current.fileName,
                fileUri = current.fileUri,
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
                fileUri = current.fileUri,
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
        fileUri: String,
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
            fileUri = fileUri,
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
        val state = _state.value
        val week = if (state.advancedMode) state.selectedWeek else 1
        val day = if (state.advancedMode) state.selectedDay else 1

        // Gli esercizi importati si accodano a quelli gia presenti nello stesso giorno.
        val firstOrder = (state.esercizi
            .filter { it.settimana == week && it.giorno == day }
            .maxOfOrNull { it.ordine } ?: -1) + 1

        val drafts = current.rows.mapIndexed { index, row ->
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
                schedaId = DRAFT_SCHEDA_ID,
                settimana = week,
                giorno = day,
                ordine = firstOrder + index,
                // Riga e gruppo di colonne di origine: e quello che permettera di riscrivere
                // il foglio senza doverlo re-interpretare a naso.
                sourceRow = row.sheetRow,
                sourceVariant = current.selectedVariant
            )
        }

        val source = SchedaSource(
            fileName = current.fileName,
            uri = current.fileUri,
            sheet = current.selectedSheet
        )

        _state.value = _state.value.copy(
            currentStep = SchedeCreateStep.Esercizi,
            schedeEntity = buildSchedaEntity(_state.value.formData, source),
            esercizi = _state.value.esercizi + drafts,
            importState = null,
            source = source
        )
    }

    fun cancelImport() {
        _state.value = _state.value.copy(
            currentStep = SchedeCreateStep.Form,
            importState = null
        )
    }

    private suspend fun knownEquipment(): List<String> = customValueRepository.getAttrezziWith(
        getApplication<Application>().resources.getStringArray(R.array.equipment_options).toList()
    )

    private fun refreshEquipmentOptions() {
        viewModelScope.launch {
            _equipmentOptions.value = withContext(Dispatchers.IO) { knownEquipment() }
        }
    }

    /**
     * Il back non chiude mai di sua iniziativa: finche la scheda vive solo in memoria uscire
     * significa perderla, quindi ai bordi del flusso chiediamo conferma con un dialog.
     */
    fun navigateBack(): Boolean {
        return when (_state.value.currentStep) {
            SchedeCreateStep.Form -> {
                requestExit()
                true
            }
            SchedeCreateStep.ImportPreview -> {
                cancelImport()
                true
            }
            SchedeCreateStep.Esercizi -> {
                if (startAtExercises) {
                    requestExit()
                } else {
                    _state.value = _state.value.copy(currentStep = SchedeCreateStep.Form)
                }
                true
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

    fun requestExit() {
        if (hasUnsavedContent()) {
            _state.value = _state.value.copy(showExitDialog = true)
        } else {
            _state.value = _state.value.copy(closeRequested = true)
        }
    }

    fun dismissExitDialog() {
        _state.value = _state.value.copy(showExitDialog = false)
    }

    fun discardAndExit() {
        _state.value = _state.value.copy(showExitDialog = false, closeRequested = true)
    }

    /** Un titolo scritto o anche un solo esercizio bastano: e roba che l'utente perderebbe. */
    private fun hasUnsavedContent(): Boolean {
        val current = _state.value
        return current.esercizi.isNotEmpty() ||
            current.formData.titolo.isNotBlank() ||
            current.formData.notes.isNotBlank()
    }

    /**
     * Passaggio obbligato del salvataggio: se la scheda ha introdotto attrezzi o esercizi nuovi
     * lo chiediamo prima di uscire, altrimenti si salva e basta.
     */
    fun requestSave(reminderTime: String?) {
        viewModelScope.launch {
            val esercizi = _state.value.esercizi

            val (nuoviAttrezzi, nuoviEsercizi) = withContext(Dispatchers.IO) {
                val attrezziNoti = knownEquipment().map { it.trim().lowercase() }.toSet()
                val eserciziNoti = customValueRepository.getEsercizi()
                    .map { it.trim().lowercase() }.toSet()

                val attrezzi = esercizi.map { it.attrezzo.trim() }
                    .filter { it.isNotEmpty() }
                    .distinctBy { it.lowercase() }
                    .filterNot { it.lowercase() in attrezziNoti }

                val nomi = esercizi.map { it.nome.trim() }
                    .filter { it.isNotEmpty() }
                    .distinctBy { it.lowercase() }
                    .filterNot { it.lowercase() in eserciziNoti }

                attrezzi to nomi
            }

            if (nuoviAttrezzi.isEmpty() && nuoviEsercizi.isEmpty()) {
                saveAndExit(reminderTime)
            } else {
                _state.value = _state.value.copy(
                    libraryPrompt = LibraryPrompt(nuoviAttrezzi, nuoviEsercizi, reminderTime)
                )
            }
        }
    }

    /** Salva in libreria solo le voci spuntate, poi prosegue con il salvataggio della scheda. */
    fun confirmLibrary(attrezzi: List<String>, esercizi: List<String>) {
        val prompt = _state.value.libraryPrompt ?: return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                attrezzi.forEach { customValueRepository.addAttrezzo(it) }
                esercizi.forEach { customValueRepository.addEsercizio(it) }
            }
            if (attrezzi.isNotEmpty()) refreshEquipmentOptions()
            _state.value = _state.value.copy(libraryPrompt = null)
            saveAndExit(prompt.reminderTime)
        }
    }

    /** Niente in libreria, ma la scheda si salva lo stesso: l'utente aveva premuto Salva. */
    fun skipLibrary() {
        val prompt = _state.value.libraryPrompt ?: return
        _state.value = _state.value.copy(libraryPrompt = null)
        saveAndExit(prompt.reminderTime)
    }

    fun saveAndExit(reminderTime: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, showExitDialog = false)

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

    private fun loadLogo() {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val bitmap = withContext(Dispatchers.IO) { LogoStore.bitmap(context) }
            _logo.value = LogoUiState(bitmap = bitmap, placement = LogoStore.placement(context))
        }
    }

    fun importLogo(uri: Uri, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val imported = withContext(Dispatchers.IO) { LogoStore.import(context, uri) }
            if (imported) loadLogo()
            onResult(if (imported) "Logo importato" else "Immagine non valida")
        }
    }

    fun removeLogo() {
        val context = getApplication<Application>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) { LogoStore.remove(context) }
            _logo.value = _logo.value.copy(bitmap = null)
        }
    }

    fun updateLogoPlacement(placement: LogoStore.Placement) {
        _logo.value = _logo.value.copy(placement = placement)
    }

    /**
     * Chiamata a fine trascinamento, non a ogni pixel: la posizione resta anche per i PDF
     * successivi senza scrivere le preferenze decine di volte per gesto.
     */
    fun saveLogoPlacement() {
        LogoStore.savePlacement(getApplication(), _logo.value.placement)
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

    /**
     * Unico punto di uscita per PDF e .fitx. Blocca i doppi tap, perche generare due volte
     * lo stesso file e il modo piu semplice per riempire i Download di duplicati.
     */
    fun esporta(tipo: EsportazioneTipo, onFilePronto: (java.io.File, String) -> Unit = { _, _ -> }) {
        if (_state.value.esportazione.inCorso != null) return

        viewModelScope.launch {
            _state.value = _state.value.copy(esportazione = EsportazioneState(inCorso = tipo))

            val context = getApplication<Application>()
            val scheda = buildSchedaEntity(_state.value.formData)
            val esercizi = _state.value.esercizi

            val result = when (tipo) {
                EsportazioneTipo.FITX -> FitxImportExport.exportScheda(context, scheda, esercizi)
                else -> PdfExporter.exportScheda(context, scheda, esercizi, _state.value.pdfMeta)
            }

            _state.value = _state.value.copy(
                esportazione = result.fold(
                    onSuccess = { file ->
                        when (tipo) {
                            EsportazioneTipo.PDF ->
                                EsportazioneState(esito = "PDF salvato in Documenti: ${file.name}")
                            EsportazioneTipo.PDF_CONDIVISO -> {
                                onFilePronto(file, "application/pdf")
                                EsportazioneState(esito = "PDF pronto: ${file.name}")
                            }
                            EsportazioneTipo.FITX -> {
                                onFilePronto(file, "application/json")
                                EsportazioneState(esito = "Backup pronto: ${file.name}")
                            }
                        }
                    },
                    onFailure = {
                        EsportazioneState(
                            esito = "Esportazione non riuscita: ${it.message ?: "errore sconosciuto"}",
                            errore = true
                        )
                    }
                )
            )
        }
    }

    fun clearEsportazione() {
        _state.value = _state.value.copy(esportazione = EsportazioneState())
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    private fun buildSchedaEntity(
        formData: SchedeFormData,
        source: SchedaSource? = _state.value.source
    ) = SchedeEntity(
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
        coachProfileId = formData.coachProfileId,
        sourceFile = source?.fileName,
        sourceUri = source?.uri,
        sourceSheet = source?.sheet
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
        notes = note.ifBlank { null },
        wgerId = wgerId,
        schedaId = existing?.schedaId ?: DRAFT_SCHEDA_ID,
        settimana = existing?.settimana ?: 1,
        giorno = existing?.giorno ?: 1,
        ordine = existing?.ordine ?: 0,
        supersetGroup = existing?.supersetGroup,
        rpe = rpe.ifBlank { null },
        tempo = tempo.ifBlank { null },
        percentuale = percentuale,
        gruppiMuscolari = gruppiMuscolari.toList().takeIf { it.isNotEmpty() }
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
