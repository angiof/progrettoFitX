package com.app.fityo.ui.schedecreate.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class SchedeFormData(
    val titolo: String = "",
    val data: String = "",
    val intensita: String = "",
    val selectedMuscleGroups: Set<String> = emptySet(),
    val notes: String = ""
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SchedeFormScreen(
    formData: SchedeFormData,
    intensityOptions: List<String>,
    muscleGroupOptions: List<String>,
    onFormDataChanged: (SchedeFormData) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showIntensityDropdown by remember { mutableStateOf(false) }
    var showMuscleGroupsSelector by remember { mutableStateOf(false) }

    val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    // Validazione form
    val isFormValid = formData.titolo.isNotBlank() &&
            formData.data.isNotBlank() &&
            formData.intensita.isNotBlank() &&
            formData.selectedMuscleGroups.isNotEmpty()

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
                    text = "Crea Scheda",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Step Indicator
            StepIndicator(currentStep = 0)

            Spacer(modifier = Modifier.height(24.dp))

            // Form Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkCard)
                    .padding(16.dp)
            ) {
                // Titolo
                Text(
                    text = "Nome Scheda",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = formData.titolo,
                    onValueChange = { onFormDataChanged(formData.copy(titolo = it)) },
                    placeholder = { Text("Es. Push Day, Leg Day...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkSurface,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentBlue
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Data
                Text(
                    text = "Data",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = formData.data,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Seleziona data") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkSurface,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        disabledTextColor = TextPrimary,
                        disabledBorderColor = DarkSurface
                    ),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = "Seleziona data",
                                tint = AccentBlue
                            )
                        }
                    },
                    enabled = false,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Intensita
                Text(
                    text = "Intensita",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = DarkSurface,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
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
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(getIntensityColor(option))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(option, color = TextPrimary)
                                    }
                                },
                                onClick = {
                                    onFormDataChanged(formData.copy(intensita = option))
                                    showIntensityDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Gruppi Muscolari
                Text(
                    text = "Gruppi Muscolari",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Selected groups display
                if (formData.selectedMuscleGroups.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        formData.selectedMuscleGroups.forEach { group ->
                            FilterChip(
                                selected = true,
                                onClick = {
                                    onFormDataChanged(
                                        formData.copy(
                                            selectedMuscleGroups = formData.selectedMuscleGroups - group
                                        )
                                    )
                                },
                                label = { Text(group, fontSize = 12.sp) },
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(getMuscleGroupColor(group))
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = getMuscleGroupColor(group).copy(alpha = 0.2f),
                                    selectedLabelColor = TextPrimary
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Muscle group selector button
                OutlinedTextField(
                    value = if (formData.selectedMuscleGroups.isEmpty())
                        ""
                    else
                        "${formData.selectedMuscleGroups.size} selezionati",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Seleziona gruppi muscolari") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMuscleGroupsSelector = !showMuscleGroupsSelector },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkSurface,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        disabledTextColor = TextPrimary,
                        disabledBorderColor = DarkSurface
                    ),
                    trailingIcon = {
                        Icon(
                            Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = if (formData.selectedMuscleGroups.isNotEmpty())
                                AccentGreen
                            else TextSecondary
                        )
                    },
                    enabled = false,
                    shape = RoundedCornerShape(12.dp)
                )

                // Muscle groups grid
                AnimatedVisibility(visible = showMuscleGroupsSelector) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .padding(12.dp)
                    ) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            muscleGroupOptions.forEach { group ->
                                val isSelected = formData.selectedMuscleGroups.contains(group)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        val newGroups = if (isSelected) {
                                            formData.selectedMuscleGroups - group
                                        } else {
                                            formData.selectedMuscleGroups + group
                                        }
                                        onFormDataChanged(formData.copy(selectedMuscleGroups = newGroups))
                                    },
                                    label = { Text(group, fontSize = 12.sp) },
                                    leadingIcon = if (isSelected) {
                                        {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = DarkCard,
                                        labelColor = TextSecondary,
                                        selectedContainerColor = getMuscleGroupColor(group).copy(alpha = 0.3f),
                                        selectedLabelColor = TextPrimary,
                                        selectedLeadingIconColor = AccentGreen
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Note
                Text(
                    text = "Note (opzionale)",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = formData.notes,
                    onValueChange = { onFormDataChanged(formData.copy(notes = it)) },
                    placeholder = { Text("Accessori, promemoria...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkSurface,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Next Button
            Button(
                onClick = onNext,
                enabled = isFormValid,
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

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = try {
                    if (formData.data.isNotBlank()) {
                        LocalDate.parse(formData.data, isoFormatter)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()
                    } else {
                        System.currentTimeMillis()
                    }
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                onFormDataChanged(formData.copy(data = date.format(isoFormatter)))
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK", color = AccentBlue)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Annulla", color = TextSecondary)
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun StepIndicator(currentStep: Int) {
    val steps = listOf("Dettagli", "Esercizi", "Riepilogo")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            val isCompleted = index < currentStep
            val isActive = index == currentStep
            val stepColor = when {
                isCompleted -> StepCompleted
                isActive -> StepActive
                else -> StepPending
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompleted || isActive) stepColor else Color.Transparent
                        )
                        .border(
                            width = 2.dp,
                            color = stepColor,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            color = if (isActive) Color.White else stepColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step,
                    color = stepColor,
                    fontSize = 11.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }

            // Line between steps
            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .height(2.dp)
                        .background(
                            if (index < currentStep) StepCompleted
                            else DarkSurface
                        )
                )
            }
        }
    }
}
