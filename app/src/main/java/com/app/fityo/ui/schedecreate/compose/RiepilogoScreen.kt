package com.app.fityo.ui.schedecreate.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Speed
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RiepilogoScreen(
    formData: SchedeFormData,
    esercizi: List<EsserciziEntity>,
    intensityOptions: List<String>,
    muscleGroupOptions: List<String>,
    profileOptions: List<ProfileOption>,
    onFormDataChanged: (SchedeFormData) -> Unit,
    onSaveAndExit: (reminderTime: String?) -> Unit,
    onExportPdf: () -> Unit,
    onBack: () -> Unit
) {
    var enableReminder by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showIntensityDropdown by remember { mutableStateOf(false) }
    var showProfileSheet by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = 12,
        initialMinute = 0
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                    text = "Riepilogo",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Step Indicator
            StepIndicator(currentStep = 2)

            Spacer(modifier = Modifier.height(24.dp))

            // Success Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = AccentGreen.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AccentGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Scheda pronta!",
                            color = AccentGreen,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Verifica i dettagli e salva",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Restano tutti modificabili: quando si arriva qui da un import i campi
                    // sono spesso vuoti o da correggere, e tornare indietro sarebbe scomodo.
                    FieldLabel("Titolo scheda")
                    OutlinedTextField(
                        value = formData.titolo,
                        onValueChange = { onFormDataChanged(formData.copy(titolo = it)) },
                        placeholder = { Text("Nome della scheda") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = riepilogoFieldColors(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FieldLabel("Data")
                    OutlinedTextField(
                        value = formData.data,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        placeholder = { Text("Seleziona data") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        colors = riepilogoFieldColors(),
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = "Seleziona data",
                                    tint = AccentBlue
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FieldLabel("Intensita")
                    ExposedDropdownMenuBox(
                        expanded = showIntensityDropdown,
                        onExpandedChange = { showIntensityDropdown = it }
                    ) {
                        OutlinedTextField(
                            value = formData.intensita,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Seleziona intensita") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = riepilogoFieldColors(),
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = if (formData.intensita.isNotBlank())
                                        getIntensityColor(formData.intensita)
                                    else TextSecondary
                                )
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = showIntensityDropdown,
                            onDismissRequest = { showIntensityDropdown = false },
                            modifier = Modifier.background(DarkSurface)
                        ) {
                            intensityOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = TextPrimary) },
                                    onClick = {
                                        onFormDataChanged(formData.copy(intensita = option))
                                        showIntensityDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FieldLabel("Gruppi Muscolari")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        muscleGroupOptions.forEach { group ->
                            FilterChip(
                                selected = group in formData.selectedMuscleGroups,
                                onClick = {
                                    val updated = formData.selectedMuscleGroups.toMutableSet()
                                    if (!updated.add(group)) updated.remove(group)
                                    onFormDataChanged(formData.copy(selectedMuscleGroups = updated))
                                },
                                label = {
                                    Text(
                                        text = group,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = getMuscleGroupColor(group).copy(alpha = 0.2f),
                                    selectedLabelColor = TextPrimary,
                                    labelColor = TextSecondary
                                ),
                                modifier = Modifier.height(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = DarkSurface)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Exercises count
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Totale Esercizi",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentBlue.copy(alpha = 0.2f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${esercizi.size}",
                                color = AccentBlue,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarkSurface)
                    Spacer(modifier = Modifier.height(16.dp))

                    FieldLabel("Note")
                    OutlinedTextField(
                        value = formData.notes,
                        onValueChange = { onFormDataChanged(formData.copy(notes = it)) },
                        placeholder = { Text("Indicazioni per l'allenamento") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = riepilogoFieldColors(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Muscle Balance Chart
            if (formData.selectedMuscleGroups.isNotEmpty()) {
                MuscleBalanceCard(muscleGroups = formData.selectedMuscleGroups.toList())
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Exercises Preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Esercizi",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    esercizi.forEachIndexed { index, esercizio ->
                        if (index > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(AccentBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = AccentBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = esercizio.nome,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = buildString {
                                        append("${esercizio.nSerie} x ${esercizio.nRipetizione}")
                                        if (esercizio.attrezzo.isNotBlank()) {
                                            append(" • ${esercizio.attrezzo}")
                                        }
                                        esercizio.peso?.let { append(" • ${formatPeso(it)}") }
                                    },
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Reminder Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                tint = AccentYellow,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Promemoria",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Ricevi una notifica",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Switch(
                            checked = enableReminder,
                            onCheckedChange = {
                                enableReminder = it
                                if (it) showTimePicker = true
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AccentGreen,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = DarkSurface
                            )
                        )
                    }

                    if (enableReminder) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurface)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = String.format(
                                    Locale.getDefault(),
                                    "%02d:%02d",
                                    timePickerState.hour,
                                    timePickerState.minute
                                ),
                                color = TextPrimary,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (showTimePicker) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TimePicker(
                                    state = timePickerState,
                                    colors = TimePickerDefaults.colors(
                                        clockDialColor = DarkSurface,
                                        selectorColor = AccentBlue,
                                        containerColor = DarkCard,
                                        periodSelectorSelectedContainerColor = AccentBlue,
                                        periodSelectorUnselectedContainerColor = DarkSurface,
                                        periodSelectorSelectedContentColor = Color.White,
                                        periodSelectorUnselectedContentColor = TextSecondary,
                                        timeSelectorSelectedContainerColor = AccentBlue,
                                        timeSelectorUnselectedContainerColor = DarkSurface,
                                        timeSelectorSelectedContentColor = Color.White,
                                        timeSelectorUnselectedContentColor = TextSecondary
                                    )
                                )
                                TextButton(onClick = { showTimePicker = false }) {
                                    Text("Conferma orario", color = AccentBlue)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    val reminderTime = if (enableReminder) {
                        String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            timePickerState.hour,
                            timePickerState.minute
                        )
                    } else null
                    onSaveAndExit(reminderTime)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Salva ed Esci",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onExportPdf,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                border = BorderStroke(1.dp, AccentBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Esporta PDF",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Il pulsante prende il colore del profilo scelto, cosi si vede a colpo d'occhio
            // a chi finira la scheda.
            val assignedProfile = profileOptions.firstOrNull { it.id == formData.coachProfileId }
            val profileColor = assignedProfile?.let { Color(it.avatarColor) } ?: TextSecondary

            OutlinedButton(
                onClick = { showProfileSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                border = BorderStroke(1.dp, profileColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = profileColor,
                    containerColor = profileColor.copy(alpha = 0.12f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = assignedProfile?.let { "Assegnata a ${it.name}" }
                        ?: "Assegna a un profilo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showDatePicker) {
            SchedaDatePickerDialog(
                currentDate = formData.data,
                onDismiss = { showDatePicker = false },
                onDateSelected = {
                    onFormDataChanged(formData.copy(data = it))
                    showDatePicker = false
                }
            )
        }

        if (showProfileSheet) {
            ProfilePickerDialog(
                profileOptions = profileOptions,
                selectedId = formData.coachProfileId,
                onDismiss = { showProfileSheet = false },
                onSelect = { id ->
                    onFormDataChanged(formData.copy(coachProfileId = id))
                    showProfileSheet = false
                }
            )
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = TextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun riepilogoFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AccentBlue,
    unfocusedBorderColor = DarkSurface,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    disabledTextColor = TextPrimary,
    disabledBorderColor = DarkSurface,
    disabledPlaceholderColor = TextSecondary
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SchedaDatePickerDialog(
    currentDate: String,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    // Come nel form: niente date passate, e la soglia va in UTC perche in UTC ragiona DatePicker.
    val isoFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val todayUtcMillis = remember {
        LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = try {
            if (currentDate.isNotBlank()) {
                LocalDate.parse(currentDate, isoFormatter)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant()
                    .toEpochMilli()
                    .coerceAtLeast(todayUtcMillis)
            } else {
                todayUtcMillis
            }
        } catch (e: Exception) {
            todayUtcMillis
        },
        yearRange = LocalDate.now().year..(LocalDate.now().year + 5),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis >= todayUtcMillis

            override fun isSelectableYear(year: Int): Boolean =
                year >= LocalDate.now().year
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        onDateSelected(date.format(isoFormatter))
                    }
                }
            ) {
                Text("OK", color = AccentBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla", color = TextSecondary)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
private fun ProfilePickerDialog(
    profileOptions: List<ProfileOption>,
    selectedId: Int?,
    onDismiss: () -> Unit,
    onSelect: (Int?) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = {
            Text(
                text = "Assegna la scheda",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ProfileRow(
                    name = "Scheda personale",
                    color = TextSecondary,
                    selected = selectedId == null,
                    onClick = { onSelect(null) }
                )
                profileOptions.forEach { profile ->
                    ProfileRow(
                        name = profile.name,
                        color = Color(profile.avatarColor),
                        selected = profile.id == selectedId,
                        onClick = { onSelect(profile.id) }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun ProfileRow(
    name: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) color.copy(alpha = 0.15f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = name,
            color = TextPrimary,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = color)
        }
    }
}

@Composable
private fun SummaryRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 12.sp
            )
            Text(
                text = value,
                color = valueColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MuscleBalanceCard(muscleGroups: List<String>) {
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bilanciamento Muscolare",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pie Chart
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
        }
    }
}
