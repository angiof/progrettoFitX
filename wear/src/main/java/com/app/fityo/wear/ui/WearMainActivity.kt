package com.app.fityo.wear.ui

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.wear.R
import com.app.fityo.wear.sensors.FitnessSensorManager
import com.app.fityo.wear.sensors.HeartRateData
import com.app.fityo.wear.sensors.StepsData
import com.app.fityo.wear.sync.WearSyncClient
import com.app.fityo.wear.sync.WearDataSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WearMainActivity : ComponentActivity() {

    private val viewModel: WearViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.onPermissionsGranted()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Richiedi permessi se necessario
        requestPermissionsIfNeeded()

        setContent {
            FityoWearTheme {
                WearHomeWithTabs(viewModel)
            }
        }
    }

    private fun requestPermissionsIfNeeded() {
        val permissions = arrayOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION
        )

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        } else {
            viewModel.onPermissionsGranted()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onCleared()
    }
}

@Composable
private fun WearHomeWithTabs(viewModel: WearViewModel) {
    val state by viewModel.state.collectAsState()
    val stepsData by viewModel.stepsData.collectAsState()
    val heartRateData by viewModel.heartRateData.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        timeText = { TimeText() }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab Row compatta
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CompactChip(
                    onClick = { selectedTab = 0 },
                    label = stringResource(R.string.tab_workout),
                    selected = selectedTab == 0,
                    modifier = Modifier.weight(1f)
                )
                CompactChip(
                    onClick = { selectedTab = 1 },
                    label = stringResource(R.string.tab_monitor),
                    selected = selectedTab == 1,
                    modifier = Modifier.weight(1f)
                )
            }

            // Content based on selected tab
            when (selectedTab) {
                0 -> WorkoutTab(viewModel, state)
                1 -> MonitorTab(viewModel, stepsData, heartRateData, state.isWorkoutActive)
            }
        }
    }
}

@Composable
private fun CompactChip(
    onClick: () -> Unit,
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Chip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.caption1,
                maxLines = 1
            )
        },
        colors = ChipDefaults.chipColors(
            backgroundColor = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.surface
        ),
        modifier = modifier.height(28.dp)
    )
}

