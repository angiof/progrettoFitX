package com.app.fityo.ui.schedecreate.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.ui.schedecreate.Progressione
import java.util.Locale

/**
 * Secondo passo della creazione scheda: la lista degli esercizi del giorno aperto.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EsercissiListScreen(
    esercizi: List<EsserciziEntity>,
    equipmentOptions: List<String>,
    onAddEsercizio: (EsercizioFormData) -> Unit,
    onEditEsercizio: (EsercizioFormData) -> Unit,
    onDeleteEsercizio: (EsserciziEntity) -> Unit,
    onSaveAttrezzo: (String) -> Unit,
    onSaveNomeComeEsercizio: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    advancedMode: Boolean = false,
    canDisableAdvanced: Boolean = true,
    weekCount: Int = 1,
    dayCount: Int = 1,
    selectedWeek: Int = 1,
    selectedDay: Int = 1,
    onToggleAdvanced: (Boolean) -> Unit = {},
    onSelectWeek: (Int) -> Unit = {},
    onSelectDay: (Int) -> Unit = {},
    onAddWeek: () -> Unit = {},
    onAddDay: () -> Unit = {},
    onDeleteWeek: (Int) -> Unit = {},
    onDeleteDay: (Int) -> Unit = {},
    onProgressione: (Progressione) -> Unit = {},
    totalEsercizi: Int = esercizi.size
) {
    var showAddSheet by remember { mutableStateOf(false) }
    var editingEsercizio by remember { mutableStateOf<EsserciziEntity?>(null) }
    var editingLinked by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf(-1) }

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
                    // In avanzata la lista mostra un giorno solo: qui diciamo il totale della
                    // scheda, altrimenti "2 esercizi" sembrerebbe tutto quello che c'e.
                    text = if (advancedMode) "$totalEsercizi in totale"
                    else "${esercizi.size} esercizi",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Step Indicator
            StepIndicator(currentStep = 1)

            Spacer(modifier = Modifier.height(12.dp))

            if (advancedMode) {
                StructureBar(
                    canDisableAdvanced = canDisableAdvanced,
                    weekCount = weekCount,
                    dayCount = dayCount,
                    selectedWeek = selectedWeek,
                    selectedDay = selectedDay,
                    onToggleAdvanced = onToggleAdvanced,
                    onSelectWeek = onSelectWeek,
                    onSelectDay = onSelectDay,
                    onAddWeek = onAddWeek,
                    onAddDay = onAddDay,
                    onDeleteWeek = onDeleteWeek,
                    onDeleteDay = onDeleteDay,
                    onProgressione = onProgressione
                )
                Spacer(modifier = Modifier.height(14.dp))
                DayHeader(
                    settimana = selectedWeek,
                    giorno = selectedDay,
                    conteggio = esercizi.size
                )
                Spacer(modifier = Modifier.height(10.dp))
            } else {
                SimpleModeRow(onToggleAdvanced = onToggleAdvanced)
                Spacer(modifier = Modifier.height(16.dp))
            }

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
                            text = if (advancedMode) "Giorno $selectedDay vuoto"
                            else "Nessun esercizio",
                            color = TextSecondary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (advancedMode)
                                "Premi + per aggiungere alla Settimana $selectedWeek"
                            else "Premi + per aggiungere esercizi",
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
                        // Il superset e "questo esercizio ha lo stesso gruppo del precedente":
                        // il primo della coppia mostra l'etichetta, gli altri solo la barra.
                        val group = esercizio.supersetGroup
                        val previousGroup = esercizi.getOrNull(index - 1)?.supersetGroup
                        val linkedToPrevious = group != null && group == previousGroup

                        SwipeableEsercizioCard(
                            esercizio = esercizio,
                            index = index,
                            mostraCollocazione = advancedMode,
                            inSuperset = group != null,
                            supersetStart = group != null && !linkedToPrevious,
                            onEdit = {
                                editingEsercizio = esercizio
                                editingLinked = linkedToPrevious
                                editingIndex = index
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
                editingLinked = false
                editingIndex = esercizi.size
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
            EsercizioEditorSheet(
                initialData = editingEsercizio?.toFormData(editingLinked),
                equipmentOptions = equipmentOptions,
                canLinkPrevious = editingIndex > 0,
                onSaveAttrezzo = onSaveAttrezzo,
                onSaveNomeComeEsercizio = onSaveNomeComeEsercizio,
                onSave = { formData ->
                    if (editingEsercizio != null) {
                        onEditEsercizio(formData)
                    } else {
                        onAddEsercizio(formData)
                    }
                },
                onDismiss = {
                    showAddSheet = false
                    editingEsercizio = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableEsercizioCard(
    esercizio: EsserciziEntity,
    index: Int,
    mostraCollocazione: Boolean,
    inSuperset: Boolean,
    supersetStart: Boolean,
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
            mostraCollocazione = mostraCollocazione,
            inSuperset = inSuperset,
            supersetStart = supersetStart,
            onEdit = onEdit
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EsercizioCard(
    esercizio: EsserciziEntity,
    index: Int,
    mostraCollocazione: Boolean = false,
    inSuperset: Boolean = false,
    supersetStart: Boolean = false,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (inSuperset && supersetStart) {
            Text(
                text = "SUPERSET",
                color = AccentOrange,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 10.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Barra laterale: lega visivamente gli esercizi dello stesso superset.
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(if (inSuperset) AccentOrange else Color.Transparent)
            )
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
                // Scorrendo la lista il contesto della barra non si vede piu: ogni esercizio
                // dice a quale settimana e giorno appartiene.
                if (mostraCollocazione) {
                    Text(
                        text = "S${esercizio.settimana} - G${esercizio.giorno}",
                        color = AccentBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AccentBlue.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = esercizio.nome,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Nomi di attrezzo lunghi spingevano il peso fuori dalla card: qui le voci
                // vanno a capo invece di essere tagliate.
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "${esercizio.nSerie} x ${esercizio.nRipetizione}",
                        color = AccentGreen,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )

                    if (esercizio.attrezzo.isNotBlank()) {
                        Text(
                            text = "• ${esercizio.attrezzo}",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }

                    esercizio.peso?.let { peso ->
                        Text(
                            text = "• ${formatPeso(peso)}",
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
                esercizio.rpe?.takeIf { it.isNotBlank() }?.let { extras.add("RPE $it") }
                esercizio.tempo?.takeIf { it.isNotBlank() }?.let { extras.add("Tempo $it") }
                esercizio.percentuale?.let { extras.add("${formatPercentuale(it)} 1RM") }

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
}

/** Un peso intero si scrive "20 kg", non "20.0 kg". */
internal fun formatPeso(value: Float?): String {
    if (value == null) return ""
    val text = if (value % 1f == 0f) {
        value.toInt().toString()
    } else {
        String.format(Locale.getDefault(), "%.1f", value)
    }
    return "$text kg"
}

private fun formatPercentuale(value: Float): String {
    val text = if (value % 1f == 0f) value.toInt().toString()
    else String.format(Locale.getDefault(), "%.1f", value)
    return "$text%"
}

internal fun formatDuration(value: Int?): String {
    if (value == null || value <= 0) return ""
    val minutes = value / 60
    val seconds = value % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
