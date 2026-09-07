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
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.unit.sp
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
import com.app.fityo.ui.factory.GenericViewModelFactory
import com.app.fityo.ui.forms.AcitivySheda
import com.app.fityo.ui.shedeForms.EsserciziViewModel
import com.app.fityo.ui.shedeForms.SchedeListViewModel
import com.app.fityo.utils.ExportMetadata
import com.app.fityo.utils.PdfExporter
import com.app.fityo.utils.ShareUtils
import com.app.fityo.utils.FitxFormat
import com.app.fityo.utils.FitxImportExport
import com.app.fityo.ui.wger.WgerExerciseInfoDialog
import com.app.fityo.import_scheda.ImportSchedaActivity
import com.app.fityo.ui.schedecreate.SchedeCreateActivity
import com.app.fityo.ui.schedecreate.compose.EsercizioEditorSheet
import com.app.fityo.ui.schedecreate.compose.toFormData
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import com.app.fityo.ui.schedecreate.compose.getMuscleGroupColor
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.text.style.TextDecoration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.Build
import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Timer

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
                        startActivity(Intent(this, SchedeCreateActivity::class.java))
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
    val equipmentOptions by viewModel.equipmentOptions.observeAsState(emptyList())
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedScheda by remember { mutableStateOf<SchedeEntity?>(null) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var infoScheda by remember { mutableStateOf<SchedeEntity?>(null) }

    // Stati per selezione multipla
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedSchede by remember { mutableStateOf(setOf<Int>()) }
    var showAssignProfileDialog by remember { mutableStateOf(false) }

    SchedeListScreen(
        schede = schede,
        selectedProfileName = selectedProfileName,
        onSelectProfile = { showProfileDialog = true },
        onBack = {
            if (isSelectionMode) {
                isSelectionMode = false
                selectedSchede = emptySet()
            } else {
                onBack()
            }
        },
        onCreateNew = onCreateScheda,
        onItemClick = { scheda ->
            if (isSelectionMode) {
                scheda.id?.let { id ->
                    selectedSchede = if (selectedSchede.contains(id)) {
                        selectedSchede - id
                    } else {
                        selectedSchede + id
                    }
                }
            } else {
                selectedScheda = scheda
            }
        },
        onToggleFavorite = { scheda, newValue ->
            scheda.id?.let { viewModel.toggleFavorite(it, newValue) }
        },
        onImportScheda = onImportScheda,
        onShowInfo = { infoScheda = it },
        isSelectionMode = isSelectionMode,
        selectedSchede = selectedSchede,
        onLongClick = { scheda ->
            if (!isSelectionMode) {
                isSelectionMode = true
                scheda.id?.let { selectedSchede = setOf(it) }
            }
        },
        onSelectAll = {
            selectedSchede = schede.mapNotNull { it.id }.toSet()
        },
        onCancelSelection = {
            isSelectionMode = false
            selectedSchede = emptySet()
        },
        onAssignToProfile = {
            showAssignProfileDialog = true
        }
    )

    // Info dialog con Pie Chart
    infoScheda?.let { scheda ->
        SchedaInfoDialog(
            scheda = scheda,
            onDismiss = { infoScheda = null }
        )
    }

    // Profile assignment dialog (per selezione multipla)
    if (showAssignProfileDialog) {
        ProfileAssignmentDialog(
            profiles = coachProfiles,
            onDismiss = { showAssignProfileDialog = false },
            onAssign = { profileId ->
                viewModel.assignSchedeToProfile(selectedSchede.toList(), profileId)
                showAssignProfileDialog = false
                isSelectionMode = false
                selectedSchede = emptySet()
            }
        )
    }

    // Profile selection dialog (per filtro)
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
            // Modifica e duplicazione riusano il flusso di creazione: e l'unico posto dove si
            // possono aggiungere esercizi con il form completo.
            onEditScheda = {
                currentScheda.id?.let { id ->
                    context.startActivity(
                        Intent(context, SchedeCreateActivity::class.java)
                            .putExtra(SchedeCreateActivity.EXTRA_SCHEDA_ID, id)
                            .putExtra(SchedeCreateActivity.EXTRA_START_AT_EXERCISES, true)
                    )
                }
            },
            onDuplicateScheda = {
                currentScheda.id?.let { id ->
                    context.startActivity(
                        Intent(context, SchedeCreateActivity::class.java)
                            .putExtra(SchedeCreateActivity.EXTRA_DUPLICATE_SCHEDA_ID, id)
                    )
                }
                selectedScheda = null
            },
            onUpdateExercise = { eserciziViewModel.update(it) },
            onDeleteExercise = { eserciziViewModel.delete(it) },
            equipmentOptions = equipmentOptions,
            onSaveAttrezzo = { attrezzo ->
                viewModel.saveAttrezzo(attrezzo) { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            },
            onSaveNomeComeEsercizio = { nome ->
                viewModel.saveEsercizio(nome) { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            },
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
    onUpdateExercise: (EsserciziEntity) -> Unit,
    onDeleteExercise: (EsserciziEntity) -> Unit,
    equipmentOptions: List<String>,
    onSaveAttrezzo: (String) -> Unit,
    onSaveNomeComeEsercizio: (String) -> Unit,
    onSharePdf: (ExportMetadata) -> Unit,
    onExportFitx: () -> Unit = {},
    onEditScheda: () -> Unit = {},
    onDuplicateScheda: () -> Unit = {}
) {
    val accent = Color(0xFF40C4FF)
    val primary = Color(0xFF455A64)
    var editingExercise by remember { mutableStateOf<EsserciziEntity?>(null) }
    var infoExerciseId by remember { mutableStateOf<Int?>(null) }
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
            val allCompleted = esercizi.isNotEmpty() && esercizi.all { it.completed }
            
            Box(modifier = Modifier.fillMaxSize()) {
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
                        
                        // Progress Bar Section
                        if (esercizi.isNotEmpty()) {
                            val completedCount = esercizi.count { it.completed }
                            val totalCount = esercizi.size
                            val progress = completedCount.toFloat() / totalCount
                            val animatedProgress by animateFloatAsState(
                                targetValue = progress,
                                animationSpec = tween(durationMillis = 500),
                                label = "progress"
                            )
                            val progressColor = when {
                                progress >= 1f -> Color(0xFF4CAF50) // Green when complete
                                progress >= 0.5f -> Color(0xFFFF9800) // Orange halfway
                                else -> accent
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Progresso allenamento",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "$completedCount/$totalCount",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = progressColor
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = progressColor,
                                trackColor = DarkCard
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
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
                                    },
                                    onInfo = { id -> infoExerciseId = id }
                                )
                            }
                        }
                    }

                    // Compact Action Bar - Modern horizontal icon row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurface)
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DetailActionIcon(
                            icon = Icons.Filled.PictureAsPdf,
                            label = "PDF",
                            onClick = { metadataAction = MetadataAction.EXPORT }
                        )
                        DetailActionIcon(
                            icon = Icons.Filled.Share,
                            label = stringResource(id = R.string.share_pdf).take(8),
                            onClick = { metadataAction = MetadataAction.SHARE }
                        )
                        DetailActionIcon(
                            icon = Icons.Filled.FileDownload,
                            label = "FitX",
                            tint = accent,
                            onClick = onExportFitx
                        )
                        DetailActionIcon(
                            icon = Icons.Filled.ContentCopy,
                            label = "Duplica",
                            onClick = onDuplicateScheda
                        )
                        DetailActionIcon(
                            icon = Icons.Filled.Delete,
                            label = stringResource(id = R.string.delete_scheda_action).take(7),
                            tint = Color(0xFFE57373),
                            onClick = onDeleteScheda
                        )
                    }
                }

                // FAB for primary action (Edit or Complete)
                FloatingActionButton(
                    onClick = {
                        if (allCompleted && !scheda.completed) {
                            scheda.id?.let { id ->
                                onCompleteScheda(id, true, java.time.LocalDate.now().toString())
                            }
                            onDismiss()
                        } else {
                            onEditScheda()
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 88.dp),
                    containerColor = if (allCompleted && !scheda.completed) Color(0xFF4CAF50) else accent,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = if (allCompleted && !scheda.completed) Icons.Filled.CheckCircle else Icons.Filled.Edit,
                        contentDescription = if (allCompleted && !scheda.completed) 
                            stringResource(id = R.string.complete_scheda) else "Modifica"
                    )
                }
            }
        }
    }

    // Stesso bottom sheet della creazione scheda: un solo form esercizio in tutta l'app.
    editingExercise?.let { esercizio ->
        EsercizioEditorSheet(
            initialData = esercizio.toFormData(),
            equipmentOptions = equipmentOptions,
            onSaveAttrezzo = onSaveAttrezzo,
            onSaveNomeComeEsercizio = onSaveNomeComeEsercizio,
            onSave = { data ->
                onUpdateExercise(
                    esercizio.copy(
                        nome = data.nome.trim(),
                        attrezzo = data.attrezzo.trim(),
                        nSerie = data.nSerie,
                        nRipetizione = data.nRipetizioni,
                        insometria = data.isometria,
                        intervallo = data.intervallo,
                        peso = data.peso,
                        wgerId = data.wgerId
                    )
                )
            },
            onDismiss = { editingExercise = null }
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

    infoExerciseId?.let { id ->
        WgerExerciseInfoDialog(
            exerciseId = id,
            onDismiss = { infoExerciseId = null }
        )
    }
}

/**
 * Modern action icon with label for the compact bottom action bar
 */
@Composable
private fun DetailActionIcon(
    icon: ImageVector,
    label: String,
    tint: Color = TextPrimary,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = tint,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ExerciseRow(
    esercizio: EsserciziEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: (Boolean) -> Unit = {},
    onInfo: ((Int) -> Unit)? = null
) {
    val context = LocalContext.current
    val accent = Color(0xFF40C4FF)
    val completedColor = Color(0xFF4CAF50)
    
    // Rest Timer State
    var isTimerRunning by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableStateOf(esercizio.intervallo ?: 60) }
    
    // Timer Effect
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning && remainingSeconds > 0) {
            while (remainingSeconds > 0 && isTimerRunning) {
                delay(1000L)
                remainingSeconds--
            }
            if (remainingSeconds == 0) {
                // Vibrate when timer ends
                vibrateDevice(context, 500L)
                isTimerRunning = false
                remainingSeconds = esercizio.intervallo ?: 60
            }
        }
    }
    
    // Border animation for completed exercises
    val borderColor = if (esercizio.completed) completedColor else Color.Transparent
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (esercizio.completed) {
                    Modifier.border(2.dp, completedColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                } else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (esercizio.completed) DarkCard.copy(alpha = 0.7f) else DarkSurface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (esercizio.completed) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Large tap target for checkbox
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (esercizio.completed) completedColor.copy(alpha = 0.15f)
                            else DarkCard
                        )
                        .clickable {
                            val newState = !esercizio.completed
                            if (newState) {
                                // Vibrate on completion
                                vibrateDevice(context, 100L)
                            }
                            onToggleComplete(newState)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (esercizio.completed) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                        contentDescription = if (esercizio.completed) "Completato" else "Da fare",
                        tint = if (esercizio.completed) completedColor else TextSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = esercizio.nome,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (esercizio.completed) TextSecondary else TextPrimary,
                            textDecoration = if (esercizio.completed) TextDecoration.LineThrough else TextDecoration.None
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.exercise_series_reps_format, esercizio.nSerie, esercizio.nRipetizione),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                
                // Action buttons
                if (esercizio.wgerId != null && onInfo != null) {
                    IconButton(onClick = { onInfo(esercizio.wgerId) }) {
                        Icon(Icons.Filled.Info, contentDescription = "Info esercizio", tint = accent)
                    }
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = stringResource(id = R.string.content_desc_edit), tint = TextSecondary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = stringResource(id = R.string.content_desc_delete), tint = Color.Red.copy(alpha = 0.7f))
                }
            }
            
            // Additional info row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 60.dp, top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (esercizio.attrezzo.isNotBlank()) {
                    Text(
                        text = esercizio.attrezzo,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary.copy(alpha = 0.7f)
                    )
                }
                esercizio.peso?.let { peso ->
                    Text(
                        text = "${peso}kg",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = accent
                    )
                }
            }
            
            // Rest Timer Section (only show if exercise has rest interval)
            esercizio.intervallo?.let { restSeconds ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkCard)
                        .clickable {
                            if (isTimerRunning) {
                                isTimerRunning = false
                                remainingSeconds = restSeconds
                            } else {
                                remainingSeconds = restSeconds
                                isTimerRunning = true
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isTimerRunning) Icons.Filled.Pause else Icons.Filled.Timer,
                            contentDescription = "Timer recupero",
                            tint = if (isTimerRunning) Color(0xFFFF9800) else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isTimerRunning) "Recupero..." else "Avvia recupero",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isTimerRunning) Color(0xFFFF9800) else TextSecondary
                        )
                    }
                    Text(
                        text = formatSeconds(remainingSeconds),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isTimerRunning) Color(0xFFFF9800) else TextSecondary
                    )
                }
            }
        }
    }
}

