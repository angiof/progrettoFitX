package com.app.fityo.ui.schedecreate.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.fityo.data_layer.network.WgerClient
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.repository.WgerRepository
import com.app.fityo.dominio.WgerSuggestion
import com.app.fityo.ui.wger.WgerExerciseInfoDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

data class EsercizioFormData(
    val id: Int? = null,
    val nome: String = "",
    val attrezzo: String = "",
    val nSerie: Int = 3,
    val nRipetizioni: Int = 10,
    val isometria: Int? = null,
    val intervallo: Int? = null,
    val peso: Float? = null,
    val wgerId: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EsercissiListScreen(
    esercizi: List<EsserciziEntity>,
    equipmentOptions: List<String>,
    onAddEsercizio: (EsercizioFormData) -> Unit,
    onEditEsercizio: (EsercizioFormData) -> Unit,
    onDeleteEsercizio: (EsserciziEntity) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var showAddSheet by remember { mutableStateOf(false) }
    var editingEsercizio by remember { mutableStateOf<EsserciziEntity?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Indietro",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Esercizi",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${esercizi.size} esercizi",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Step Indicator
            StepIndicator(currentStep = 1)

            Spacer(modifier = Modifier.height(16.dp))

            // Exercises List
            if (esercizi.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nessun esercizio",
                            color = TextSecondary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Premi + per aggiungere esercizi",
                            color = TextSecondary.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        items = esercizi,
                        key = { _, item -> item.id ?: item.hashCode() }
                    ) { index, esercizio ->
                        SwipeableEsercizioCard(
                            esercizio = esercizio,
                            index = index,
                            onEdit = {
                                editingEsercizio = esercizio
                                showAddSheet = true
                            },
                            onDelete = { onDeleteEsercizio(esercizio) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }

            // Next Button
            Button(
                onClick = onNext,
                enabled = esercizi.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    disabledContainerColor = DarkSurface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Avanti",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
            }
        }

        // FAB
        FloatingActionButton(
            onClick = {
                editingEsercizio = null
                showAddSheet = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp),
            containerColor = AccentGreen,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Aggiungi esercizio")
        }

        // Add/Edit Sheet
        if (showAddSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                        showAddSheet = false
                        editingEsercizio = null
                    }
                },
                sheetState = sheetState,
                containerColor = DarkCard,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(TextSecondary.copy(alpha = 0.5f))
                    )
                }
            ) {
                EsercizioFormSheet(
                    initialData = editingEsercizio?.let {
                        EsercizioFormData(
                            id = it.id,
                            nome = it.nome,
                            attrezzo = it.attrezzo,
                            nSerie = it.nSerie,
                            nRipetizioni = it.nRipetizione,
                            isometria = it.insometria,
                            intervallo = it.intervallo,
                            peso = it.peso,
                            wgerId = it.wgerId
                        )
                    },
                    equipmentOptions = equipmentOptions,
                    onSave = { formData ->
                        if (editingEsercizio != null) {
                            onEditEsercizio(formData)
                        } else {
                            onAddEsercizio(formData)
                        }
                        scope.launch {
                            sheetState.hide()
                            showAddSheet = false
                            editingEsercizio = null
                        }
                    },
                    onCancel = {
                        scope.launch {
                            sheetState.hide()
                            showAddSheet = false
                            editingEsercizio = null
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableEsercizioCard(
    esercizio: EsserciziEntity,
    index: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> AccentRed
                    else -> Color.Transparent
                },
                label = "swipe_color"
            )
            val scale by animateFloatAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1f else 0.8f,
                label = "swipe_scale"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Elimina",
                    tint = Color.White,
                    modifier = Modifier.scale(scale)
                )
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        EsercizioCard(
            esercizio = esercizio,
            index = index,
            onEdit = onEdit
        )
    }
}