@Composable
private fun WorkoutTab(viewModel: WearViewModel, state: WearUiState) {
    if (state.schede.isEmpty()) {
        EmptyState()
    } else {
        val listState = rememberScalingLazyListState()

        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            state = listState
        ) {
            item {
                Text(
                    text = state.selectedDetail?.scheda?.titolo ?: "",
                    style = MaterialTheme.typography.title3,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            state.selectedDetail?.let { detail ->
                item {
                    CompactCard(
                        primary = detail.scheda.getGruppiMuscolariDisplay(),
                        secondary = detail.scheda.data
                    )
                }

                item {
                    Text(
                        text = stringResource(id = R.string.exercises_title),
                        style = MaterialTheme.typography.caption2.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(detail.exercises.size) { idx ->
                    val exercise = detail.exercises[idx]
                    CompactToggleChip(
                        checked = exercise.completed,
                        onCheckedChange = {
                            exercise.id?.let { id -> viewModel.toggleExercise(id, it) }
                        },
                        label = exercise.nome,
                        secondaryLabel = "${exercise.nSerie}x${exercise.nRipetizione}"
                    )
                }

                item {
                    CompactButton(
                        onClick = {
                            detail.scheda.id?.let { id ->
                                viewModel.completeScheda(id)
                            }
                        },
                        text = if (detail.scheda.completed) {
                            stringResource(id = R.string.completed_label)
                        } else {
                            stringResource(id = R.string.complete_scheda)
                        },
                        enabled = detail.scheda.id != null
                    )
                }
            }

            item {
                Text(
                    text = stringResource(id = R.string.schede_title),
                    style = MaterialTheme.typography.caption2.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            items(state.schede.size) { idx ->
                val detail = state.schede[idx]
                val scheda = detail.scheda
                CompactChipWithSecondary(
                    onClick = { scheda.id?.let { viewModel.selectScheda(it) } },
                    label = scheda.titolo,
                    secondaryLabel = scheda.getGruppiMuscolariDisplay()
                )
            }
        }
    }
}

@Composable
private fun MonitorTab(
    viewModel: WearViewModel,
    stepsData: StepsData,
    heartRateData: HeartRateData,
    isWorkoutActive: Boolean
) {
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        state = listState
    ) {
        if (!isWorkoutActive) {
            item {
                Text(
                    text = stringResource(R.string.start_workout_prompt),
                    style = MaterialTheme.typography.caption1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                )
            }
        } else {
            // Passi - Card compatta
            item {
                Card(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.steps_label),
                            style = MaterialTheme.typography.caption2
                        )
                        if (stepsData.isAvailable) {
                            Text(
                                text = "${stepsData.currentSteps}",
                                style = MaterialTheme.typography.display2,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.sensor_unavailable),
                                style = MaterialTheme.typography.caption2
                            )
                        }
                    }
                }
            }

            // Frequenza cardiaca - Card compatta
            item {
                Card(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.heart_rate_label),
                            style = MaterialTheme.typography.caption2
                        )
                        if (heartRateData.isAvailable) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${heartRateData.currentBpm}",
                                    style = MaterialTheme.typography.display2,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.bpm_unit),
                                    style = MaterialTheme.typography.caption2
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = stringResource(R.string.avg_bpm),
                                        style = MaterialTheme.typography.caption2
                                    )
                                    Text(
                                        text = "${heartRateData.avgBpm}",
                                        style = MaterialTheme.typography.caption1,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = stringResource(R.string.max_bpm),
                                        style = MaterialTheme.typography.caption2
                                    )
                                    Text(
                                        text = "${heartRateData.maxBpm}",
                                        style = MaterialTheme.typography.caption1,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = stringResource(R.string.sensor_unavailable),
                                style = MaterialTheme.typography.caption2
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.no_schede),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center
        )
    }
}

// Componenti compatti per Wear OS
@Composable
private fun CompactCard(
    primary: String,
    secondary: String
) {
    Card(
        onClick = {},
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            Text(
                text = primary,
                style = MaterialTheme.typography.caption1,
                maxLines = 1
            )
            Text(
                text = secondary,
                style = MaterialTheme.typography.caption2,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CompactToggleChip(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    secondaryLabel: String
) {
    ToggleChip(
        checked = checked,
        onCheckedChange = onCheckedChange,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.caption1,
                maxLines = 1
            )
        },
        secondaryLabel = {
            Text(
                text = secondaryLabel,
                style = MaterialTheme.typography.caption2
            )
        },
        toggleControl = {
            Switch(checked = checked, onCheckedChange = null)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    )
}

@Composable
private fun CompactButton(
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.caption1,
            maxLines = 1
        )
    }
}

@Composable
private fun CompactChipWithSecondary(
    onClick: () -> Unit,
    label: String,
    secondaryLabel: String
) {
    Chip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.caption1,
                maxLines = 1
            )
        },
        secondaryLabel = {
            Text(
                text = secondaryLabel,
                style = MaterialTheme.typography.caption2,
                maxLines = 1
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    )
}

@Composable
private fun FityoWearTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}

private fun today(): String =
    LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

data class SchedaDetail(
    val scheda: SchedeEntity,
    val exercises: List<EsserciziEntity>
)

data class WearUiState(
    val schede: List<SchedaDetail> = emptyList(),
    val selectedId: Int? = null,
    val isWorkoutActive: Boolean = false
) {
    val selectedDetail: SchedaDetail? = schede.firstOrNull { it.scheda.id == selectedId }
}

private fun List<SchedaDetail>.firstOrDefault(defaultId: Int?): Int? {
    if (isEmpty()) return null
    if (defaultId != null && any { it.scheda.id == defaultId }) return defaultId
    return first().scheda.id
}

class WearViewModel(application: Application) : AndroidViewModel(application) {