// Helper function for vibration
private fun vibrateDevice(context: Context, durationMs: Long) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    vibrator?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            it.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            it.vibrate(durationMs)
        }
    }
}

// Helper to format seconds as MM:SS
private fun formatSeconds(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SchedeListScreen(
    schede: List<SchedeEntity>,
    selectedProfileName: String?,
    onSelectProfile: () -> Unit,
    onBack: () -> Unit,
    onCreateNew: () -> Unit,
    onItemClick: (SchedeEntity) -> Unit,
    onToggleFavorite: (SchedeEntity, Boolean) -> Unit,
    onImportScheda: () -> Unit = {},
    onShowInfo: (SchedeEntity) -> Unit = {},
    isSelectionMode: Boolean = false,
    selectedSchede: Set<Int> = emptySet(),
    onLongClick: (SchedeEntity) -> Unit = {},
    onSelectAll: () -> Unit = {},
    onCancelSelection: () -> Unit = {},
    onAssignToProfile: () -> Unit = {}
) {
    val primary = Color(0xFF455A64)
    val accent = Color(0xFF40C4FF)
    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (isSelectionMode) {
                                "${selectedSchede.size} selezionate"
                            } else {
                                stringResource(id = R.string.apri_schede)
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                if (isSelectionMode) Icons.Filled.Close else Icons.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back_content_description)
                            )
                        }
                    },
                    actions = {
                        if (isSelectionMode) {
                            TextButton(onClick = onSelectAll) {
                                Text("Seleziona tutto", color = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = if (isSelectionMode) accent else primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
                // Profile selector row (nascosto in selection mode)
                if (!isSelectionMode) {
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
            }
        },
        bottomBar = {
            if (isSelectionMode && selectedSchede.isNotEmpty()) {
                Surface(
                    color = DarkSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancelSelection,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                            border = BorderStroke(1.dp, TextSecondary)
                        ) {
                            Text("Annulla")
                        }
                        Button(
                            onClick = onAssignToProfile,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = accent)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Assegna a profilo")
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            // Nasconde il FAB in selection mode
            if (!isSelectionMode) {
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
                        onClick = { onItemClick(scheda) },
                        onShowInfo = { onShowInfo(scheda) },
                        onLongClick = { onLongClick(scheda) },
                        isSelectionMode = isSelectionMode,
                        isSelected = scheda.id?.let { selectedSchede.contains(it) } ?: false
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SchedaCard(
    scheda: SchedeEntity,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    onShowInfo: () -> Unit = {},
    onLongClick: () -> Unit = {},
    isSelectionMode: Boolean = false,
    isSelected: Boolean = false
) {
    val accent = Color(0xFF40C4FF)
    val selectedBorder = if (isSelected) BorderStroke(2.dp, accent) else null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DarkCard.copy(alpha = 0.9f) else DarkCard
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = selectedBorder
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox in selection mode
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = accent,
                        uncheckedColor = TextSecondary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
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
                    if (!isSelectionMode) {
                        IconButton(onClick = onShowInfo) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info muscoli",
                                tint = TextSecondary
                            )
                        }
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                imageVector = if (scheda.favorite) Icons.Rounded.Star else Icons.Outlined.Star,
                                contentDescription = null,
                                tint = if (scheda.favorite) accent else Color(0xFFBDBDBD)
                            )
                        }
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
private fun ProfileAssignmentDialog(
    profiles: List<CoachProfileEntity>,
    onDismiss: () -> Unit,
    onAssign: (profileId: Int?) -> Unit
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
                    text = "Assegna a profilo",
                    style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary)
                )
                Text(
                    text = "Scegli il profilo a cui assegnare le schede selezionate",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Option to remove from profile (personal)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAssign(null) },
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Personale",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = accent,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Rimuovi assegnazione profilo",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // List of profiles
                profiles.forEach { profile ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAssign(profile.id) },
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
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(20.dp),
                                color = Color(profile.avatarColor)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = profile.name.take(2).uppercase(),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkCard)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Nessun profilo disponibile",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Text(
                                text = "Crea un profilo nel Coach Mode",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary.copy(alpha = 0.7f)
                            )
                        }
                    }
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SchedaInfoDialog(
    scheda: SchedeEntity,
    onDismiss: () -> Unit
) {
    val muscleGroups = scheda.getAllGruppiMuscolari()
    val accent = Color(0xFF40C4FF)

    val pieData = remember(muscleGroups) {
        muscleGroups.mapIndexed { index, group ->
            Pie(
                label = group,
                data = 100.0 / muscleGroups.size,
                color = getMuscleGroupColor(group),
                selectedColor = getMuscleGroupColor(group).copy(alpha = 0.8f)
            )
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = scheda.titolo,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Chiudi",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Pie Chart
                if (muscleGroups.isNotEmpty()) {
                    Text(
                        text = "Gruppi Muscolari",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PieChart(
                        modifier = Modifier.size(180.dp),
                        data = pieData,
                        selectedScale = 1.1f,
                        spaceDegree = 4f,
                        selectedPaddingDegree = 3f,
                        style = Pie.Style.Stroke(width = 50.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Legend
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        muscleGroups.forEach { group ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(getMuscleGroupColor(group))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = group,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Nessun gruppo muscolare definito",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Info aggiuntive
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Intensita", color = TextSecondary, fontSize = 14.sp)
                            Text(scheda.intesita, color = accent, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Data", color = TextSecondary, fontSize = 14.sp)
                            Text(scheda.data, color = TextPrimary, fontSize = 14.sp)
                        }
                        scheda.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Note:", color = TextSecondary, fontSize = 14.sp)
                            Text(notes, color = TextPrimary, fontSize = 13.sp)
                        }
                    }
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

