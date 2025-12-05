package com.app.progrettofitx.ui.compose

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.contentValuesOf
import com.app.progrettofitx.R
import com.app.progrettofitx.data_layer.db.DB.DbFit
import com.app.progrettofitx.data_layer.db.EsserciziEntity
import com.app.progrettofitx.data_layer.db.SchedeEntity
import com.app.progrettofitx.data_layer.db.repos.EsserciziRepository
import com.app.progrettofitx.dominio.UsesCasesEssercissi
import com.app.progrettofitx.fragments.filtro.ShedeFragments
import com.app.progrettofitx.ui.factory.GenericViewModelFactory
import com.app.progrettofitx.ui.forms.AcitivySheda
import com.app.progrettofitx.ui.shedeForms.EsserciziViewModel
import com.app.progrettofitx.ui.shedeForms.SchedeListViewModel
import com.app.progrettofitx.utils.ExportMetadata
import com.app.progrettofitx.utils.PdfExporter
import com.app.progrettofitx.utils.ShareUtils
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        schedeViewModel.loadSchede()
    }
}

@Composable
private fun SchedeListRoute(
    viewModel: SchedeListViewModel,
    eserciziViewModel: EsserciziViewModel,
    onBack: () -> Unit,
    onOpenScheda: (SchedeEntity) -> Unit,
    onCreateScheda: () -> Unit
) {
    val schede by viewModel.schede.observeAsState(emptyList())
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedScheda by remember { mutableStateOf<SchedeEntity?>(null) }

    SchedeListScreen(
        schede = schede,
        onBack = onBack,
        onCreateNew = onCreateScheda,
        onItemClick = { selectedScheda = it },
        onToggleFavorite = { scheda, newValue ->
            scheda.id?.let { viewModel.toggleFavorite(it, newValue) }
        }
    )

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
    onExportPdf: (ExportMetadata) -> Unit,
    onAddExercise: (EsserciziEntity) -> Unit,
    onUpdateExercise: (EsserciziEntity) -> Unit,
    onDeleteExercise: (EsserciziEntity) -> Unit,
    onSharePdf: (ExportMetadata) -> Unit
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
                            Icon(Icons.Filled.Close, contentDescription = "Chiudi", tint = Color.White)
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
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = scheda.gruppoMuscolare,
                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Intensità: ${scheda.intesita}", color = TextSecondary)
                    Text(text = "Data: ${scheda.data}", color = TextSecondary)
                    scheda.notes?.takeIf { it.isNotBlank() }?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it, color = TextPrimary)
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
                                onDelete = { onDeleteExercise(esercizio) }
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
                        Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Elimina scheda", color = Color.Red)
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
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = esercizio.nome,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Modifica")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Elimina")
                }
            }
            Text(text = "Serie x Rep: ${esercizio.nSerie} x ${esercizio.nRipetizione}", color = TextSecondary)
            if (esercizio.attrezzo.isNotBlank()) {
                Text(text = "Attrezzo: ${esercizio.attrezzo}", color = TextSecondary)
            }
            esercizio.insometria?.let { Text(text = "Isometria: ${it}s", color = TextSecondary) }
            esercizio.intervallo?.let { Text(text = "Recupero: ${it}s", color = TextSecondary) }
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            }
        },
        confirmButton = {
            TextButton(
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
                enabled = isValid
            ) {
                Text(text = stringResource(id = R.string.exercise_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.exercise_cancel))
            }
        }
    )
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
                                    endDate = endDate.trim()
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
    onBack: () -> Unit,
    onCreateNew: () -> Unit,
    onItemClick: (SchedeEntity) -> Unit,
    onToggleFavorite: (SchedeEntity, Boolean) -> Unit
) {
    val primary = Color(0xFF455A64)
    Scaffold(
        containerColor = DarkBackground,
        topBar = {
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNew) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.add_scheda_content_description)
                )
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
            Text(text = "Intensità: ${scheda.intesita}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text(text = "Data: ${scheda.data}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            if (!scheda.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = scheda.notes, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
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
