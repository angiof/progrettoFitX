package com.app.fityo.ui.coach.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.app.fityo.R
import com.app.fityo.data_layer.db.CoachAppointmentEntity
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentDialog(
    profileId: Int,
    initialDate: String? = null,
    existingAppointment: CoachAppointmentEntity? = null,
    onDismiss: () -> Unit,
    onSave: (CoachAppointmentEntity) -> Unit
) {
    var title by remember { mutableStateOf(existingAppointment?.title ?: "") }
    var notes by remember { mutableStateOf(existingAppointment?.notes ?: "") }
    var selectedDate by remember {
        mutableStateOf(
            existingAppointment?.date
                ?: initialDate
                ?: LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        )
    }
    var selectedTime by remember { mutableStateOf(existingAppointment?.time ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = try {
            LocalDate.parse(selectedDate).toEpochDay() * 24 * 60 * 60 * 1000
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    )

    val timePickerState = rememberTimePickerState(
        initialHour = try {
            if (selectedTime.isNotBlank()) LocalTime.parse(selectedTime).hour else 9
        } catch (e: Exception) { 9 },
        initialMinute = try {
            if (selectedTime.isNotBlank()) LocalTime.parse(selectedTime).minute else 0
        } catch (e: Exception) { 0 }
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(
                        if (existingAppointment == null) R.string.coach_new_appointment
                        else R.string.coach_edit_appointment
                    ),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = false
                    },
                    label = { Text(stringResource(R.string.coach_appointment_title)) },
                    isError = titleError,
                    supportingText = if (titleError) {
                        { Text(stringResource(R.string.app_error_field_empty)) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f),
                        focusedLabelColor = AccentPurple,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                // Date selector
                OutlinedTextField(
                    value = try {
                        LocalDate.parse(selectedDate).format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        )
                    } catch (e: Exception) {
                        selectedDate
                    },
                    onValueChange = {},
                    label = { Text(stringResource(R.string.coach_appointment_date)) },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = AccentPurple
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f),
                        focusedLabelColor = AccentPurple,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Time selector (optional)
                OutlinedTextField(
                    value = selectedTime,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.coach_appointment_time)) },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("HH:mm", color = TextSecondary.copy(alpha = 0.5f)) },
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = AccentPurple
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f),
                        focusedLabelColor = AccentPurple,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Notes field
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.coach_appointment_notes)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f),
                        focusedLabelColor = AccentPurple,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 3
                )

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            stringResource(R.string.exercise_cancel),
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                titleError = true
                                return@Button
                            }

                            val appointment = CoachAppointmentEntity(
                                id = existingAppointment?.id,
                                profileId = profileId,
                                date = selectedDate,
                                time = selectedTime.takeIf { it.isNotBlank() },
                                title = title.trim(),
                                notes = notes.trim().takeIf { it.isNotBlank() },
                                isCompleted = existingAppointment?.isCompleted ?: false,
                                schedeId = existingAppointment?.schedeId,
                                createdAt = existingAppointment?.createdAt ?: System.currentTimeMillis()
                            )
                            onSave(appointment)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.exercise_save))
                    }
                }
            }
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        selectedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = AccentPurple)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.exercise_cancel), color = TextSecondary)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = DarkSurface
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = DarkSurface,
                    titleContentColor = TextPrimary,
                    headlineContentColor = TextPrimary,
                    weekdayContentColor = TextSecondary,
                    dayContentColor = TextPrimary,
                    selectedDayContainerColor = AccentPurple,
                    todayContentColor = AccentPurple,
                    todayDateBorderColor = AccentPurple
                )
            )
        }
    }

    // Time picker dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = DarkSurface,
            title = { Text(stringResource(R.string.coach_appointment_time), color = TextPrimary) },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        containerColor = DarkSurface,
                        clockDialColor = DarkCard,
                        clockDialSelectedContentColor = TextPrimary,
                        clockDialUnselectedContentColor = TextSecondary,
                        selectorColor = AccentPurple,
                        periodSelectorSelectedContainerColor = AccentPurple,
                        timeSelectorSelectedContainerColor = AccentPurple,
                        timeSelectorUnselectedContainerColor = DarkCard
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) {
                    Text("OK", color = AccentPurple)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.exercise_cancel), color = TextSecondary)
                }
            }
        )
    }
}