    private val db by lazy { DbFit.getDatabase(application) }
    private val syncClient = WearSyncClient(application)
    private val sensorManager = FitnessSensorManager(application)
    private val dataSyncManager = WearDataSyncManager(application)

    private val _state = MutableStateFlow(WearUiState())
    val state: StateFlow<WearUiState> = _state.asStateFlow()

    val stepsData: StateFlow<StepsData> = sensorManager.stepsData
    val heartRateData: StateFlow<HeartRateData> = sensorManager.heartRateData

    init {
        // Avvia sync iniziale e polling periodico
        viewModelScope.launch {
            dataSyncManager.requestFullSync()
            delay(2000) // Aspetta sync iniziale
            refreshSchede()
        }
        dataSyncManager.startPeriodicSync()
    }

    fun onPermissionsGranted() {
        // Permessi concessi, i sensori saranno attivati quando necessario
    }

    fun selectScheda(id: Int) {
        _state.value = _state.value.copy(selectedId = id, isWorkoutActive = true)
        // Avvia tracking sensori quando selezioniamo una scheda
        sensorManager.startTracking()
    }

    fun toggleExercise(exerciseId: Int, completed: Boolean) {
        viewModelScope.launch {
            val currentDetail = _state.value.schede.firstNotNullOfOrNull { detail ->
                detail.exercises.firstOrNull { it.id == exerciseId }?.let { exercise ->
                    detail to exercise
                }
            } ?: return@launch

            withContext(Dispatchers.IO) {
                val updated = currentDetail.second.copy(completed = completed)
                db.essercissiDao().update(updated)
            }
            currentDetail.second.id?.let { syncClient.sendExerciseCompletion(it, completed) }
            refreshSchede()
        }
    }

    fun completeScheda(schedaId: Int) {
        viewModelScope.launch {
            val date = today()

            // Ottieni dati dai sensori
            val steps = stepsData.value.currentSteps
            val avgBpm = heartRateData.value.avgBpm
            val maxBpm = heartRateData.value.maxBpm

            withContext(Dispatchers.IO) {
                // Aggiorna scheda con dati fitness
                val scheda = db.schedeDao().getSchedeById(schedaId)
                scheda?.let {
                    val updated = it.copy(
                        completed = true,
                        completedDate = date,
                        totalSteps = steps,
                        avgHeartRate = avgBpm,
                        maxHeartRate = maxBpm
                    )
                    db.schedeDao().update(updated)
                }

                // Completa tutti gli esercizi
                _state.value.schede.firstOrNull { it.scheda.id == schedaId }?.exercises?.forEach { ex ->
                    ex.id?.let { db.essercissiDao().update(ex.copy(completed = true)) }
                }
            }

            // Sincronizza con telefono
            syncClient.sendSchedaCompletion(schedaId, true, date, steps, avgBpm, maxBpm)

            // Ferma tracking
            sensorManager.stopTracking()
            _state.value = _state.value.copy(isWorkoutActive = false)

            refreshSchede(selectedId = schedaId)
        }
    }

    private fun refreshSchede(selectedId: Int? = _state.value.selectedId) {
        viewModelScope.launch {
            val details = withContext(Dispatchers.IO) {
                val schede = db.schedeDao().getAllSchede()
                schede.mapNotNull { scheda ->
                    scheda.id?.let { id ->
                        val exercises = db.essercissiDao().getEssercissiById(id)
                        SchedaDetail(scheda, exercises)
                    }
                }
            }
            val activeId = details.firstOrDefault(selectedId)
            _state.value = _state.value.copy(
                schede = details,
                selectedId = activeId
            )
        }
    }

    public override fun onCleared() {
        super.onCleared()
        sensorManager.release()
        dataSyncManager.release()
    }

    // Metodo pubblico per forzare sync manualmente
    fun forceSync() {
        viewModelScope.launch {
            dataSyncManager.requestFullSync()
            delay(2000)
            refreshSchede()
        }
    }
}
