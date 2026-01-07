package com.app.fityo.ui.compose

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.contentValuesOf
import com.app.fityo.R
import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.data_layer.db.repos.EsserciziRepository
import com.app.fityo.dominio.UsesCasesEssercissi
import com.app.fityo.fragments.filtro.ShedeFragments
import com.app.fityo.ui.factory.GenericViewModelFactory
import com.app.fityo.ui.forms.AcitivySheda
import com.app.fityo.ui.shedeForms.EsserciziViewModel
import com.app.fityo.ui.shedeForms.SchedeListViewModel
import com.app.fityo.utils.ExportMetadata
import com.app.fityo.utils.PdfExporter
import com.app.fityo.utils.ShareUtils
import com.app.fityo.utils.FitxFormat
import com.app.fityo.utils.FitxImportExport
import com.app.fityo.import_scheda.ImportSchedaActivity
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DarkBackground = Color(0xFF10161B)
private val DarkSurface = Color(0xFF1A222A)
private val DarkCard = Color(0xFF222C35)
private val TextPrimary = Color(0xFFECF0F1)
private val TextSecondary = Color(0xFFB0BEC5)

class SchedeListActivity : ComponentActivity() {

    private val schedeViewModel: SchedeListViewModel by viewModels()
    private val eserciziViewModel: EsserciziViewModel by viewModels {
        GenericViewModelFactory {
            EsserciziViewModel(
                UsesCasesEssercissi(
                    EsserciziRepository(
                        DbFit.getDatabase(application).essercissiDao()
                    )
                )
            )
        }
    }

    // Launcher per ImportSchedaActivity
    private val importSchedaLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Ricarica le schede dopo l'importazione
            schedeViewModel.loadSchede()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Gestisci il profilo passato dal Coach Mode
        intent.getIntExtra(EXTRA_COACH_PROFILE_ID, -1).takeIf { it != -1 }?.let { profileId ->
            // Carica il profilo e impostalo come selezionato
            val db = DbFit.getDatabase(application)
            lifecycleScope.launch {
                val profile = withContext(Dispatchers.IO) {
                    db.coachProfileDao().getById(profileId)
                }
                profile?.let {
                    // setSelectedProfile must be called on main thread
                    schedeViewModel.setSelectedProfile(it.id, it.name)
                }
            }
        }

