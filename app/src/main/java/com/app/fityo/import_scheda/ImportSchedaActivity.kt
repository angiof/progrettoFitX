package com.app.fityo.import_scheda

import android.content.Intent
import android.net.Uri
import com.app.fityo.ui.schedecreate.SchedeCreateActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.app.fityo.R
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.utils.FitxFormat
import com.app.fityo.utils.FitxImportExport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Activity per gestire tutti i flussi di importazione schede:
 * - FitYo format (.fitx)
 * - PDF
 * - Camera OCR
 */
class ImportSchedaActivity : ComponentActivity() {

    private var currentImportType by mutableStateOf<ImportType?>(null)
    private var showOptionsDialog by mutableStateOf(true)
    private var showProfileDialog by mutableStateOf(false)
    private var pendingSchedaData: PendingSchedaImport? = null
    private var profiles by mutableStateOf<List<ProfileInfo>>(emptyList())

    // Launcher per file .fitx
    private val fitxFilePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            handleFitxImport(uri)
        } else {
            // Utente ha cancellato - torna al dialog
            showOptionsDialog = true
            currentImportType = null
        }
    }

    // Launcher per Excel: qui non salviamo niente, il file viene passato alla creazione scheda
    // che ne mostra l'anteprima e poi prosegue con il flusso normale.
    private val excelFilePicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            startActivity(
                Intent(this, SchedeCreateActivity::class.java)
                    .putExtra(SchedeCreateActivity.EXTRA_EXCEL_URI, uri.toString())
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            )
            finish()
        } else {
            showOptionsDialog = true
            currentImportType = null
        }
    }

    // Launcher per PDF
    private val pdfFilePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            handlePdfImport(uri)
        } else {
            // Utente ha cancellato - torna al dialog
            showOptionsDialog = true
            currentImportType = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Carica i profili coach
        loadProfiles()

        setContent {
            MaterialTheme {
                ImportSchedaScreen()
            }
        }
    }

    @Composable
    private fun ImportSchedaScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF10161B)) // DarkBackground
        ) {
            // Contenuto principale basato sullo stato
            when {
                // Schermata Camera OCR (full screen)
                currentImportType == ImportType.CAMERA_OCR && !showOptionsDialog -> {
                    CameraOcrImportScreen(
                        onBack = {
                            currentImportType = null
                            showOptionsDialog = true
                        },
                        onImportComplete = { rows, title ->
                            handleCameraOcrResult(rows, title)
                        }
                    )
                }

                // Processing PDF
                isProcessingPdf -> {
                    PdfProcessingScreen(step = pdfProcessingStep)
                }

                // Revisione risultati PDF
                pdfResult?.success == true && pdfEditableRows.isNotEmpty() && !isProcessingPdf -> {
                    PdfReviewScreen(
                        pageCount = pdfResult?.pageCount ?: 0,
                        rows = pdfEditableRows,
                        schedaTitle = pdfSchedaTitle,
                        onTitleChange = { pdfSchedaTitle = it },
                        onRowChange = { index, newRow ->
                            pdfEditableRows = pdfEditableRows.toMutableList().apply {
                                set(index, newRow)
                            }
                        },
                        onDeleteRow = { index ->
                            pdfEditableRows = pdfEditableRows.toMutableList().apply {
                                removeAt(index)
                            }
                        },
                        onAddRow = {
                            pdfEditableRows = pdfEditableRows + EditableExerciseRow(
                                id = java.util.UUID.randomUUID().toString(),
                                exerciseName = "",
                                sets = "",
                                reps = "",
                                weight = "",
                                rest = "",
                                notes = "",
                                confidence = 1f,
                                hasError = false,
                                errorMessage = null
                            )
                        },
                        onBack = {
                            pdfResult = null
                            pdfEditableRows = emptyList()
                            showOptionsDialog = true
                        },
                        onImport = {
                            if (pdfSchedaTitle.isNotBlank() && pdfEditableRows.any { it.isValid() }) {
                                handlePdfImportResult(pdfEditableRows.filter { it.isValid() }, pdfSchedaTitle)
                            }
                        },
                        canImport = pdfSchedaTitle.isNotBlank() && pdfEditableRows.any { it.isValid() }
                    )
                }
            }

            // Dialog opzioni importazione (overlay)
            if (showOptionsDialog) {
                ImportOptionsDialog(
                    onDismiss = { finish() },
                    onSelectFityo = {
                        showOptionsDialog = false
                        currentImportType = ImportType.FITYO_FORMAT
                        fitxFilePicker.launch("*/*")
                    },
                    onSelectExcel = {
                        showOptionsDialog = false
                        currentImportType = ImportType.EXCEL
                        excelFilePicker.launch(SchedeCreateActivity.EXCEL_MIME_TYPES)
                    },
                    onSelectPdf = {
                        showOptionsDialog = false
                        currentImportType = ImportType.PDF
                        pdfFilePicker.launch("application/pdf")
                    },
                    onSelectCamera = {
                        showOptionsDialog = false
                        currentImportType = ImportType.CAMERA_OCR
                    }
                )
            }

            // Dialog assegnazione profilo (overlay)
            if (showProfileDialog && pendingSchedaData != null) {
                ProfileAssignmentDialog(
                    profiles = profiles,
                    onDismiss = {
                        showProfileDialog = false
                        pendingSchedaData = null
                        finish()
                    },
                    onSelectPersonal = {
                        saveScheda(null)
                    },
                    onSelectProfile = { profileId ->
                        saveScheda(profileId)
                    }
                )
            }
        }
    }

    private fun loadProfiles() {
        lifecycleScope.launch {
            val db = DbFit.getDatabase(application)
            val coachProfiles = withContext(Dispatchers.IO) {
                db.coachProfileDao().getAllProfilesSync()
            }
            profiles = coachProfiles.map { profile ->
                ProfileInfo(
                    id = profile.id ?: 0,
                    name = profile.name,
                    avatarColor = profile.avatarColor,
                    notes = profile.notes
                )
            }
        }
    }

    private fun handleFitxImport(uri: Uri) {
        lifecycleScope.launch {
            val result = FitxImportExport.importScheda(this@ImportSchedaActivity, uri)
            result.onSuccess { fitx ->
                // Prepara i dati per il salvataggio
                val (scheda, esercizi) = FitxFormat.toEntities(fitx)
                pendingSchedaData = PendingSchedaImport(
                    scheda = scheda,
                    esercizi = esercizi.map { fitxEsercizio ->
                        EsercizioImportData(
                            nome = fitxEsercizio.nome,
                            serie = fitxEsercizio.serie,
                            ripetizioni = fitxEsercizio.ripetizioni,
                            attrezzo = fitxEsercizio.attrezzo,
                            peso = fitxEsercizio.peso,
                            recupero = fitxEsercizio.recupero,
                            isometria = fitxEsercizio.isometria,
                            notes = fitxEsercizio.notes
                        )
                    }
                )
                showProfileDialog = true
            }.onFailure { error ->
                Toast.makeText(
                    this@ImportSchedaActivity,
                    getString(R.string.toast_import_error, error.message ?: ""),
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private var isProcessingPdf by mutableStateOf(false)
    private var pdfProcessingStep by mutableStateOf("")
    private var pdfResult: PdfOcrHelper.PdfImportResult? by mutableStateOf(null)
    private var pdfEditableRows by mutableStateOf<List<EditableExerciseRow>>(emptyList())
    private var pdfSchedaTitle by mutableStateOf("")

    private fun handlePdfImport(uri: Uri) {
        lifecycleScope.launch {
            isProcessingPdf = true
            pdfProcessingStep = getString(R.string.import_pdf_processing_hint)

            val pdfHelper = PdfOcrHelper(this@ImportSchedaActivity)
            try {
                val result = pdfHelper.processPdf(uri)
                pdfResult = result

                android.util.Log.d("ImportScheda", "PDF result: success=${result.success}, pages=${result.pageCount}, rows=${result.allParsedRows.size}")

                if (result.success && result.allParsedRows.isNotEmpty()) {
                    // Converti i risultati in righe editabili
                    pdfEditableRows = result.allParsedRows.map { parsed ->
                        EditableExerciseRow.fromParsed(parsed, null)
                    }
                    pdfSchedaTitle = ""

                    Toast.makeText(
                        this@ImportSchedaActivity,
                        "Trovati ${result.allParsedRows.size} esercizi in ${result.pageCount} pagine",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@ImportSchedaActivity,
                        result.errorMessage ?: getString(R.string.import_no_results_title),
                        Toast.LENGTH_LONG
                    ).show()
                    showOptionsDialog = true
                    pdfResult = null
                }
            } catch (e: Exception) {
                android.util.Log.e("ImportScheda", "PDF error", e)
                Toast.makeText(
                    this@ImportSchedaActivity,
                    getString(R.string.toast_import_error, e.message ?: "Errore sconosciuto"),
                    Toast.LENGTH_LONG
                ).show()
                showOptionsDialog = true
                pdfResult = null
            } finally {
                isProcessingPdf = false
                pdfHelper.close()
            }
        }
    }

    private fun handlePdfImportResult(rows: List<EditableExerciseRow>, title: String) {
        handleCameraOcrResult(rows, title) // Stesso flusso del camera OCR
        pdfResult = null
        pdfEditableRows = emptyList()
    }

    private fun handleCameraOcrResult(rows: List<EditableExerciseRow>, title: String) {
        // Converti i risultati OCR in dati importabili
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        val scheda = SchedeEntity(
            titolo = title,
            gruppoMuscolare = "Importato", // Valore di default
            gruppiMuscolari = null,
            intesita = "Media",
            data = today,
            notes = "Importato tramite OCR"
        )

        pendingSchedaData = PendingSchedaImport(
            scheda = scheda,
            esercizi = rows.filter { it.isValid() }.map { row ->
                EsercizioImportData(
                    nome = row.exerciseName,
                    serie = row.sets.toIntOrNull() ?: 3,
                    ripetizioni = row.reps.toIntOrNull() ?: 10,
                    attrezzo = "",
                    peso = row.weight.replace(Regex("[^0-9.,]"), "").replace(",", ".").toFloatOrNull(),
                    recupero = row.rest.replace(Regex("[^0-9]"), "").toIntOrNull(),
                    isometria = null,
                    notes = row.notes.takeIf { it.isNotBlank() }
                )
            }
        )
        showProfileDialog = true
    }

    private fun saveScheda(profileId: Int?) {
        val data = pendingSchedaData ?: return

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val db = DbFit.getDatabase(application)
                val schedeDao = db.schedeDao()
                val eserciziDao = db.essercissiDao()

                // Crea la scheda con il profilo assegnato
                val schedaToSave = data.scheda.copy(coachProfileId = profileId)
                val schedaId = schedeDao.insert(schedaToSave)

                // Inserisci gli esercizi
                data.esercizi.forEach { esercizio ->
                    val entity = EsserciziEntity(
                        nome = esercizio.nome,
                        nSerie = esercizio.serie,
                        nRipetizione = esercizio.ripetizioni,
                        attrezzo = esercizio.attrezzo,
                        peso = esercizio.peso,
                        intervallo = esercizio.recupero,
                        insometria = esercizio.isometria,
                        notes = esercizio.notes,
                        schedaId = schedaId.toInt()
                    )
                    eserciziDao.insert(entity)
                }
            }

            Toast.makeText(
                this@ImportSchedaActivity,
                getString(R.string.toast_import_success),
                Toast.LENGTH_SHORT
            ).show()

            setResult(RESULT_OK)
            finish()
        }
    }

    companion object {
        const val RESULT_IMPORTED = 100
    }
}

/**
 * Dati temporanei per l'importazione
 */
private data class PendingSchedaImport(
    val scheda: SchedeEntity,
    val esercizi: List<EsercizioImportData>
)

private data class EsercizioImportData(
    val nome: String,
    val serie: Int,
    val ripetizioni: Int,
    val attrezzo: String,
    val peso: Float?,
    val recupero: Int?,
    val isometria: Int?,
    val notes: String?
)
