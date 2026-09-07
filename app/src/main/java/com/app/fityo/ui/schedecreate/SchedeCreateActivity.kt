package com.app.fityo.ui.schedecreate

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.app.fityo.R
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.repos.CustomValueRepository
import com.app.fityo.data_layer.db.repos.EsserciziRepository
import com.app.fityo.data_layer.repository.CoachProfileRepository
import com.app.fityo.data_layer.repository.SchedeRepository
import com.app.fityo.ui.factory.GenericViewModelFactory
import com.app.fityo.ui.schedecreate.compose.EsercissiListScreen
import com.app.fityo.ui.schedecreate.compose.ImportPreviewScreen
import com.app.fityo.ui.schedecreate.compose.PdfPreviewScreen
import com.app.fityo.ui.schedecreate.compose.ProfileOption
import com.app.fityo.ui.schedecreate.compose.RiepilogoScreen
import com.app.fityo.ui.schedecreate.compose.SchedeCreateTheme
import com.app.fityo.ui.schedecreate.compose.SchedeFormScreen

class SchedeCreateActivity : ComponentActivity() {

    companion object {
        const val EXTRA_COACH_PROFILE_ID = "extra_coach_profile_id"
        const val EXTRA_SCHEDA_ID = "extra_scheda_id"
        const val EXTRA_DUPLICATE_SCHEDA_ID = "extra_duplicate_scheda_id"
        const val EXTRA_START_AT_EXERCISES = "extra_start_at_exercises"
        const val EXTRA_EXCEL_URI = "extra_excel_uri"

        // Alcuni file manager restituiscono octet-stream per gli xlsx, quindi accettiamo anche
        // quello e lasciamo che sia XlsxReader a dire se il contenuto e davvero un foglio di calcolo.
        val EXCEL_MIME_TYPES = arrayOf(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/octet-stream"
        )
    }