        setContent {
            MaterialTheme {
                SchedeListRoute(
                    viewModel = schedeViewModel,
                    eserciziViewModel = eserciziViewModel,
                    onBack = { finish() },
                    onOpenScheda = { scheda ->
                        startActivity(
                            Intent(this, AcitivySheda::class.java)
                                .putExtra("isNew", false)
                                .putExtra("f", scheda)
                        )
                    },
                    onCreateScheda = {
                        startActivity(
                            Intent(this, AcitivySheda::class.java)
                                .putExtra("isNew", true)
                        )
                    },
                    onImportScheda = {
                        // Apri la nuova ImportSchedaActivity con le 3 opzioni
                        importSchedaLauncher.launch(
                            Intent(this@SchedeListActivity, ImportSchedaActivity::class.java)
                        )
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        schedeViewModel.loadSchede()
    }

    companion object {
        const val EXTRA_COACH_PROFILE_ID = "extra_coach_profile_id"
    }
}

@Composable
private fun SchedeListRoute(
    viewModel: SchedeListViewModel,
    eserciziViewModel: EsserciziViewModel,
    onBack: () -> Unit,
    onOpenScheda: (SchedeEntity) -> Unit,
    onCreateScheda: () -> Unit,
    onImportScheda: () -> Unit
) {
    val schede by viewModel.schede.observeAsState(emptyList())
    val coachProfiles by viewModel.coachProfiles.observeAsState(emptyList())
    val selectedProfileName by viewModel.selectedProfileName.observeAsState(null)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedScheda by remember { mutableStateOf<SchedeEntity?>(null) }
    var showProfileDialog by remember { mutableStateOf(false) }

    SchedeListScreen(
        schede = schede,
        selectedProfileName = selectedProfileName,
        onSelectProfile = { showProfileDialog = true },
        onBack = onBack,
        onCreateNew = onCreateScheda,
        onItemClick = { selectedScheda = it },
        onToggleFavorite = { scheda, newValue ->
            scheda.id?.let { viewModel.toggleFavorite(it, newValue) }
        },
        onImportScheda = onImportScheda
    )

    // Profile selection dialog
    if (showProfileDialog) {
        ProfileSelectionDialog(
            profiles = coachProfiles,
            onDismiss = { showProfileDialog = false },
            onSelectAll = {
                viewModel.setSelectedProfile(null, null)
                showProfileDialog = false
            },
            onSelectProfile = { profile ->
                viewModel.setSelectedProfile(profile.id, profile.name)
                showProfileDialog = false
            }
        )
    }

    val currentScheda = selectedScheda
    if (currentScheda != null) {
        val exercises =
            currentScheda.id?.let { eserciziViewModel.getAllById(it).observeAsState(emptyList()).value }
                ?: emptyList()

        SchedaDetailDialog(
            scheda = currentScheda,
            esercizi = exercises,
            onDismiss = { selectedScheda = null },
            onDeleteScheda = {
                viewModel.deleteScheda(currentScheda)
                selectedScheda = null
            },
            onToggleFavorite = { fav ->
                currentScheda.id?.let { viewModel.toggleFavorite(it, fav) }
            },
            onCompleteScheda = { id, isCompleted, date ->
                viewModel.completeScheda(id, isCompleted, date)
            },
            onExportPdf = { meta ->
                scope.launch {
                    val result = PdfExporter.exportScheda(context, currentScheda, exercises, meta)
                    withContext(Dispatchers.Main) {
                        result.onSuccess { file ->
                            Toast.makeText(
                                context,
                                context.getString(R.string.export_pdf_success, file.name),
                                Toast.LENGTH_SHORT
                            ).show()
                            Toast.makeText(
                                context,
                                context.getString(R.string.export_pdf_saved),
                                Toast.LENGTH_LONG
                            ).show()
                        }.onFailure {
                            Toast.makeText(
                                context,
                                R.string.export_pdf_error,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            },
            onAddExercise = { eserciziViewModel.insert(it) },
            onUpdateExercise = { eserciziViewModel.update(it) },
            onDeleteExercise = { eserciziViewModel.delete(it) },
            onSharePdf = { meta ->
                scope.launch {
                    val result = PdfExporter.exportScheda(context, currentScheda, exercises, meta)
                    withContext(Dispatchers.Main) {
                        result.onSuccess { file ->
                            ShareUtils.sharePdf(context, file)
                        }.onFailure {
                            Toast.makeText(
                                context,
                                R.string.export_pdf_error,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            },
            onExportFitx = {
                scope.launch {
                    val result = FitxImportExport.exportScheda(context, currentScheda, exercises)
                    withContext(Dispatchers.Main) {
                        result.onSuccess { file ->
                            Toast.makeText(
                                context,
                                context.getString(R.string.toast_export_success, file.name),
                                Toast.LENGTH_SHORT
                            ).show()
                            Toast.makeText(
                                context,
                                context.getString(R.string.toast_export_saved, context.getString(R.string.app_name)),
                                Toast.LENGTH_LONG
                            ).show()
                        }.onFailure {
                            Toast.makeText(
                                context,
                                context.getString(R.string.toast_export_error),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SchedaDetailDialog(
    scheda: SchedeEntity,
    esercizi: List<EsserciziEntity>,
    onDismiss: () -> Unit,
    onDeleteScheda: () -> Unit,
    onToggleFavorite: (Boolean) -> Unit,
    onCompleteScheda: (Int, Boolean, String?) -> Unit,
    onExportPdf: (ExportMetadata) -> Unit,
    onAddExercise: (EsserciziEntity) -> Unit,
    onUpdateExercise: (EsserciziEntity) -> Unit,
    onDeleteExercise: (EsserciziEntity) -> Unit,
    onSharePdf: (ExportMetadata) -> Unit,
    onExportFitx: () -> Unit = {}
) {
    val accent = Color(0xFF40C4FF)
    val primary = Color(0xFF455A64)
    var showAddDialog by remember { mutableStateOf(false) }
    var editingExercise by remember { mutableStateOf<EsserciziEntity?>(null) }
    var customTitle by remember(scheda.id) { mutableStateOf(scheda.titolo) }
    var coachName by remember(scheda.id) { mutableStateOf("") }
    var athleteName by remember(scheda.id) { mutableStateOf("") }
    var startDate by remember(scheda.id) { mutableStateOf(formatDateForDisplay(scheda.data)) }
    var endDate by remember(scheda.id) { mutableStateOf(formatDateForDisplay(scheda.data)) }
    var metadataAction by remember(scheda.id) { mutableStateOf<MetadataAction?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {

        Surface(modifier = Modifier.fillMaxSize(), color = DarkBackground) {
            Column(modifier = Modifier.fillMaxSize()) {
                CenterAlignedTopAppBar(
                    title = { Text(text = scheda.titolo, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, contentDescription = stringResource(id = R.string.content_desc_close), tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { onToggleFavorite(!scheda.favorite) }) {
                            Icon(
                                imageVector = if (scheda.favorite) Icons.Rounded.Star else Icons.Outlined.Star,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurface)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Gruppi muscolari - su una riga separata
                    Text(
                        text = scheda.getGruppiMuscolariDisplay(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Info row: intensita e data
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.intensity_value, scheda.intesita),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Text(
                            text = stringResource(id = R.string.date_value, scheda.data),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    scheda.notes?.takeIf { it.isNotBlank() }?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            maxLines = 2
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    if (esercizi.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.detail_no_exercises),
                                    modifier = Modifier
                                        .padding(24.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    } else {
                        items(esercizi) { esercizio ->
                            ExerciseRow(
                                esercizio = esercizio,
                                onEdit = { editingExercise = esercizio },
                                onDelete = { onDeleteExercise(esercizio) },
                                onToggleComplete = { completed ->
                                    onUpdateExercise(esercizio.copy(completed = completed))
                                }
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurface)
                        .padding(16.dp)
                ) {
                    val allCompleted = esercizi.isNotEmpty() && esercizi.all { it.completed }
                    if (allCompleted && !scheda.completed) {
                        Button(
                            onClick = {
                                scheda.id?.let { id ->
                                    onCompleteScheda(
                                        id,
                                        true,
                                        java.time.LocalDate.now().toString()
                                    )
                                }
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(id = R.string.complete_scheda))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Button(
                        onClick = { metadataAction = MetadataAction.EXPORT },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = primary, contentColor = Color.White)
                    ) {
                        Icon(Icons.Filled.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(id = R.string.export_pdf))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    ActionOutlineButton(
                        text = stringResource(id = R.string.share_pdf),
                        icon = Icons.Filled.Share,
                        color = primary,
                        onClick = { metadataAction = MetadataAction.SHARE }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ActionOutlineButton(
                        text = stringResource(id = R.string.export_fitx),
                        icon = Icons.Filled.FileDownload,
                        color = accent,
                        onClick = onExportFitx
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = Color.White)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(id = R.string.exercise_add_title))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(
                        onClick = onDeleteScheda,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(id = R.string.content_desc_delete), tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.delete_scheda_action), color = Color.Red)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ExerciseFormDialog(
            title = stringResource(id = R.string.exercise_add_title),
            initialData = ExerciseFormData(schedaId = scheda.id ?: 0),
            onDismiss = { showAddDialog = false },
            onConfirm = { data ->
                data.toEntity(context = ShedeFragments().requireActivity().baseContext)?.let {
                    onAddExercise(it)
                    showAddDialog = false
                }
            }
        )
    }

    editingExercise?.let { esercizio ->
        ExerciseFormDialog(
            title = stringResource(id = R.string.exercise_edit_title),
            initialData = ExerciseFormData.from(esercizio),
            onDismiss = { editingExercise = null },
            onConfirm = { data ->
                data.toEntity(context = ShedeFragments().requireActivity().baseContext)?.let {
                    onUpdateExercise(it)
                    editingExercise = null
                }
            }
        )
    }

    metadataAction?.let { action ->
        MetadataDialog(
            action = action,
            customTitle = customTitle,
            onCustomTitleChange = { customTitle = it },
            coachName = coachName,
            onCoachNameChange = { coachName = it },
            athleteName = athleteName,
            onAthleteNameChange = { athleteName = it },
            startDate = startDate,
            onStartDateChange = { startDate = it },
            endDate = endDate,
            onEndDateChange = { endDate = it },
            onDismiss = { metadataAction = null },
            onConfirm = { meta ->
                when (action) {
                    MetadataAction.EXPORT -> onExportPdf(meta)
                    MetadataAction.SHARE -> onSharePdf(meta)
                }
                metadataAction = null
            }
        )
    }
}

@Composable
private fun ExerciseRow(
    esercizio: EsserciziEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: (Boolean) -> Unit = {}
) {
    val accent = Color(0xFF40C4FF)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (esercizio.completed) DarkCard else DarkSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = esercizio.completed,
                    onCheckedChange = onToggleComplete,
                    colors = CheckboxDefaults.colors(
                        checkedColor = accent,
                        uncheckedColor = TextSecondary
                    )
                )
                Text(
                    text = esercizio.nome,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (esercizio.completed) TextSecondary else TextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = stringResource(id = R.string.content_desc_edit), tint = TextSecondary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = stringResource(id = R.string.content_desc_delete), tint = Color.Red)
                }
            }
            Text(
                text = stringResource(id = R.string.exercise_series_reps_format, esercizio.nSerie, esercizio.nRipetizione),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(start = 48.dp)
            )
            if (esercizio.attrezzo.isNotBlank()) {
                Text(
                    text = stringResource(id = R.string.exercise_attrezzo_format, esercizio.attrezzo),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 48.dp)
                )
            }
            esercizio.insometria?.let {
                Text(
                    text = stringResource(id = R.string.exercise_isometria_format, it),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 48.dp)
                )
            }
            esercizio.intervallo?.let {
                Text(
                    text = stringResource(id = R.string.exercise_recupero_format, it),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 48.dp)
                )
            }
        }
    }
}

@Composable
private fun ExerciseFormDialog(
    title: String,
    initialData: ExerciseFormData,
    onDismiss: () -> Unit,
    onConfirm: (ExerciseFormData) -> Unit
) {
    var name by remember { mutableStateOf(initialData.nome) }
    var attrezzo by remember { mutableStateOf(initialData.attrezzo) }
    var serie by remember { mutableStateOf(initialData.serie) }
    var ripetizioni by remember { mutableStateOf(initialData.ripetizioni) }
    var isometria by remember { mutableStateOf(initialData.isometria) }
    var recupero by remember { mutableStateOf(initialData.recupero) }
    val isValid = name.isNotBlank() && serie.toIntOrNull() != null && ripetizioni.toIntOrNull() != null
    val scrollState = rememberScrollState()
    val accent = Color(0xFF40C4FF)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary)
                )
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(id = R.string.exercise_name_label)
                )
                CustomTextField(
                    value = attrezzo,
                    onValueChange = { attrezzo = it },
                    label = stringResource(id = R.string.exercise_attrezzo_label)
                )
                CustomTextField(
                    value = serie,
                    onValueChange = { serie = it },
                    label = stringResource(id = R.string.exercise_sets_label),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                CustomTextField(
                    value = ripetizioni,
                    onValueChange = { ripetizioni = it },
                    label = stringResource(id = R.string.exercise_reps_label),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                CustomTextField(
                    value = isometria,
                    onValueChange = { isometria = it },
                    label = stringResource(id = R.string.exercise_isometria_label),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                CustomTextField(
                    value = recupero,
                    onValueChange = { recupero = it },
                    label = stringResource(id = R.string.exercise_recupero_label),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(id = R.string.exercise_cancel), color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onConfirm(
                                initialData.copy(
                                    nome = name,
                                    attrezzo = attrezzo,
                                    serie = serie,
                                    ripetizioni = ripetizioni,
                                    isometria = isometria,
                                    recupero = recupero
                                )
                            )
                        },
                        enabled = isValid,
                        colors = ButtonDefaults.buttonColors(containerColor = accent)
                    ) {
                        Text(text = stringResource(id = R.string.exercise_save))
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val accent = Color(0xFF40C4FF)
    val combinedModifier = if (onClick != null) {
        modifier
            .fillMaxWidth()
            .clickable { onClick() }
    } else {
        modifier.fillMaxWidth()
    }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = combinedModifier,
        keyboardOptions = keyboardOptions,
        readOnly = readOnly,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = TextSecondary,
            unfocusedLabelColor = TextSecondary,
            focusedIndicatorColor = accent,
            unfocusedIndicatorColor = accent,
            cursorColor = accent
        )
    )
}

@Composable
private fun ActionOutlineButton(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color
        ),
        border = BorderStroke(1.dp, color)
    ) {
        Icon(icon, contentDescription = null, tint = color)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = color)
    }
}

@Composable
private fun PdfFormatCard(
    format: PdfFormat,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = Color(0xFF40C4FF)
    val borderColor = if (isSelected) accent else TextSecondary.copy(alpha = 0.5f)
    val backgroundColor = if (isSelected) DarkCard else DarkBackground

    Card(
        modifier = modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = format.previewRes),
                contentDescription = stringResource(id = format.labelRes),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = format.labelRes),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isSelected) accent else TextPrimary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                textAlign = TextAlign.Center
            )
            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(id = R.string.content_desc_selected),
                    tint = accent,
                    modifier = Modifier.width(16.dp).height(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MetadataDialog(
    action: MetadataAction,
    customTitle: String,
    onCustomTitleChange: (String) -> Unit,
    coachName: String,
    onCoachNameChange: (String) -> Unit,
    athleteName: String,
    onAthleteNameChange: (String) -> Unit,
    startDate: String,
    onStartDateChange: (String) -> Unit,
    endDate: String,
    onEndDateChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (ExportMetadata) -> Unit
) {
    val context = LocalContext.current
    val confirmLabel = when (action) {
        MetadataAction.EXPORT -> stringResource(id = R.string.export_pdf)
        MetadataAction.SHARE -> stringResource(id = R.string.share_pdf)
    }
    val scrollState = rememberScrollState()
    val accent = Color(0xFF40C4FF)
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var selectedFormat by remember { mutableStateOf(PdfFormat.CLASSIC) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.export_meta_section),
                    style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary)
                )
                Text(
                    text = when (action) {
                        MetadataAction.EXPORT -> stringResource(id = R.string.export_pdf)
                        MetadataAction.SHARE -> stringResource(id = R.string.share_pdf)
                    },
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
                CustomTextField(
                    value = customTitle,
                    onValueChange = onCustomTitleChange,
                    label = stringResource(id = R.string.export_meta_title)
                )
                CustomTextField(
                    value = coachName,
                    onValueChange = onCoachNameChange,
                    label = stringResource(id = R.string.export_meta_coach)
                )
                CustomTextField(
                    value = athleteName,
                    onValueChange = onAthleteNameChange,
                    label = stringResource(id = R.string.export_meta_athlete)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CustomTextField(
                        value = startDate,
                        onValueChange = onStartDateChange,
                        label = stringResource(id = R.string.export_meta_start),
                        modifier = Modifier.weight(1f),
                        readOnly = true,
                        onClick = { showStartPicker = true }
                    )
                    CustomTextField(
                        value = endDate,
                        onValueChange = onEndDateChange,
                        label = stringResource(id = R.string.export_meta_end),
                        modifier = Modifier.weight(1f),
                        readOnly = true,
                        onClick = { showEndPicker = true }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.pdf_format_title),
                    style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PdfFormat.values().forEach { format ->
                        PdfFormatCard(
                            format = format,
                            isSelected = selectedFormat == format,
                            onClick = { selectedFormat = format },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(id = R.string.exercise_cancel), color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onConfirm(
                                ExportMetadata(
                                    customTitle = customTitle.trim(),
                                    coachName = coachName.trim(),
                                    athleteName = athleteName.trim(),
                                    startDate = startDate.trim(),
                                    endDate = endDate.trim(),
                                    pdfFormat = selectedFormat.name
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accent)
                    ) {
                        Text(confirmLabel)
                    }
                }
            }
        }
    }

    if (showStartPicker) {
        val dateState = rememberDatePickerState(
            initialSelectedDateMillis = dateStringToMillis(startDate)
                ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let {
                        onStartDateChange(millisToDateString(it))
                    }
                    showStartPicker = false
                }) {
                    Text(text = stringResource(id = R.string.exercise_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) {
                    Text(text = stringResource(id = R.string.exercise_cancel))
                }
            }
        ) {
            DatePicker(
                state = dateState,
                colors = DatePickerDefaults.colors(
                    containerColor = DarkSurface,
                    titleContentColor = TextPrimary,
                    headlineContentColor = TextPrimary,
                    weekdayContentColor = TextSecondary,
                    dayContentColor = TextPrimary,
                    selectedDayContainerColor = accent,
                    selectedDayContentColor = Color.White
                )
            )
        }
    }

    if (showEndPicker) {
        val dateState = rememberDatePickerState(
            initialSelectedDateMillis = dateStringToMillis(endDate)
                ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let {
                        onEndDateChange(millisToDateString(it))
                    }
                    showEndPicker = false
                }) {
                    Text(text = stringResource(id = R.string.exercise_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) {
                    Text(text = stringResource(id = R.string.exercise_cancel))
                }
            }
        ) {
            DatePicker(
                state = dateState,
                colors = DatePickerDefaults.colors(
                    containerColor = DarkSurface,
                    titleContentColor = TextPrimary,
                    headlineContentColor = TextPrimary,
                    weekdayContentColor = TextSecondary,
                    dayContentColor = TextPrimary,
                    selectedDayContainerColor = accent,
                    selectedDayContentColor = Color.White
                )
            )
        }
    }
}

private enum class MetadataAction {
    EXPORT,
    SHARE
}

enum class PdfFormat(
    val labelRes: Int,
    val previewRes: Int
) {
    CLASSIC(R.string.pdf_format_classic, R.drawable.pdf_format_classic_preview),
    MODERN(R.string.pdf_format_modern, R.drawable.pdf_format_modern_preview)
}

@Composable
private fun ImportPreviewDialog(
    fitxFormat: FitxFormat,
    onDismiss: () -> Unit,
    onConfirm: (FitxFormat) -> Unit
) {
    val accent = Color(0xFF40C4FF)
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.import_preview_title),
                    style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary)
                )
                Text(
                    text = stringResource(id = R.string.import_preview_subtitle),
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Informazioni scheda
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = fitxFormat.scheda.titolo,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = accent,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = stringResource(
                                id = R.string.import_preview_group,
                                fitxFormat.scheda.getGruppiMuscolariDisplay()
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Text(
                            text = stringResource(id = R.string.import_preview_intensity, fitxFormat.scheda.intensita),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Text(
                            text = stringResource(id = R.string.import_preview_date, fitxFormat.scheda.data),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        fitxFormat.scheda.notes?.takeIf { it.isNotBlank() }?.let {
                            Text(
                                text = stringResource(id = R.string.import_preview_notes, it),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Lista esercizi
                Text(
                    text = stringResource(id = R.string.import_preview_exercises_count, fitxFormat.scheda.esercizi.size),
                    style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary)
                )

                fitxFormat.scheda.esercizi.take(5).forEach { esercizio ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkBackground)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = esercizio.nome,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            )
                            val exerciseDetail = if (esercizio.attrezzo.isNotBlank()) {
                                stringResource(
                                    id = R.string.import_preview_exercise_detail_with_equipment,
                                    esercizio.serie,
                                    esercizio.ripetizioni,
                                    esercizio.attrezzo
                                )
                            } else {
                                stringResource(
                                    id = R.string.import_preview_exercise_detail,
                                    esercizio.serie,
                                    esercizio.ripetizioni
                                )
                            }
                            Text(
                                text = exerciseDetail,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                if (fitxFormat.scheda.esercizi.size > 5) {
                    Text(
                        text = stringResource(id = R.string.import_preview_more_exercises, fitxFormat.scheda.esercizi.size - 5),
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(id = R.string.exercise_cancel), color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(fitxFormat) },
                        colors = ButtonDefaults.buttonColors(containerColor = accent)
                    ) {
                        Text(text = stringResource(id = R.string.import_action))
                    }
                }
            }
        }
    }
}

private fun FitxFormat.FitxScheda.getGruppiMuscolariDisplay(): String {
    return gruppiMuscolari?.joinToString(", ") ?: gruppoMuscolare
}

private val DISPLAY_DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
private val SYSTEM_ZONE: ZoneId = ZoneId.systemDefault()

private fun formatDateForDisplay(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    return (parseIsoDate(raw)?.format(DISPLAY_DATE_FORMATTER)
        ?: runCatching { LocalDate.parse(raw, DISPLAY_DATE_FORMATTER) }.getOrElse { raw }) as String
}

private fun parseIsoDate(value: String): LocalDate? =
    runCatching { LocalDate.parse(value) }.getOrNull()

private fun dateStringToMillis(value: String): Long? {
    if (value.isBlank()) return null
    return runCatching { LocalDate.parse(value, DISPLAY_DATE_FORMATTER) }
        .getOrNull()
        ?.atStartOfDay(SYSTEM_ZONE)
        ?.toInstant()
        ?.toEpochMilli()
}

private fun millisToDateString(millis: Long): String {
    return Instant.ofEpochMilli(millis)
        .atZone(SYSTEM_ZONE)
        .toLocalDate()
        .format(DISPLAY_DATE_FORMATTER)
}

private data class ExerciseFormData(
    val schedaId: Int,
    val id: Int? = null,
    val nome: String = "",
    val attrezzo: String = "",
    val serie: String = "",
    val ripetizioni: String = "",
    val isometria: String = "",
    val recupero: String = ""
) {
    fun toEntity(context: android.content.Context): EsserciziEntity? {
        val serieInt = serie.toIntOrNull()
        val ripInt = ripetizioni.toIntOrNull()
        if (nome.isBlank() || serieInt == null || ripInt == null) {
            Toast.makeText(
                context,
                context.getString(R.string.exercise_validation_error),
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
        return EsserciziEntity(
            id = id,
            nome = nome.trim(),
            attrezzo = attrezzo.trim(),
            nSerie = serieInt,
            nRipetizione = ripInt,
            insometria = isometria.toIntOrNull(),
            intervallo = recupero.toIntOrNull(),
            schedaId = schedaId
        )
    }

    companion object {
        fun from(entity: EsserciziEntity) = ExerciseFormData(
            schedaId = entity.schedaId,
            id = entity.id,
            nome = entity.nome,
            attrezzo = entity.attrezzo,
            serie = entity.nSerie.toString(),
            ripetizioni = entity.nRipetizione.toString(),
            isometria = entity.insometria?.toString().orEmpty(),
            recupero = entity.intervallo?.toString().orEmpty()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SchedeListScreen(
    schede: List<SchedeEntity>,
    selectedProfileName: String?,
    onSelectProfile: () -> Unit,
    onBack: () -> Unit,
    onCreateNew: () -> Unit,
    onItemClick: (SchedeEntity) -> Unit,
    onToggleFavorite: (SchedeEntity, Boolean) -> Unit,
    onImportScheda: () -> Unit = {}
) {
    val primary = Color(0xFF455A64)
    val accent = Color(0xFF40C4FF)
    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text(text = stringResource(id = R.string.apri_schede)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back_content_description)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
                // Profile selector row
                Surface(
                    color = DarkSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.profile_filter_label),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(
                            onClick = onSelectProfile,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = accent),
                            border = BorderStroke(1.dp, accent)
                        ) {
                            Text(
                                text = selectedProfileName ?: stringResource(id = R.string.all_profiles),
                                color = accent
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(
                    visible = fabExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // FAB Importa
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                color = DarkSurface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.import_scheda),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                            }
                            SmallFloatingActionButton(
                                onClick = {
                                    onImportScheda()
                                    fabExpanded = false
                                },
                                containerColor = accent
                            ) {
                                Icon(Icons.Filled.FileUpload, contentDescription = stringResource(id = R.string.content_desc_import))
                            }
                        }

                        // FAB Crea Nuova
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                color = DarkSurface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.create_new),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                            }
                            SmallFloatingActionButton(
                                onClick = {
                                    onCreateNew()
                                    fabExpanded = false
                                },
                                containerColor = accent
                            ) {
                                Icon(Icons.Filled.CreateNewFolder, contentDescription = stringResource(id = R.string.content_desc_create_new))
                            }
                        }
                    }
                }

                // FAB Principale
                FloatingActionButton(
                    onClick = { fabExpanded = !fabExpanded },
                    containerColor = if (fabExpanded) primary else accent
                ) {
                    Icon(
                        imageVector = if (fabExpanded) Icons.Filled.Close else Icons.Filled.Add,
                        contentDescription = if (fabExpanded) stringResource(id = R.string.menu_close) else stringResource(id = R.string.menu_open)
                    )
                }
            }
        }
    ) { padding ->
        if (schede.isEmpty()) {
            EmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                onCreateNew = onCreateNew
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = schede,
                    key = { it.id ?: it.hashCode() }
                ) { scheda ->
                    SchedaCard(
                        scheda = scheda,
                        onToggleFavorite = {
                            scheda.id?.let { onToggleFavorite(scheda, !scheda.favorite) }
                        },
                        onClick = { onItemClick(scheda) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SchedaCard(
    scheda: SchedeEntity,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = scheda.titolo,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (scheda.favorite) Icons.Rounded.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = if (scheda.favorite) Color(0xFF40C4FF) else Color(0xFFBDBDBD)
                    )
                }
            }
            Text(text = scheda.gruppoMuscolare, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Text(text = stringResource(id = R.string.intensity_value, scheda.intesita), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text(text = stringResource(id = R.string.date_value, scheda.data), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            if (!scheda.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = scheda.notes, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun ProfileSelectionDialog(
    profiles: List<CoachProfileEntity>,
    onDismiss: () -> Unit,
    onSelectAll: () -> Unit,
    onSelectProfile: (CoachProfileEntity) -> Unit
) {
    val accent = Color(0xFF40C4FF)
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.select_profile_title),
                    style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary)
                )

                // Option for all profiles
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectAll() },
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.all_profiles),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = accent,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // List of profiles
                profiles.forEach { profile ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectProfile(profile) },
                        colors = CardDefaults.cardColors(containerColor = DarkCard)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar circle with initials
                            Surface(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp),
                                shape = RoundedCornerShape(20.dp),
                                color = Color(profile.avatarColor)
                            ) {
                                Text(
                                    text = profile.name.take(2).uppercase(),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = profile.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                profile.notes?.takeIf { it.isNotBlank() }?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                if (profiles.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.no_profiles_available),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = stringResource(id = R.string.exercise_cancel), color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    onCreateNew: () -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.nessuna_scheda_title),
            style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFF40C4FF))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.nessuna_scheda_description),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCreateNew) {
            Text(stringResource(id = R.string.crea_scheda))
        }
    }
}