@Composable
private fun EsercizioCard(
    esercizio: EsserciziEntity,
    index: Int,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Index badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AccentBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    color = AccentBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = esercizio.nome,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Serie x Ripetizioni
                    Text(
                        text = "${esercizio.nSerie} x ${esercizio.nRipetizione}",
                        color = AccentGreen,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )

                    if (esercizio.attrezzo.isNotBlank()) {
                        Text(
                            text = " • ",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = esercizio.attrezzo,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }

                    esercizio.peso?.let { peso ->
                        Text(
                            text = " • ",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${peso}kg",
                            color = AccentOrange,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }

                // Optional fields
                val extras = mutableListOf<String>()
                esercizio.insometria?.let { if (it > 0) extras.add("Iso: ${formatDuration(it)}") }
                esercizio.intervallo?.let { if (it > 0) extras.add("Rec: ${formatDuration(it)}") }

                if (extras.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = extras.joinToString(" • "),
                            color = TextSecondary.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Modifica",
                    tint = TextSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EsercizioFormSheet(
    initialData: EsercizioFormData?,
    equipmentOptions: List<String>,
    onSave: (EsercizioFormData) -> Unit,
    onCancel: () -> Unit
) {
    var formData by remember {
        mutableStateOf(initialData ?: EsercizioFormData())
    }
    var showEquipmentDropdown by remember { mutableStateOf(false) }
    var showIsometriaPicker by remember { mutableStateOf(false) }
    var showRecuperoPicker by remember { mutableStateOf(false) }
    val wgerRepository = remember { WgerRepository(WgerClient.service) }
    var wgerSuggestionsEnabled by remember { mutableStateOf(false) }
    var wgerSuggestions by remember { mutableStateOf<List<WgerSuggestion>>(emptyList()) }
    var wgerSearchError by remember { mutableStateOf<String?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var pendingSuggestion by remember { mutableStateOf<WgerSuggestion?>(null) }
    var showWgerDialog by remember { mutableStateOf(false) }

    val isValid = formData.nome.isNotBlank() && formData.nSerie > 0 && formData.nRipetizioni > 0

    androidx.compose.runtime.LaunchedEffect(wgerSuggestionsEnabled, formData.nome) {
        if (!wgerSuggestionsEnabled) {
            wgerSuggestions = emptyList()
            wgerSearchError = null
            isSearching = false
            return@LaunchedEffect
        }
        val query = formData.nome.trim()
        if (query.length < 2) {
            wgerSuggestions = emptyList()
            wgerSearchError = null
            isSearching = false
            return@LaunchedEffect
        }
        isSearching = true
        delay(350)
        val result = wgerRepository.searchExercises(query)
        result.onSuccess {
            wgerSuggestions = it
            wgerSearchError = null
        }.onFailure {
            wgerSuggestions = emptyList()
            wgerSearchError = "Errore di rete."
        }
        isSearching = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (initialData != null) "Modifica Esercizio" else "Nuovo Esercizio",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onCancel) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Chiudi",
                    tint = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Nome
        OutlinedTextField(
            value = formData.nome,
            onValueChange = { formData = formData.copy(nome = it) },
            label = { Text("Nome esercizio") },
            placeholder = { Text("Es. Panca piana, Squat...") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                IconButton(
                    onClick = {
                        val newState = !wgerSuggestionsEnabled
                        wgerSuggestionsEnabled = newState
                        if (!newState) {
                            wgerSuggestions = emptyList()
                            wgerSearchError = null
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (wgerSuggestionsEnabled) Icons.Filled.Info else Icons.Outlined.Info,
                        contentDescription = "Suggerimenti Wger",
                        tint = if (wgerSuggestionsEnabled) AccentBlue else TextSecondary
                    )
                }
            }
        )

        if (wgerSuggestionsEnabled) {
            Spacer(modifier = Modifier.height(8.dp))
            WgerSuggestionsDropdown(
                query = formData.nome,
                isSearching = isSearching,
                errorMessage = wgerSearchError,
                suggestions = wgerSuggestions,
                onSuggestionClick = { suggestion ->
                    pendingSuggestion = suggestion
                    showWgerDialog = true
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Serie e Ripetizioni
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = if (formData.nSerie > 0) formData.nSerie.toString() else "",
                onValueChange = {
                    formData = formData.copy(nSerie = it.toIntOrNull() ?: 0)
                },
                label = { Text("Serie") },
                modifier = Modifier.weight(1f),
                colors = textFieldColors(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = if (formData.nRipetizioni > 0) formData.nRipetizioni.toString() else "",
                onValueChange = {
                    formData = formData.copy(nRipetizioni = it.toIntOrNull() ?: 0)
                },
                label = { Text("Ripetizioni") },
                modifier = Modifier.weight(1f),
                colors = textFieldColors(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Attrezzo
        ExposedDropdownMenuBox(
            expanded = showEquipmentDropdown,
            onExpandedChange = { showEquipmentDropdown = it }
        ) {
            OutlinedTextField(
                value = formData.attrezzo,
                onValueChange = { formData = formData.copy(attrezzo = it) },
                label = { Text("Attrezzo") },
                placeholder = { Text("Seleziona o digita") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = textFieldColors(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = showEquipmentDropdown,
                onDismissRequest = { showEquipmentDropdown = false },
                modifier = Modifier.background(DarkSurface)
            ) {
                equipmentOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = TextPrimary) },
                        onClick = {
                            formData = formData.copy(attrezzo = option)
                            showEquipmentDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Peso
        OutlinedTextField(
            value = formData.peso?.toString() ?: "",
            onValueChange = {
                formData = formData.copy(peso = it.toFloatOrNull())
            },
            label = { Text("Peso (kg)") },
            placeholder = { Text("Opzionale") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Isometria e Recupero
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = formatDuration(formData.isometria),
                onValueChange = {},
                label = { Text("Isometria") },
                placeholder = { Text("00:00") },
                modifier = Modifier
                    .weight(1f)
                    .clickable { showIsometriaPicker = true },
                colors = textFieldColors(),
                readOnly = true,
                enabled = false,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = formatDuration(formData.intervallo),
                onValueChange = {},
                label = { Text("Recupero") },
                placeholder = { Text("00:00") },
                modifier = Modifier
                    .weight(1f)
                    .clickable { showRecuperoPicker = true },
                colors = textFieldColors(),
                readOnly = true,
                enabled = false,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Duration pickers
        AnimatedVisibility(
            visible = showIsometriaPicker,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            DurationPicker(
                title = "Isometria",
                initialSeconds = formData.isometria ?: 0,
                onValueChange = {
                    formData = formData.copy(isometria = if (it > 0) it else null)
                },
                onDismiss = { showIsometriaPicker = false }
            )
        }

        AnimatedVisibility(
            visible = showRecuperoPicker,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            DurationPicker(
                title = "Recupero",
                initialSeconds = formData.intervallo ?: 0,
                onValueChange = {
                    formData = formData.copy(intervallo = if (it > 0) it else null)
                },
                onDismiss = { showRecuperoPicker = false }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save Button
        Button(
            onClick = { onSave(formData) },
            enabled = isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentGreen,
                disabledContainerColor = DarkSurface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (initialData != null) "Salva Modifiche" else "Aggiungi Esercizio",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }

    if (showWgerDialog && pendingSuggestion != null) {
        val suggestion = pendingSuggestion!!
        WgerExerciseInfoDialog(
            exerciseId = suggestion.id,
            onDismiss = {
                showWgerDialog = false
                pendingSuggestion = null
            },
            onConfirm = {
                formData = formData.copy(
                    nome = suggestion.value,
                    wgerId = suggestion.id
                )
                wgerSuggestions = emptyList()
                showWgerDialog = false
                pendingSuggestion = null
            }
        )
    }
}

@Composable
private fun DurationPicker(
    title: String,
    initialSeconds: Int,
    onValueChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var minutes by remember { mutableStateOf(initialSeconds / 60) }
    var seconds by remember { mutableStateOf(initialSeconds % 60) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Minutes
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { if (minutes < 59) minutes++ }) {
                    Text("+", color = AccentBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = String.format(Locale.getDefault(), "%02d", minutes),
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("min", color = TextSecondary, fontSize = 12.sp)
                IconButton(onClick = { if (minutes > 0) minutes-- }) {
                    Text("-", color = AccentBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = ":",
                color = TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Seconds
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { if (seconds < 59) seconds++ else { seconds = 0; if (minutes < 59) minutes++ } }) {
                    Text("+", color = AccentBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = String.format(Locale.getDefault(), "%02d", seconds),
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("sec", color = TextSecondary, fontSize = 12.sp)
                IconButton(onClick = { if (seconds > 0) seconds-- else if (minutes > 0) { minutes--; seconds = 59 } }) {
                    Text("-", color = AccentBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismiss) {
                Text("Annulla", color = TextSecondary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(
                onClick = {
                    onValueChange(minutes * 60 + seconds)
                    onDismiss()
                }
            ) {
                Text("OK", color = AccentBlue, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AccentBlue,
    unfocusedBorderColor = DarkSurface,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    disabledTextColor = TextPrimary,
    disabledBorderColor = DarkSurface,
    cursorColor = AccentBlue,
    focusedLabelColor = AccentBlue,
    unfocusedLabelColor = TextSecondary,
    disabledLabelColor = TextSecondary
)

private fun formatDuration(value: Int?): String {
    if (value == null || value <= 0) return ""
    val minutes = value / 60
    val seconds = value % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

@Composable
private fun WgerSuggestionsDropdown(
    query: String,
    isSearching: Boolean,
    errorMessage: String?,
    suggestions: List<WgerSuggestion>,
    onSuggestionClick: (WgerSuggestion) -> Unit
) {
    val trimmedQuery = query.trim()
    if (trimmedQuery.length < 2) {
        return
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        when {
            isSearching -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = AccentBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Ricerca in corso...", color = TextSecondary, fontSize = 12.sp)
                }
            }
            errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = errorMessage, color = TextSecondary, fontSize = 12.sp)
                }
            }
            suggestions.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Nessun risultato", color = TextSecondary, fontSize = 12.sp)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    itemsIndexed(suggestions) { _, suggestion ->
                        WgerSuggestionRow(
                            suggestion = suggestion,
                            onClick = { onSuggestionClick(suggestion) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WgerSuggestionRow(
    suggestion: WgerSuggestion,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val imageUrl = suggestion.imageUrl
        if (imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = suggestion.value,
                modifier = Modifier
                    .size(40.dp)
                    .background(DarkBackground, CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = suggestion.value,
                color = TextPrimary,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            suggestion.category?.let {
                Text(
                    text = it,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = AccentBlue,
            modifier = Modifier.size(18.dp)
        )
    }
}