    private val viewModel: SchedeCreateViewModel by viewModels {
        val db = DbFit.getDatabase(application)
        val coachProfileId = intent.getIntExtra(EXTRA_COACH_PROFILE_ID, -1)
            .takeIf { it != -1 }
        val schedaId = intent.getIntExtra(EXTRA_SCHEDA_ID, -1)
            .takeIf { it != -1 }
        val duplicateSchedaId = intent.getIntExtra(EXTRA_DUPLICATE_SCHEDA_ID, -1)
            .takeIf { it != -1 }
        val startAtExercises = intent.getBooleanExtra(EXTRA_START_AT_EXERCISES, false)

        GenericViewModelFactory {
            SchedeCreateViewModel(
                application = application,
                schedeRepository = SchedeRepository(db.schedeDao()),
                esserciziRepository = EsserciziRepository(db.essercissiDao()),
                coachProfileRepository = CoachProfileRepository(db.coachProfileDao()),
                customValueRepository = CustomValueRepository(db.customValueDao()),
                coachProfileId = coachProfileId,
                editSchedaId = schedaId,
                duplicateSchedaId = duplicateSchedaId,
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

    private val trainingStyleOptions by lazy {
        resources.getStringArray(R.array.training_style_options).toList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Arriviamo dal dialog "Importa scheda": leggiamo subito il file e partiamo dall'anteprima.
        if (savedInstanceState == null) {
            intent.getStringExtra(EXTRA_EXCEL_URI)?.let { raw ->
                val uri = Uri.parse(raw)
                viewModel.importExcel(uri, displayName(this, uri))
            }
        }

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

private fun displayName(context: Context, uri: Uri): String {
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (index >= 0 && cursor.moveToFirst()) return cursor.getString(index)
    }
    return uri.lastPathSegment.orEmpty()
}

@Composable
private fun SchedeCreateNavHost(
    viewModel: SchedeCreateViewModel,
    intensityOptions: List<String>,
    muscleGroupOptions: List<String>,
    trainingStyleOptions: List<String>,
    onFinish: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val coachProfiles by viewModel.coachProfiles.collectAsState()
    val equipmentOptions by viewModel.equipmentOptions.collectAsState()
    val logo by viewModel.logo.collectAsState()
    val esercizi = state.esercizi
    val context = LocalContext.current

    val excelPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.importExcel(uri, displayName(context, uri))
        }
    }

    val logoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.importLogo(uri) { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Converti i profili in ProfileOption
    val profileOptions = coachProfiles.map { profile ->
        ProfileOption(
            id = profile.id,
            name = profile.name,
            avatarColor = profile.avatarColor
        )
    }

    // Si esce solo dopo un salvataggio riuscito o dopo che l'utente ha scelto di scartare.
    LaunchedEffect(state.isSaved, state.closeRequested) {
        if (state.isSaved || state.closeRequested) {
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
                    onImportExcel = { excelPicker.launch(SchedeCreateActivity.EXCEL_MIME_TYPES) },
                    onDismissError = { viewModel.clearError() },
                    onBack = { viewModel.navigateBack() }
                )
            }

            SchedeCreateStep.ImportPreview -> {
                state.importState?.let { importState ->
                    ImportPreviewScreen(
                        importState = importState,
                        onSelectSheet = { viewModel.selectSheet(it) },
                        onSelectDay = { viewModel.selectDay(it) },
                        onSelectVariant = { viewModel.selectVariant(it) },
                        onRowChanged = { viewModel.updateImportedRow(it) },
                        onAddEquipment = { viewModel.addCustomEquipment(it) },
                        onConfirm = { viewModel.confirmImport() },
                        onBack = { viewModel.cancelImport() }
                    )
                }
            }

            SchedeCreateStep.Esercizi -> {
                EsercissiListScreen(
                    esercizi = esercizi,
                    equipmentOptions = equipmentOptions,
                    onAddEsercizio = { viewModel.addEsercizio(it) },
                    onEditEsercizio = { viewModel.updateEsercizio(it) },
                    onDeleteEsercizio = { viewModel.deleteEsercizio(it) },
                    onSaveAttrezzo = { nome ->
                        viewModel.saveAttrezzo(nome) { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onSaveNomeComeEsercizio = { nome ->
                        viewModel.saveNomeComeEsercizio(nome) { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onNext = { viewModel.navigateToRiepilogo() },
                    onBack = { viewModel.navigateBack() }
                )
            }

            SchedeCreateStep.Riepilogo -> {
                RiepilogoScreen(
                    formData = state.formData,
                    esercizi = esercizi,
                    intensityOptions = intensityOptions,
                    muscleGroupOptions = muscleGroupOptions,
                    profileOptions = profileOptions,
                    onFormDataChanged = { viewModel.updateFormData(it) },
                    onSaveAndExit = { reminderTime ->
                        viewModel.saveAndExit(reminderTime)
                    },
                    onExportPdf = { viewModel.openPdfPreview() },
                    onBack = { viewModel.navigateBack() }
                )
            }

            SchedeCreateStep.PdfPreview -> {
                PdfPreviewScreen(
                    meta = state.pdfMeta,
                    notes = state.formData.notes,
                    esercizi = esercizi,
                    logo = logo.bitmap,
                    logoPlacement = logo.placement,
                    onMetaChanged = { viewModel.updatePdfMeta(it) },
                    onNotesChanged = { viewModel.updateFormData(state.formData.copy(notes = it)) },
                    onEsercizioChanged = { viewModel.updateEsercizioEntity(it) },
                    onPickLogo = { logoPicker.launch(arrayOf("image/*")) },
                    onRemoveLogo = { viewModel.removeLogo() },
                    onLogoPlacementChanged = { viewModel.updateLogoPlacement(it) },
                    onSaveLogoPlacement = { viewModel.saveLogoPlacement() },
                    onGenerate = {
                        viewModel.exportPdf { message ->
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    },
                    onBack = { viewModel.navigateBack() }
                )
            }
        }

        if (state.showExitDialog) {
            ExitConfirmDialog(
                onSave = { viewModel.saveAndExit(null) },
                onDiscard = { viewModel.discardAndExit() },
                onDismiss = { viewModel.dismissExitDialog() }
            )
        }
    }
}

/**
 * Uscendo dal flusso la scheda in memoria andrebbe persa senza avvisi: qui la si salva
 * o si sceglie esplicitamente di buttarla.
 */
@Composable
private fun ExitConfirmDialog(
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFFFC107)
            )
        },
        title = { Text("Attenzione") },
        text = { Text("La scheda non e ancora stata salvata. Vuoi salvarla prima di uscire?") },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("Salva")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDismiss) {
                    Text("Annulla")
                }
                TextButton(onClick = onDiscard) {
                    Text("Non salvare")
                }
            }
        }
    )
}
